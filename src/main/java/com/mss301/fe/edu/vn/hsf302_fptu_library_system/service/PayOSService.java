package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.PayOSCreatePaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.exception.PayOSException;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.model.v2.paymentRequests.PaymentLinkStatus;

@Slf4j
@Service
public class PayOSService {

    private final PayOS payOS;
    private final String returnUrl;
    private final String cancelUrl;

    public PayOSService(
            @Value("${payos.client-id}") String clientId,
            @Value("${payos.api-key}") String apiKey,
            @Value("${payos.checksum-key}") String checksumKey,
            @Value("${payos.return-url}") String returnUrl,
            @Value("${payos.cancel-url}") String cancelUrl
    ) {
        this.payOS = new PayOS(clientId, apiKey, checksumKey);
        this.returnUrl = returnUrl;
        this.cancelUrl = cancelUrl;
    }

    public PayOSCreatePaymentResponse createPaymentLink(long orderCode, long amount, String description) {
        try {
            CreatePaymentLinkRequest request = CreatePaymentLinkRequest.builder()
                    .orderCode(orderCode)
                    .amount(amount)
                    .description(description)
                    .returnUrl(returnUrl + "?orderCode=" + orderCode)
                    .cancelUrl(cancelUrl + "?orderCode=" + orderCode)
                    .build();

            CreatePaymentLinkResponse response = payOS.paymentRequests().create(request);
            return PayOSCreatePaymentResponse.builder()
                    .orderCode(orderCode)
                    .checkoutUrl(response.getCheckoutUrl())
                    .paymentLinkId(response.getPaymentLinkId())
                    .qrCode(response.getQrCode())
                    .build();
        } catch (PayOSException e) {
            log.error("PayOS create payment failed: {}", e.getMessage());
            throw new RuntimeException("Không thể tạo link thanh toán PayOS: " + e.getMessage(), e);
        }
    }

    public boolean isPaymentPaid(long orderCode) {
        try {
            PaymentLink info = payOS.paymentRequests().get(orderCode);
            return info != null && PaymentLinkStatus.PAID.equals(info.getStatus());
        } catch (PayOSException e) {
            log.error("PayOS verify payment failed: {}", e.getMessage());
            throw new RuntimeException("Không thể xác minh giao dịch PayOS", e);
        }
    }

    public String getTransactionCode(long orderCode) {
        return String.valueOf(orderCode);
    }
}
