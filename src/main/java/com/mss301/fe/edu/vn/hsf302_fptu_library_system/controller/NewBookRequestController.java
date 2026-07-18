package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ENewBookRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.NewBookRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.NewBookRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/new-book-requests")
@RequiredArgsConstructor
public class NewBookRequestController {
    private final NewBookRequestService newBookRequestService;
    @GetMapping
    public String showMyRequests(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ENewBookRequestStatus status,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fromDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Page<NewBookRequest> requestPage = newBookRequestService.searchMyRequests(keyword, status, fromDate, toDate, page, size);
        model.addAttribute("requests", requestPage.getContent());
        model.addAttribute("requestPage", requestPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        return "pages/new-book-request-list";
    }

    @PostMapping("/create")
    public String createRequest(@ModelAttribute NewBookRequest request) {
        try {
            // Gọi Service để tạo yêu cầu
            newBookRequestService.createRequest(request);
            return "redirect:/new-book-requests?success=Tao yeu cau thanh cong!";
        } catch (Exception e) {
            // Nếu có lỗi, cũng trả về nhưng hiện báo lỗi
            return "redirect:/new-book-requests?error=Co loi xay ra!";
        }
    }
}