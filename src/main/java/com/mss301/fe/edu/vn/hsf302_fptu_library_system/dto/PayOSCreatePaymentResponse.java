package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayOSCreatePaymentResponse {
    private Long orderCode;
    private String checkoutUrl;
    private String paymentLinkId;
    private String qrCode;
}
