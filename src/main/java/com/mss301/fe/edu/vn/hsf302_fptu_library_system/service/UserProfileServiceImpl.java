package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.UserProfileDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.User;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.util.CommonFunction;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final CommonFunction commonFunction;

    @Override
    public UserProfileDto getCurrentUserProfile() {
        User user = commonFunction.getCurrentUser();
        // Chuyển từ Entity sang DTO — chỉ lấy đúng thông tin cần hiển thị
        return UserProfileDto.builder()
                .fullName(user.getFullName())
                .code(user.getCode())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .role(user.getRole())
                .avatarUrl(user.getAvatar().getFileUrl())
                .build();
    }
}
