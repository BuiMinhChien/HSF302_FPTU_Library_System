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

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/new-book-requests")
@RequiredArgsConstructor
public class ApprovalNewBookRequestController {

    private final NewBookRequestService newBookRequestService;

    // Màn hình danh sách duyệt
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String showAllRequests(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ENewBookRequestStatus status,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fromDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Page<NewBookRequest> requestPage = newBookRequestService.searchAllRequests(keyword, status, fromDate, page, size);

        model.addAttribute("requests", requestPage.getContent());
        model.addAttribute("requestPage", requestPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("fromDate", fromDate);

        return "pages/admin-new-book-request";
    }

    // xử lý nút duyệt
    @PostMapping("/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public String approveRequest(@RequestParam("requestId") Integer requestId, RedirectAttributes redirectAttributes) {
        try {
            newBookRequestService.approveRequest(requestId);
            redirectAttributes.addAttribute("success", "Duyệt yêu cầu thành công!");
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/new-book-requests";
    }

    // Xử lý nút Từ chối
    @PostMapping("/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public String rejectRequest(
            @RequestParam("requestId") Integer requestId,
            @RequestParam(value = "rejectionReason", required = false) String rejectionReason,
            RedirectAttributes redirectAttributes
    ) {
        try {
            newBookRequestService.rejectRequest(requestId, rejectionReason);
            redirectAttributes.addAttribute("success", "Đã từ chối yêu cầu!");
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/new-book-requests";
    }
}