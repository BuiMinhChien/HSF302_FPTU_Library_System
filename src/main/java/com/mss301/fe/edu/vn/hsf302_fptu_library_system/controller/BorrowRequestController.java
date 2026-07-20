package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BorrowRequestDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.BorrowRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/borrow-requests")
public class BorrowRequestController {
    private final BorrowRequestService borrowRequestService;

    @PostMapping("/create")
    public String createBorrowRequest(
            @RequestParam Integer bookId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        String result = borrowRequestService.createBorrowRequest(bookId, authentication.getName());
        if(result == null) {
            redirectAttributes.addFlashAttribute("success", "Đăng ký mượn sách thành công!");
        }
        else redirectAttributes.addFlashAttribute("error", result);
        return "redirect:/books/" + bookId;
    }

    @GetMapping
    public String borrowRequests(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) EBorrowRequestStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Page<BorrowRequestDto> requestPage = borrowRequestService.getCurrentUserRequests(keyword, status, page, size);
        model.addAttribute("requestPage", requestPage);
        model.addAttribute("requests", requestPage.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute(
                "status",
                status != null ? status.name() : null
        );
        return "/pages/borrow-request-list";
    }

    @PostMapping("/{id}/cancel")
    public String cancelRequest(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            borrowRequestService.cancelPendingRequest(id);
            redirectAttributes.addFlashAttribute("success", "Đã huỷ yêu cầu mượn sách");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/borrow-requests";
    }
}
