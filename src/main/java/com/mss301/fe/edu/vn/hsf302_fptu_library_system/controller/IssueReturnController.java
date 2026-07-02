package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowHistory;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BorrowHistoryRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BorrowRequestRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.BorrowHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/librarian")
public class IssueReturnController {

    private final BorrowHistoryService borrowHistoryService;
    private final BorrowRequestRepository borrowRequestRepository;
    private final BorrowHistoryRepository borrowHistoryRepository;

    // ─────────────────────────────────────────────
    // ISSUE BOOK — Hiển thị danh sách yêu cầu đã duyệt (status = WAITING = chờ lấy sách)
    // URL: GET /librarian/issue
    // ─────────────────────────────────────────────
    @GetMapping("/issue")
    public String listApprovedRequests(Model model) {
        List<BorrowRequest> approvedList = borrowRequestRepository
                .findByStatusOrderByCreatedAtAsc(EBorrowRequestStatus.WAITING);
        model.addAttribute("approvedList", approvedList);
        return "pages/issue-book";
    }

    // ─────────────────────────────────────────────
    // ISSUE BOOK — Thực hiện giao sách
    // URL: POST /librarian/issue/{requestId}
    // ─────────────────────────────────────────────
    @PostMapping("/issue/{requestId}")
    public String issueBook(@PathVariable Integer requestId,
                            RedirectAttributes redirectAttributes) {
        try {
            borrowHistoryService.issueBook(requestId);
            redirectAttributes.addFlashAttribute("success", "Giao sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/librarian/issue";
    }

    // ─────────────────────────────────────────────
    // CONFIRM RETURN — Hiển thị danh sách đang mượn (chưa trả)
    // URL: GET /librarian/confirm-return
    // ─────────────────────────────────────────────
    @GetMapping("/confirm-return")
    public String listActiveBorrows(Model model) {
        List<BorrowHistory> activeList = borrowHistoryRepository
                .findByReturnDateIsNull();
        model.addAttribute("activeList", activeList);
        return "pages/confirm-return";
    }

    // ─────────────────────────────────────────────
    // CONFIRM RETURN — Xác nhận sinh viên đã trả sách
    // URL: POST /librarian/confirm-return/{borrowId}
    // ─────────────────────────────────────────────
    @PostMapping("/confirm-return/{borrowId}")
    public String confirmReturn(@PathVariable Integer borrowId,
                                RedirectAttributes redirectAttributes) {
        try {
            borrowHistoryService.confirmReturn(borrowId);
            redirectAttributes.addFlashAttribute("success", "Xác nhận trả sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/librarian/confirm-return";
    }
}
