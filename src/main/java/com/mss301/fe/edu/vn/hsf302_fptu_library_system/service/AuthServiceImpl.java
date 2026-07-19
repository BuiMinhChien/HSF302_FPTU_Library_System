package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.User;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.UserRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.util.CommonFunction;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CommonFunction commonFunction;

    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));
        // Tạo mật khẩu mới
        String newPassword = commonFunction.generateRandomPassword();
        // Gửi email trước
        try {
            emailService.sendNewPasswordEmail(
                    user.getEmail(),
                    user.getFullName(),
                    newPassword
            );
        } catch (Exception e) {
            throw new RuntimeException("Send mail failed");
        }
        // Hash password
        user.setPassword(passwordEncoder.encode(newPassword));
        // Lưu DB
        userRepository.save(user);
    }

    @Override
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));
        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }
        // Hash password
        user.setPassword(passwordEncoder.encode(newPassword));
        // Lưu DB
        userRepository.save(user);
        return true;
    }
}
