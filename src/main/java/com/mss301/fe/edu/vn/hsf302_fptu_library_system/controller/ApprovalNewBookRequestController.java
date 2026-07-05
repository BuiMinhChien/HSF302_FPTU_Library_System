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

    //màn hình danh sách
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String showAllRequests(
            @RequestParam(required = false) ENewBookRequestStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        // gọi sv laayasy danh sách của toàn trường
        Page<NewBookRequest> requestPage = newBookRequestService.searchAllRequests(status, page, size);

        model.addAttribute("requests", requestPage.getContent());
        model.addAttribute("requestPage", requestPage);
        model.addAttribute("status", status);

        //trả về giao diện
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

    //Xử lý nút Từ chối
    @PostMapping("/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public String rejectRequest(@RequestParam("requestId") Integer requestId, RedirectAttributes redirectAttributes) {
        try {
            newBookRequestService.rejectRequest(requestId);
            redirectAttributes.addAttribute("success", "Đã từ chối yêu cầu!");
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/new-book-requests";
    }
}