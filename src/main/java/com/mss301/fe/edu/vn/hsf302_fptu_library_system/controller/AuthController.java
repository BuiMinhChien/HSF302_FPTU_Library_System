package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.ChangePasswordRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

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

    @GetMapping("/change-password")
    public String changePasswordPage(Model model) {
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        return "pages/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute ChangePasswordRequest changePasswordRequest, BindingResult bindingResult, Model model, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "pages/change-password";
        }
        boolean result = authService.changePassword(
                authentication.getName(),
                changePasswordRequest.getOldPassword(),
                changePasswordRequest.getNewPassword()
            );
        if(result) {
            model.addAttribute("success", true);
        }
        else model.addAttribute("error", "Mật khẩu hiện tại không đúng");
        return "pages/change-password";
    }

    @GetMapping("/403")
    public String accessDenied(HttpServletRequest request, Model model) {
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("status", 403);
        model.addAttribute("error", "FORBIDDEN");
        model.addAttribute("message", "You don't have permission to access this page");
        model.addAttribute("detail", request.getAttribute("detail"));
        model.addAttribute("path", request.getAttribute("path"));
        return "pages/error-page";
    }
}
