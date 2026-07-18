package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BookDetailDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BookListDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    // TH1: Nhấn "Trang chủ" -> gọi GET /books -> hiện danh sách sách + search
    @GetMapping()
    public String homePage(
            @RequestParam(required = false) String keyword,         // từ khoá tìm kiếm
            @RequestParam(required = false, defaultValue = "title") String searchType, // loại tìm kiếm
            @RequestParam(defaultValue = "0") int page,             // trang hiện tại
            @RequestParam(defaultValue = "10") int size,            // 10 sách/trang (2 hàng × 5 cột)n)
            Model model
    ) {
        // Gọi service lấy danh sách sách có phân trang
        Page<BookListDto> bookPage = bookService.searchBooks(keyword, searchType, page, size);

        // Đẩy data xuống Thymeleaf
        model.addAttribute("bookPage", bookPage);
        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);

        return "pages/home";
    }

    // Xem chi tiết 1 cuốn sách
    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        BookDetailDto bookDetail = bookService.getBookDetail(id);
        model.addAttribute("book", bookDetail);
        return "pages/book-detail";
    }
}