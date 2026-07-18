package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.LibraryStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class LibraryStatisticController {

    private final LibraryStatisticService libraryStatisticService;

    // Thống kê cho Thủ thư (Librarian)
    @GetMapping("/librarian/statistics")
    public String viewLibrarianStatistics(Model model) {
        Map<String, Object> stats = libraryStatisticService.getStatistics();
        model.addAttribute("stats", stats);
        return "pages/library-statistics";
    }

    // Thống kê cho Quản trị viên (Admin)
    @GetMapping("/admin/statistics")
    public String viewAdminStatistics(Model model) {
        Map<String, Object> stats = libraryStatisticService.getAdminStatistics();
        model.addAttribute("stats", stats);
        return "pages/admin-statistics";
    }
}
