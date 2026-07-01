package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EFineStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EPaymentMethod;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EPaymentStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.FinePaymentDTO;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Fine;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.FinePayment;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.User;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.FinePaymentRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.util.CommonFunction;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
@RequiredArgsConstructor
public class FinePaymentServiceImpl implements FinePaymentService {

    private static final long ORDER_CODE_MULTIPLIER = 10_000_000_000L;

    private final Map<Long, FinePaymentDTO> pendingCheckouts = new ConcurrentHashMap<>();

    private final FinePaymentRepository finePaymentRepository;
    private final FineService fineService;
    private final PayOSService payOSService;
    private final CommonFunction commonFunction;

    @Override
    public FinePaymentDTO prepareCheckout(Integer fineId) {
        User reader = commonFunction.getCurrentUser();
        Fine fine = fineService.getFineForPayment(fineId);

        if (!fine.getUser().getUserId().equals(reader.getUserId())) {
            throw new AccessDeniedException("Bạn không có quyền thanh toán khoản phạt này");
        }
        if (fine.getStatus() != EFineStatus.UNPAID) {
            throw new IllegalStateException("Khoản phạt đã được thanh toán");
        }
        if (finePaymentRepository.existsByFineId(fineId)) {
            throw new IllegalStateException("Khoản phạt đã có bản ghi thanh toán");
        }

        long orderCode = generateOrderCode(fineId);

        var payosResponse = payOSService.createPaymentLink(
                orderCode,
                fine.getAmount().longValue(),
                String.format("Phat thu vien #%d", fineId)
        );

        FinePaymentDTO checkout = enrichCheckout(FinePaymentDTO.builder()
                .fineId(fine.getFineId())
                .readerId(reader.getUserId())
                .orderCode(orderCode)
                .amount(fine.getAmount())
                .paymentMethod(EPaymentMethod.PAYOS)
                .checkoutUrl(payosResponse.getCheckoutUrl())
                .qrCode(payosResponse.getQrCode())
                .qrPayload(buildQrPayload(fine.getFineId(), orderCode, fine.getAmount(), payosResponse.getQrCode()))
                .mockEnabled(payOSService.isMockEnabled())
                .build());

        pendingCheckouts.put(orderCode, checkout);
        return checkout;
    }

    @Override
    public FinePaymentDTO getCheckoutInfo(Long orderCode) {
        FinePaymentDTO cached = pendingCheckouts.get(orderCode);
        if (cached != null) {
            return cached;
        }

        Fine fine = fineService.getFineForPayment(extractFineId(orderCode));
        return enrichCheckout(FinePaymentDTO.builder()
                .fineId(fine.getFineId())
                .readerId(fine.getUser().getUserId())
                .orderCode(orderCode)
                .amount(fine.getAmount())
                .paymentMethod(EPaymentMethod.PAYOS)
                .qrPayload(buildQrPayload(fine.getFineId(), orderCode, fine.getAmount(), null))
                .mockEnabled(payOSService.isMockEnabled())
                .build());
    }

    private FinePaymentDTO enrichCheckout(FinePaymentDTO checkout) {
        checkout.setQrImageUrl(buildQrImageUrl(checkout.getQrCode(), checkout.getQrPayload()));
        return checkout;
    }

    private String buildQrImageUrl(String qrCode, String qrPayload) {
        if (qrCode != null && !qrCode.isBlank()
                && (qrCode.startsWith("data:image") || qrCode.startsWith("http"))) {
            return qrCode;
        }
        String payload = (qrPayload != null && !qrPayload.isBlank()) ? qrPayload : qrCode;
        if (payload == null || payload.isBlank()) {
            return null;
        }
        return "https://api.qrserver.com/v1/create-qr-code/?size=240x240&margin=10&data="
                + URLEncoder.encode(payload, StandardCharsets.UTF_8);
    }

    private String buildQrPayload(Integer fineId, Long orderCode, BigDecimal amount, String qrCode) {
        if (qrCode != null && !qrCode.isBlank()
                && !qrCode.startsWith("data:image")
                && !qrCode.startsWith("http")) {
            return qrCode;
        }
        return String.format("FPTU-Library|fineId=%d|orderCode=%d|amount=%s", fineId, orderCode, amount.toPlainString());
    }

