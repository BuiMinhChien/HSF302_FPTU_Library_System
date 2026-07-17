package com.mss301.fe.edu.vn.hsf302_fptu_library_system.controller;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.UserProfileDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping
    public String viewProfile(Model model) {
        UserProfileDto user = userProfileService.getCurrentUserProfile();
        model.addAttribute("user", user);
        return "pages/profile";
    }
}
