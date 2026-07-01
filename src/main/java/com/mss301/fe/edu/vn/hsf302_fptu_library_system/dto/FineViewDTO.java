package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EFineStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class FineViewDTO {
    private Integer fineId;
    private Integer borrowId;
    private String bookTitle;
    private String reason;
    private BigDecimal amount;
    private EFineStatus status;
    private String statusLabel;
    private LocalDateTime createdAt;
    private boolean canPay;
}
