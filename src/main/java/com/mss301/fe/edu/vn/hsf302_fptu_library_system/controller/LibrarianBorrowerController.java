package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BorrowRequestDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.BorrowRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/librarian/borrowers-this-week")
@RequiredArgsConstructor
public class LibrarianBorrowerController {

    private final BorrowRequestService borrowRequestService;

    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public String viewBorrowersThisWeek(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Page<BorrowRequestDto> requestPage = borrowRequestService.getBorrowersThisWeek(keyword, page, size);

        model.addAttribute("requests", requestPage.getContent());
        model.addAttribute("requestPage", requestPage);
        model.addAttribute("keyword", keyword);
        return "pages/librarian-borrowers-this-week";
    }

    @PostMapping("/{id}/issue")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public String issueBook(@PathVariable("id") Integer requestId, RedirectAttributes redirectAttributes) {
        try {
            borrowRequestService.issueBook(requestId);
            redirectAttributes.addFlashAttribute("success", "Đã xác nhận giao sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/librarian/borrowers-this-week";
    }
}