package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EPaymentStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.FinePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/payment/payos")
@RequiredArgsConstructor
public class FinePaymentController {

    private final FinePaymentService finePaymentService;

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam Long orderCode, Model model) {
        try {
            var payment = finePaymentService.handleSuccess(orderCode);
            model.addAttribute("payment", payment);
            if (payment.getPaymentStatus() == EPaymentStatus.SUCCESS) {
                return "pages/fine-payment-success";
            }
            return "pages/fine-payment-fail";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "pages/fine-payment-fail";
        }
    }

    @GetMapping("/cancel")
    public String paymentCancel(@RequestParam Long orderCode, Model model) {
        try {
            var payment = finePaymentService.handleCancel(orderCode);
            model.addAttribute("payment", payment);
            return "pages/fine-payment-cancel";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "pages/fine-payment-cancel";
        }
    }
}
