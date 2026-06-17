package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @GetMapping({"/", "/home"})
    public String homePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Lấy username (email) của người đang login
        String username = (userDetails != null) ? userDetails.getUsername() : "Khách";
        return "pages/home";
    }
}
