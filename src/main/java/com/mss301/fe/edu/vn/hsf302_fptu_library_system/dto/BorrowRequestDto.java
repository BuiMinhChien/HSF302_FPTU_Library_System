package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowRequestStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRequestDto {
    private Integer requestId;
    private Integer bookId;
    private String bookTitle;
    private Integer userId;
    private String studentCode;
    private String username;
    private EBorrowRequestStatus status;
    private String rejectionReason;
    private Integer approvedById;
    private String approvedByName;
    private LocalDateTime approvedDate;
    private LocalDateTime createdDate;
    private BookDetailDto bookDetail;
    private String reservedBookCopyBarcode;
}
