package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ERole;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.User;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/accounts")
@RequiredArgsConstructor
public class AdminAccountController {

    private final AccountService accountService;

    // 1. Màn hình danh sách tài khoản
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String showAllAccounts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ERole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Page<User> accountPage = accountService.searchAccounts(keyword, role, page, size);

        model.addAttribute("accounts", accountPage.getContent());
        model.addAttribute("accountPage", accountPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("role", role);

        return "pages/admin-account-list";
    }

    // 2. Xử lý nút Khóa / Mở khóa
    @PostMapping("/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public String toggleAccountStatus(@RequestParam("userId") Integer userId, RedirectAttributes redirectAttributes) {
        try {
            accountService.toggleAccountStatus(userId);
            redirectAttributes.addAttribute("success", "Cập nhật trạng thái tài khoản thành công!");
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/accounts";
    }
}
