package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowHistoryStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowHistoryDto {
    private Integer borrowId;
    private String studentName;
    private String studentCode;
    private String bookTitle;
    private EBorrowHistoryStatus status;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
}
