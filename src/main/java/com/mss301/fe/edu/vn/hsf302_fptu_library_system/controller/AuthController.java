package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "pages/login";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "pages/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPasswordPost(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            authService.resetPassword(email);
            redirectAttributes.addFlashAttribute("success", "Mật khẩu mới đã được gửi về email của bạn.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/forgot-password";
    }
}
