package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BookFormDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.AuthorService;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.BookService;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    // --- CHỨC NĂNG THÊM SÁCH ---
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("bookForm", new BookFormDto());
        // Lấy danh sách category và author để hiển thị checkbox
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("authors", authorService.getAllAuthors(null, 0, 1000).getContent());
        return "pages/admin-book-form";
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute BookFormDto bookForm, RedirectAttributes redirectAttributes) {
        try {
            bookService.createBook(bookForm);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/books";
    }

    // --- CHỨC NĂNG SỬA SÁCH ---
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        model.addAttribute("bookForm", bookService.getBookFormById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("authors", authorService.getAllAuthors(null, 0, 1000).getContent());
        return "pages/admin-book-form";
    }

    @PostMapping("/edit/{id}")
    public String editBook(@PathVariable Integer id, @ModelAttribute BookFormDto bookForm, RedirectAttributes redirectAttributes) {
        try {
            bookService.updateBook(id, bookForm);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/books";
    }

    // --- CHỨC NĂNG XÓA SÁCH ---
    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/books";
    }
}