package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.CategoryDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Category;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
        Page<CategoryDto> categoryPage = categoryService.getAllCategories(keyword, page, size);
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("keyword", keyword);
        return "pages/admin-category-list";
    }

    @PostMapping("/add")
    public String addCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        try {
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("success", "Thêm danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/edit/{id}")
    public String editCategory(@PathVariable Integer id, @ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        try {
            categoryService.update(id, category);
            redirectAttributes.addFlashAttribute("success", "Cập nhật danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}