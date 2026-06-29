package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ENewBookRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.NewBookRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.NewBookRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/new-book-requests")
@RequiredArgsConstructor
public class ApprovalNewBookRequestController {

    private final NewBookRequestService newBookRequestService;

    // 1. Màn hình danh sách (CHỈ ADMIN MỚI ĐƯỢC VÀO)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String showAllRequests(
            @RequestParam(required = false) ENewBookRequestStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        // Gọi Service lấy danh sách của TOÀN TRƯỜNG
        Page<NewBookRequest> requestPage = newBookRequestService.searchAllRequests(status, page, size);

        model.addAttribute("requests", requestPage.getContent());
        model.addAttribute("requestPage", requestPage);
        model.addAttribute("status", status);

        // Trả về file HTML giao diện dành cho Admin mà tôi vừa ném vào project
        return "pages/admin-new-book-request";
    }

    // 2. Xử lý nút Duyệt
    @PostMapping("/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public String approveRequest(@RequestParam("requestId") Integer requestId) {
        try {
            newBookRequestService.approveRequest(requestId);
            return "redirect:/admin/new-book-requests?success=Duyệt yêu cầu thành công!";
        } catch (Exception e) {
            return "redirect:/admin/new-book-requests?error=" + e.getMessage();
        }
    }

    // 3. Xử lý nút Từ chối
    @PostMapping("/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public String rejectRequest(@RequestParam("requestId") Integer requestId) {
        try {
            newBookRequestService.rejectRequest(requestId);
            return "redirect:/admin/new-book-requests?success=Đã từ chối yêu cầu!";
        } catch (Exception e) {
            return "redirect:/admin/new-book-requests?error=" + e.getMessage();
        }
    }
}