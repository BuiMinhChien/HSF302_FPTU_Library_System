package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EFineStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EPaymentMethod;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EPaymentStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.FinePaymentDTO;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.PayOSCreatePaymentResponse;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Fine;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.User;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.model.FinePaymentRecord;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.FinePaymentJdbcRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.util.CommonFunction;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class FinePaymentServiceImpl implements FinePaymentService {

    private final FinePaymentJdbcRepository paymentRepository;
    private final FineService fineService;
    private final PayOSService payOSService;
    private final CommonFunction commonFunction;

    @Override
    public String initiatePayment(Integer fineId) {
        User reader = commonFunction.getCurrentUser();
        Fine fine = fineService.getFineForPayment(fineId);

        if (!fine.getUser().getUserId().equals(reader.getUserId())) {
            throw new AccessDeniedException("Bạn không có quyền thanh toán khoản phạt này");
        }
        if (fine.getStatus() != EFineStatus.UNPAID) {
            throw new IllegalStateException("Khoản phạt đã được thanh toán");
        }
        if (paymentRepository.existsPendingByFineId(fineId)) {
            throw new IllegalStateException("Đang có giao dịch thanh toán chờ xử lý");
        }

        long orderCode = System.currentTimeMillis() / 1000;

        FinePaymentRecord record = FinePaymentRecord.builder()
                .fineId(fineId)
                .readerId(reader.getUserId())
                .orderCode(orderCode)
                .amount(fine.getAmount())
                .paymentMethod(EPaymentMethod.PAYOS)
                .paymentStatus(EPaymentStatus.PENDING)
                .build();

        Long paymentId = paymentRepository.insertPending(record);

        PayOSCreatePaymentResponse payosResponse = payOSService.createPaymentLink(
                orderCode,
                fine.getAmount().longValue(),
                "Thanh toan phat thu vien #" + fineId
        );

        paymentRepository.updateCheckoutUrl(paymentId, payosResponse.getCheckoutUrl());
        return payosResponse.getCheckoutUrl();
    }

    @Override
    public FinePaymentDTO handleSuccess(Long orderCode) {
        FinePaymentRecord payment = paymentRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giao dịch"));

        if (payment.getPaymentStatus() == EPaymentStatus.SUCCESS) {
            return toDTO(payment);
        }

        boolean paid = payOSService.isPaymentPaid(orderCode);
        if (paid) {
            String txnCode = payOSService.getTransactionCode(orderCode);
            paymentRepository.markSuccess(payment.getId(), txnCode);
            fineService.markFineAsPaid(payment.getFineId());
            payment = paymentRepository.findByOrderCode(orderCode).orElse(payment);
        } else {
            paymentRepository.markFailed(payment.getId());
            payment = paymentRepository.findByOrderCode(orderCode).orElse(payment);
        }
        return toDTO(payment);
    }

    @Override
    public FinePaymentDTO handleCancel(Long orderCode) {
        FinePaymentRecord payment = paymentRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giao dịch"));

        if (payment.getPaymentStatus() == EPaymentStatus.PENDING) {
            paymentRepository.markCancelled(payment.getId());
            payment = paymentRepository.findByOrderCode(orderCode).orElse(payment);
        }
        return toDTO(payment);
    }

    private FinePaymentDTO toDTO(FinePaymentRecord record) {
        return FinePaymentDTO.builder()
                .id(record.getId())
                .fineId(record.getFineId())
                .readerId(record.getReaderId())
                .orderCode(record.getOrderCode())
                .amount(record.getAmount())
                .paymentMethod(record.getPaymentMethod())
                .paymentStatus(record.getPaymentStatus())
                .checkoutUrl(record.getCheckoutUrl())
                .transactionCode(record.getTransactionCode())
                .createdAt(record.getCreatedAt())
                .paidAt(record.getPaidAt())
                .cancelledAt(record.getCancelledAt())
                .build();
    }
}
