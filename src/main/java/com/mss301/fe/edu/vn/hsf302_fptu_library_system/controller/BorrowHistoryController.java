package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowHistory;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.BorrowHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/borrow-history")
public class BorrowHistoryController {

    private final BorrowHistoryService borrowHistoryService;

    // Reader vào trang này để xem lịch sử mượn sách của mình
    @GetMapping
    public String borrowHistory(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fromDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        // Gọi service lấy danh sách lịch sử có tìm kiếm và phân trang
        Page<BorrowHistory> historyPage =
                borrowHistoryService.getCurrentUserHistory(keyword, fromDate, toDate, page, size);

        // Đẩy dữ liệu ra model cho Thymeleaf
        model.addAttribute("historyPage", historyPage);
        model.addAttribute("histories", historyPage.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        // Trả về tên file Thymeleaf template (không cần đuôi .html)
        return "/pages/borrow-history";
    }
}