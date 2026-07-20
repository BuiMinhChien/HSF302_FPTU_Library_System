package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BookDetailDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BookListDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping()
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @Value("${borrow.request.borrow-days}")
    private int borrowDays;

    @GetMapping({"/","/books"})
    public String homePage(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "title") String searchType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Page<BookListDto> bookPage = bookService.searchBooks(keyword, searchType, page, size);
        model.addAttribute("bookPage", bookPage);
        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        return "pages/home";
    }

    // Xem chi tiết 1 cuốn sách
    @GetMapping("/books/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        BookDetailDto bookDetail = bookService.getBookDetail(id);
        model.addAttribute("book", bookDetail);
        model.addAttribute("borrowDays", borrowDays);
        return "pages/book-detail";
    }
}