    @Override
    public FinePaymentDTO handleSuccess(Long orderCode) {
        Fine fine = fineService.getFineForPayment(extractFineId(orderCode));

        if (fine.getStatus() == EFineStatus.PAID
                && finePaymentRepository.existsByFineId(fine.getFineId())) {
            pendingCheckouts.remove(orderCode);
            return toSuccessDTO(fine, orderCode);
        }

        if (!payOSService.isPaymentPaid(orderCode)) {
            return FinePaymentDTO.builder()
                    .fineId(fine.getFineId())
                    .readerId(fine.getUser().getUserId())
                    .orderCode(orderCode)
                    .amount(fine.getAmount())
                    .paymentMethod(EPaymentMethod.PAYOS)
                    .paymentStatus(EPaymentStatus.FAILED)
                    .build();
        }

        completePayment(fine, orderCode, payOSService.getTransactionCode(orderCode));
        pendingCheckouts.remove(orderCode);
        return toSuccessDTO(fine, orderCode);
    }

    @Override
    public FinePaymentDTO syncPaymentStatus(Long orderCode) {
        User reader = commonFunction.getCurrentUser();
        Fine fine = fineService.getFineForPayment(extractFineId(orderCode));

        if (!fine.getUser().getUserId().equals(reader.getUserId())) {
            throw new AccessDeniedException("Bạn không có quyền xem giao dịch này");
        }
        if (fine.getStatus() == EFineStatus.PAID) {
            return toSuccessDTO(fine, orderCode);
        }
        if (!payOSService.isMockEnabled() && payOSService.isPaymentPaid(orderCode)) {
            return handleSuccess(orderCode);
        }

        FinePaymentDTO cached = pendingCheckouts.get(orderCode);
        return FinePaymentDTO.builder()
                .fineId(fine.getFineId())
                .readerId(reader.getUserId())
                .orderCode(orderCode)
                .amount(fine.getAmount())
                .paymentMethod(EPaymentMethod.PAYOS)
                .paymentStatus(EPaymentStatus.PENDING)
                .mockEnabled(payOSService.isMockEnabled())
                .qrImageUrl(cached != null ? cached.getQrImageUrl() : null)
                .build();
    }

    @Override
    public FinePaymentDTO handleCancel(Long orderCode) {
        pendingCheckouts.remove(orderCode);
        Fine fine = fineService.getFineForPayment(extractFineId(orderCode));

        return FinePaymentDTO.builder()
                .fineId(fine.getFineId())
                .readerId(fine.getUser().getUserId())
                .orderCode(orderCode)
                .amount(fine.getAmount())
                .paymentMethod(EPaymentMethod.PAYOS)
                .paymentStatus(EPaymentStatus.CANCELLED)
                .build();
    }

    private void completePayment(Fine fine, Long orderCode, String transactionCode) {
        if (!finePaymentRepository.existsByFineId(fine.getFineId())) {
            FinePayment payment = FinePayment.builder()
                    .fine(fine)
                    .amount(fine.getAmount())
                    .paymentDate(LocalDateTime.now())
                    .paymentMethod(EPaymentMethod.PAYOS.name())
                    .note("PayOS orderCode=" + orderCode + ", transactionCode=" + transactionCode)
                    .build();
            finePaymentRepository.save(payment);
        }

        fineService.markFineAsPaid(fine.getFineId());
    }

    private FinePaymentDTO toSuccessDTO(Fine fine, Long orderCode) {
        return FinePaymentDTO.builder()
                .fineId(fine.getFineId())
                .readerId(fine.getUser().getUserId())
                .orderCode(orderCode)
                .amount(fine.getAmount())
                .paymentMethod(EPaymentMethod.PAYOS)
                .paymentStatus(EPaymentStatus.SUCCESS)
                .paidAt(LocalDateTime.now())
                .build();
    }

    private long generateOrderCode(Integer fineId) {
        return fineId * ORDER_CODE_MULTIPLIER + (System.currentTimeMillis() / 1000) % ORDER_CODE_MULTIPLIER;
    }

    private int extractFineId(Long orderCode) {
        if (orderCode == null || orderCode <= 0) {
            throw new IllegalArgumentException("Mã đơn hàng không hợp lệ");
        }
        return (int) (orderCode / ORDER_CODE_MULTIPLIER);
    }
}
