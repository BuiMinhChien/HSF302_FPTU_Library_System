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
            @RequestParam(required = false) String keyword,// từ khóa tìm kiếm, không bắt buộc
            @RequestParam(defaultValue = "0") int page,// trang hiện tại, mặc định = 0
            @RequestParam(defaultValue = "10") int size,// số dòng/trang, mặc định = 10
            Model model                                            // object để đẩy data sang Thymeleaf
    ) {
        // Gọi sv lấy danh sách lịch sử có phân trang
        Page<BorrowHistory> historyPage =
                borrowHistoryService.getCurrentUserHistory(keyword, page, size);

        // Đẩy dữ liệu vào model để Thymeleaf có thể đọc và render ra HTML
        model.addAttribute("historyPage", historyPage);           // object Page (chứa thông tin phân trang)
        model.addAttribute("histories", historyPage.getContent()); // danh sách lịch sửi
        model.addAttribute("keyword", keyword);                    // giữ lại keyword trên thanh tìm kiếm

        // Trả về tên file Thymeleaf template (không cần đuôi .html)
        return "/pages/borrow-history";
    }
}