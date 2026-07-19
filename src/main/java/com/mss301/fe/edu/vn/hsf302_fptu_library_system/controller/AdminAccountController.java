package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ERole;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.AccountFormDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.UserProfileDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
            @RequestParam(required = false) Boolean status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Page<UserProfileDto> accountPage = accountService.searchAccounts(keyword, role, status, page, size);
        model.addAttribute("accounts", accountPage.getContent());
        model.addAttribute("accountPage", accountPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("role", role);
        model.addAttribute("status", status);
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

    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addAccountPage(Model model) {
        model.addAttribute("accountForm", new AccountFormDto());
        return "pages/account-detail";
    }

    // 3. Xử lý Thêm mới tài khoản
    @PostMapping({"/add","/edit"})
    @PreAuthorize("hasRole('ADMIN')")
    public String addAccount(
            @Valid @ModelAttribute("accountForm") AccountFormDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "pages/account-detail";
        }
        try {
            accountService.saveAccount(dto);
            if(dto.getUserId() != null){
                redirectAttributes.addFlashAttribute("success", "Cập nhật tài khoản thành công!");
            }
            else redirectAttributes.addFlashAttribute("success", "Thêm tài khoản mới thành công!");
            return "redirect:/admin/accounts";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            if(dto.getUserId() != null){
                return "redirect:/admin/accounts/edit/" + dto.getUserId();
            }
            else return "redirect:/admin/accounts/add";
        }
    }

    @GetMapping("/edit/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editAccountPage(@PathVariable Integer userId, Model model) {
        AccountFormDto dto = accountService.getAccountForEdit(userId);
        model.addAttribute("accountForm", dto);
        model.addAttribute("isEdit", true);
        return "pages/account-detail";
    }
}
