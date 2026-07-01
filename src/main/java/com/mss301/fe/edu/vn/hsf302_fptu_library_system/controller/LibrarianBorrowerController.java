package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowHistory;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.BorrowHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;

@Controller
@RequestMapping("/librarian/borrowers-this-week")
@RequiredArgsConstructor
public class LibrarianBorrowerController {

    private final BorrowHistoryService borrowHistoryService;

    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public String viewBorrowersThisWeek(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isReturned,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        // Lấy danh sách từ sv
        Page<BorrowHistory> historyPage = borrowHistoryService.getBorrowersThisWeek(keyword, isReturned, page, size);

        //Lấy ngày đầu tuần và cuối tuần để mang ra màn hình
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        model.addAttribute("histories", historyPage.getContent());
        model.addAttribute("historyPage", historyPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("isReturned", isReturned);
        model.addAttribute("startOfWeek", startOfWeek);
        model.addAttribute("endOfWeek", endOfWeek);
        // Chuyển hướng ra file HTML
        return "pages/librarian-borrowers-this-week";
    }
}