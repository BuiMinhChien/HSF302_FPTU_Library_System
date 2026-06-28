package com.mss301.fe.edu.vn.hsf302_fptu_library_system.model;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EPaymentMethod;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EPaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class FinePaymentRecord {
    private Long id;
    private Integer fineId;
    private Integer readerId;
    private Long orderCode;
    private BigDecimal amount;
    private EPaymentMethod paymentMethod;
    private EPaymentStatus paymentStatus;
    private String checkoutUrl;
    private String transactionCode;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;
}
