package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

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
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
}
