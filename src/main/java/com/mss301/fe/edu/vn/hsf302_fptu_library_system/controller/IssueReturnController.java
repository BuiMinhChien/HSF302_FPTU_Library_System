package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BorrowHistoryDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BorrowRequestDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BorrowHistoryRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BorrowRequestRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.BorrowHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/librarian")
public class IssueReturnController {

    private final BorrowHistoryService borrowHistoryService;
    private final BorrowRequestRepository borrowRequestRepository;
    private final BorrowHistoryRepository borrowHistoryRepository;

    // ─────────────────────────────────────────────
    // ISSUE BOOK — Hiển thị danh sách yêu cầu đã duyệt (WAITING = chờ lấy sách)
    // URL: GET /librarian/issue
    // ─────────────────────────────────────────────
    @GetMapping("/issue")
    public String listApprovedRequests(Model model) {
        List<BorrowRequestDto> approvedList = borrowRequestRepository
                .findByStatusOrderByCreatedAtAsc(EBorrowRequestStatus.WAITING)
                .stream()
                .map(r -> BorrowRequestDto.builder()
                        .requestId(r.getRequestId())
                        .username(r.getUser().getFullName())
                        .studentCode(r.getUser().getCode())
                        .bookTitle(r.getBook().getTitle())
                        .createdDate(r.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        model.addAttribute("approvedList", approvedList);
        return "pages/issue-book";
    }

    // ─────────────────────────────────────────────
    // ISSUE BOOK — Thực hiện giao sách vật lý
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
        List<BorrowHistoryDto> activeList = borrowHistoryRepository
                .findByReturnDateIsNull()
                .stream()
                .map(b -> BorrowHistoryDto.builder()
                        .borrowId(b.getBorrowId())
                        .studentName(b.getUser().getFullName())
                        .studentCode(b.getUser().getCode())
                        .bookTitle(b.getCopy().getBook().getTitle())
                        .borrowDate(b.getBorrowDate())
                        .dueDate(b.getDueDate())
                        .build())
                .collect(Collectors.toList());
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
