package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.AuthorFormDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
        model.addAttribute("authors", authorPage.getContent());
        model.addAttribute("keyword", keyword);
        return "pages/admin-author-list";
    }

    @GetMapping("/add")
    public String addAuthorPage(Model model) {
        model.addAttribute("authorForm", new AuthorFormDto());
        return "pages/author-form";
    }

    @GetMapping("/edit/{id}")
    public String editAccountPage(@PathVariable Integer id, Model model) {
        AuthorFormDto dto = authorService.getById(id);
        model.addAttribute("authorForm", dto);
        return "pages/author-form";
    }

    @PostMapping({"/add","/edit"})
    public String saveAuthor(
            @Valid @ModelAttribute("authorForm") AuthorFormDto form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "pages/author-form";
        }
        try {
            authorService.save(form);
            if (form.getAuthorId() != null) {
                redirectAttributes.addFlashAttribute("success", "Cập nhật tác giả thành công!");
            } else {
                redirectAttributes.addFlashAttribute("success", "Thêm tác giả thành công!");
            }
            return "redirect:/admin/authors";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            if (form.getAuthorId() != null) {
                return "redirect:/admin/authors/edit/" + form.getAuthorId();
            } else {
                return "redirect:/admin/authors/add";
            }
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteAuthor(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            authorService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa tác giả thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/authors";
    }
}