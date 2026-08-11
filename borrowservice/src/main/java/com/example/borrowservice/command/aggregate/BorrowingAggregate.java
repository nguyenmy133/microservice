package com.example.borrowservice.command.aggregate;

import com.example.borrowservice.command.command.CreateBorrowingCommand;
import com.example.borrowservice.command.command.DeleteBorrowingCommand;
import com.example.borrowservice.command.event.BorrowingCreatedEvent;
import com.example.borrowservice.command.event.BorrowingDeleteEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Aggregate
public class BorrowingAggregate {
    @AggregateIdentifier
    private String id;
    private String bookId;
    private String employeeId;   // Thêm field để aggregate state đầy đủ
    private Date borrowingDate;
    private Date returnDate;

    public BorrowingAggregate(){}

    @CommandHandler
    public BorrowingAggregate(CreateBorrowingCommand command){
        BorrowingCreatedEvent event=new BorrowingCreatedEvent();
        BeanUtils.copyProperties(command,event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(BorrowingCreatedEvent event){
        this.id=event.getId();
        this.bookId=event.getBookId();         // Đã sửa: trước đây sai là event.getEmployeeId()
        this.employeeId=event.getEmployeeId(); // Thêm: lưu đúng employeeId
        this.borrowingDate=event.getBorrowingDate();
    }

    @CommandHandler
    public void handle(DeleteBorrowingCommand command){
        BorrowingDeleteEvent event=new BorrowingDeleteEvent(command.getId());
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(BorrowingDeleteEvent event){
        this.id=event.getId();
    }
}
