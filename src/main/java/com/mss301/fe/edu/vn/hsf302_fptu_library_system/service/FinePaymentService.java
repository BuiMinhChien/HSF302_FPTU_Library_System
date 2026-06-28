package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.FinePaymentDTO;

public interface FinePaymentService {

    String initiatePayment(Integer fineId);

    FinePaymentDTO handleSuccess(Long orderCode);

    FinePaymentDTO handleCancel(Long orderCode);
}
