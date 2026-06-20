package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BookDetailDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping()
    public String homePage() {
        return "pages/home";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        BookDetailDto bookDetail = bookService.getBookDetail(id);
        model.addAttribute("book", bookDetail);
        return "pages/book-detail";
    }
}
