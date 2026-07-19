package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowHistoryStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BorrowHistoryDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BorrowRequestDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BorrowHistoryRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BorrowRequestRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.BorrowHistoryService;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.BorrowRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

//    @GetMapping("/issue")
//    public String listApprovedRequests(
//            Model model
//    ) {
//        List<BorrowRequestDto> approvedList = borrowRequestRepository
//                .findByStatusOrderByCreatedAtAsc(EBorrowRequestStatus.WAITING)
//                .stream()
//                .map(r -> BorrowRequestDto.builder()
//                        .requestId(r.getRequestId())
//                        .username(r.getUser().getFullName())
//                        .studentCode(r.getUser().getCode())
//                        .bookTitle(r.getBook().getTitle())
//                        .createdDate(r.getCreatedAt())
//                        .build())
//                .collect(Collectors.toList());
//        model.addAttribute("approvedList", approvedList);
//        return "pages/issue-book";
//    }

//    @PostMapping("/issue/{requestId}")
//    public String issueBook(@PathVariable Integer requestId,
//                            RedirectAttributes redirectAttributes) {
//        try {
//            borrowHistoryService.issueBook(requestId);
//            redirectAttributes.addFlashAttribute("success", "Giao sách thành công!");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", e.getMessage());
//        }
//        return "redirect:/librarian/issue";
//    }

//    @GetMapping("/confirm-return")
//    public String listActiveBorrows(Model model) {
//        List<BorrowHistoryDto> activeList = borrowHistoryRepository
//                .findByReturnDateIsNull()
//                .stream()
//                .map(b -> BorrowHistoryDto.builder()
//                        .borrowId(b.getBorrowId())
//                        .studentName(b.getUser().getFullName())
//                        .studentCode(b.getUser().getCode())
//                        .bookTitle(b.getCopy().getBook().getTitle())
//                        .borrowDate(b.getBorrowDate())
//                        .dueDate(b.getDueDate())
//                        .status(b.getStatus())
//                        .build())
//                .collect(Collectors.toList());
//        model.addAttribute("activeList", activeList);
//        return "pages/confirm-return";
//    }

    @GetMapping("/confirm-return")
    public String listActiveBorrows(
            @RequestParam(defaultValue = "") String fullName,
            @RequestParam(defaultValue = "") String bookTitle,
            @RequestParam(required = false) EBorrowHistoryStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Page<BorrowHistoryDto> activePage =
                borrowHistoryService.getActiveBorrows(
                        fullName,
                        bookTitle,
                        status,
                        page,
                        size
                );
        model.addAttribute("activePage", activePage);
        model.addAttribute("activeList", activePage.getContent());
        model.addAttribute("fullName", fullName);
        model.addAttribute("bookTitle", bookTitle);
        model.addAttribute("status", status);
        return "pages/confirm-return";
    }

    @PostMapping("/confirm-return/{borrowId}")
    public String confirmReturn(@PathVariable Integer borrowId, RedirectAttributes redirectAttributes) {
        try {
            borrowHistoryService.confirmReturn(borrowId);
            redirectAttributes.addFlashAttribute("success", "Xác nhận trả sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/librarian/confirm-return";
    }
}
