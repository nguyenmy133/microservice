package com.example.commonservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event được publish bởi bookservice khi trạng thái sẵn sàng (isReady) của sách thay đổi.
 * Đặt trong commonservice để cả bookservice và borrowservice đều có thể dùng mà không tạo coupling trực tiếp.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookUpdateStatusEvent {
    private String bookId;
    private Boolean isReady;
    private String employeeId;   // Đã sửa typo: emmployeeId → employeeId
    private String borrowingId;
}
