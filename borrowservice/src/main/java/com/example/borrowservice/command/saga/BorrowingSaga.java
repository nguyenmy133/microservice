package com.example.borrowservice.command.saga;

import com.example.borrowservice.command.command.DeleteBorrowingCommand;
import com.example.borrowservice.command.event.BorrowingCreatedEvent;
import com.example.commonservice.command.BookUpdateStatusCommand;
import com.example.commonservice.event.BookUpdateStatusEvent;
import com.example.commonservice.model.BookResponseCommonModel;
import com.example.commonservice.model.EmployeeResponseCommonModel;
import com.example.commonservice.queries.GetBookDetailQuery;
import com.example.commonservice.queries.GetDetailsEmployeeQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

@Saga
@Slf4j
@ProcessingGroup("borrowing-saga")  // Isolate vào processor group riêng
public class BorrowingSaga {

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient CommandGateway commandGateway;

    /**
     * Bước 1: Saga khởi động khi có BorrowingCreatedEvent.
     *
     * QUAN TRỌNG: Mọi exception phải được bắt hoàn toàn bên trong handler này.
     * Nếu exception thoát ra ngoài @SagaEventHandler → TrackingEventProcessor
     * sẽ release token claim và retry sau 60s → vòng lặp vô tận.
     */
    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    private void handle(BorrowingCreatedEvent event) {
        log.info("BorrowingCreatedEvent in saga - borrowingId: {}, bookId: {}, employeeId: {}",
                event.getId(), event.getBookId(), event.getEmployeeId());
        try {
            // 1. Kiểm tra thông tin Nhân viên từ EmployeeService
            GetDetailsEmployeeQuery getEmployeeQuery = new GetDetailsEmployeeQuery(event.getEmployeeId());
            EmployeeResponseCommonModel employee = queryGateway
                    .query(getEmployeeQuery, ResponseTypes.instanceOf(EmployeeResponseCommonModel.class))
                    .get(30, TimeUnit.SECONDS);

            if (employee == null) {
                log.error("Saga từ chối: Không tìm thấy nhân viên employeeId={}", event.getEmployeeId());
                rollbackBorrowingRecord(event.getId());
                return;
            }

            if (Boolean.TRUE.equals(employee.getIsDescription())) {
                log.warn("Saga từ chối: Nhân viên employeeId={} đang bị kỷ luật (isDescription=true), rollback borrowingId={}",
                        event.getEmployeeId(), event.getId());
                rollbackBorrowingRecord(event.getId());
                return;
            }

            // 2. Query trạng thái sách (best-effort check, guard cuối cùng ở BookAggregate)
            GetBookDetailQuery getBookDetailQuery = new GetBookDetailQuery(event.getBookId());
            BookResponseCommonModel book = queryGateway
                    .query(getBookDetailQuery, ResponseTypes.instanceOf(BookResponseCommonModel.class))
                    .get(30, TimeUnit.SECONDS);  // timeout rõ ràng thay vì .join() block mãi

            if (book == null) {
                log.error("Saga từ chối: Không tìm thấy sách bookId={}", event.getBookId());
                rollbackBorrowingRecord(event.getId());
                return;
            }

            Boolean isReady = book.getIsReady();
            if (Boolean.FALSE.equals(isReady)) {
                log.warn("Saga từ chối: Sách '{}' đã có người mượn (isReady=false), rollback borrowingId={}",
                        event.getBookId(), event.getId());
                rollbackBorrowingRecord(event.getId());
                return;
            }

            // ====== FIX: Đăng ký association "borrowingId" trước khi gửi command ======
            // Khi BookUpdateStatusEvent đến với borrowingId=X, Axon tìm saga có key="borrowingId", value=X.
            // Nếu không gọi dòng này, Axon chỉ biết saga có key="id", value=X
            // → on(BookUpdateStatusEvent) sẽ KHÔNG BAO GIỜ được gọi!
            SagaLifecycle.associateWith("borrowingId", event.getId());

            // Gửi command cập nhật trạng thái sách → isReady = false
            // BookAggregate sẽ check lại isReady (guard chống race condition)
            BookUpdateStatusCommand command = new BookUpdateStatusCommand(
                    event.getBookId(), false, event.getEmployeeId(), event.getId()
            );
            commandGateway.sendAndWait(command, 30, TimeUnit.SECONDS);
            log.info("Saga: BookUpdateStatusCommand sent successfully for borrowingId={}", event.getId());

        } catch (Exception e) {
            // Bắt TẤT CẢ exception, kể cả TimeoutException từ sendAndWait
            // Không để exception thoát ra ngoài → tránh TrackingEventProcessor vào retry loop
            log.error("Saga failed for borrowingId={}, reason: {}", event.getId(), e.getMessage(), e);
            rollbackBorrowingRecord(event.getId());
        }
    }

    /**
     * Bước 2: Saga kết thúc khi nhận BookUpdateStatusEvent.
     * Association dùng "borrowingId" để khớp đúng với saga này.
     */
    @SagaEventHandler(associationProperty = "borrowingId")
    private void on(BookUpdateStatusEvent event) {
        log.info("Saga complete - bookId: {}, isReady: {}, borrowingId: {}",
                event.getBookId(), event.getIsReady(), event.getBorrowingId());
        SagaLifecycle.end();
    }

    /**
     * Compensating transaction: rollback bản ghi mượn sách.
     *
     * QUAN TRỌNG: Phương thức này KHÔNG được throw exception ra ngoài.
     * Mọi lỗi phải được xử lý nội bộ — sau đó vẫn phải gọi SagaLifecycle.end()
     * để tránh saga bị treo trong saga store vĩnh viễn.
     */
    private void rollbackBorrowingRecord(String borrowingId) {
        log.warn("Rolling back borrowing record: {}", borrowingId);
        try {
            DeleteBorrowingCommand command = new DeleteBorrowingCommand(borrowingId);
            commandGateway.sendAndWait(command, 30, TimeUnit.SECONDS);
            log.info("Rollback success for borrowingId={}", borrowingId);
        } catch (Exception ex) {
            // Log lỗi nhưng KHÔNG re-throw — saga phải end dù rollback có thành công hay không
            log.error("Rollback failed for borrowingId={}, reason: {}. Saga will end anyway.",
                    borrowingId, ex.getMessage());
        } finally {
            // LUÔN end saga dù rollback thành công hay thất bại
            // Tránh saga bị treo mãi trong saga store
            SagaLifecycle.end();
        }
    }
}
