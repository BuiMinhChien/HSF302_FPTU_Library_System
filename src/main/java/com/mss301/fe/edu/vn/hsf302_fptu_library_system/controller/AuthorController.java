package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.AuthorFormDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping
    public String listAuthors(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<AuthorFormDto> authorPage = authorService.getAllAuthors(keyword, page, size);
        model.addAttribute("authorPage", authorPage);
        model.addAttribute("keyword", keyword);
        return "pages/admin-author-list";
    }

    @PostMapping("/add")
    public String addAuthor(@ModelAttribute AuthorFormDto form, RedirectAttributes redirectAttributes) {
        try {
            authorService.save(form);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm tác giả thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/authors";
    }

    @PostMapping("/edit/{id}")
    public String editAuthor(@PathVariable Integer id, @ModelAttribute AuthorFormDto form, RedirectAttributes redirectAttributes) {
        try {
            authorService.update(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật tác giả thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/authors";
    }

    @PostMapping("/delete/{id}")
    public String deleteAuthor(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            authorService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa tác giả thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/authors";
    }
}