package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BorrowRequestDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.BorrowRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/librarian/borrow-requests")
public class ApprovalBorrowRequestController {
    private final BorrowRequestService borrowRequestService;

    @GetMapping
    public String listRequests(
            @RequestParam(required = false) String bookTitle,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String studentCode,
            @RequestParam(required = false) EBorrowRequestStatus status,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Page<BorrowRequestDto> requestPage =
                borrowRequestService.getAllRequests(
                        bookTitle,
                        fullName,
                        studentCode,
                        status,
                        page,
                        10
                );
        model.addAttribute("requestPage", requestPage);
        model.addAttribute("requests", requestPage.getContent());
        model.addAttribute("bookTitle", bookTitle);
        model.addAttribute("fullName", fullName);
        model.addAttribute("studentCode", studentCode);
        model.addAttribute(
                "status",
                status != null ? status.name() : null
        );
        return "pages/approval-borrow-request-list";
    }
}
