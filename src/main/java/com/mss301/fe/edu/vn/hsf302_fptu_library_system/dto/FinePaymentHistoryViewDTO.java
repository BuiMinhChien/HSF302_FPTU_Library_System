package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinePaymentHistoryViewDTO {
    private Integer paymentId;
    private Integer fineId;
    private String bookTitle;
    private String reason;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String paymentMethod;
}
