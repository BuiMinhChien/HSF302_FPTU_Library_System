package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.LibraryStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/librarian/statistics")
public class LibraryStatisticController {

    private final LibraryStatisticService libraryStatisticService;

    @GetMapping
    public String viewStatistics(Model model) {
        Map<String, Long> stats = libraryStatisticService.getStatistics();
        model.addAttribute("stats", stats);
        return "pages/library-statistics";
    }
}
