package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BookFormDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.AuthorService;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.BookService;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class AdminBookController {
    private final BookService bookService;
    private final CategoryService categoryService;
    private final AuthorService authorService;

    @GetMapping
    public String listBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<BookFormDto> bookPage = bookService.getAllBooksForAdmin(keyword, page, size);
        model.addAttribute("bookPage", bookPage);
        model.addAttribute("keyword", keyword);
        return "pages/admin-book-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("bookForm", new BookFormDto());
        // Lấy danh sách category và author để hiển thị checkbox
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("authors", authorService.getAllAuthorsForCreate());
        return "pages/admin-book-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        model.addAttribute("bookForm", bookService.getBookFormById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("authors", authorService.getAllAuthorsForCreate());
        return "pages/admin-book-form";
    }

    @PostMapping({"/add", "/edit"})
    public String saveBook(
            @Valid @ModelAttribute("bookForm") BookFormDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("authors", authorService.getAllAuthorsForCreate());
            return "pages/admin-book-form";
        }
        try {
            bookService.saveBook(dto);
            if (dto.getBookId() == null) {
                redirectAttributes.addFlashAttribute("success", "Thêm sách thành công!");
            } else {
                redirectAttributes.addFlashAttribute("success", "Cập nhật sách thành công!");
            }
            return "redirect:/admin/books";
        } catch (Exception e) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("authors", authorService.getAllAuthorsForCreate());
            model.addAttribute("error", e.getMessage());
            return "pages/admin-book-form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("success", "Xóa sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/books";
    }
}