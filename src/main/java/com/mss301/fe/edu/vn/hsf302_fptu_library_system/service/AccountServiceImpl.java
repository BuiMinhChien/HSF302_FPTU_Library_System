package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ERole;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.AccountFormDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.User;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<User> searchAccounts(String keyword, ERole role, Boolean status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("userId").descending());
        return userRepository.searchAccounts(keyword, role, status, pageable);
    }

    @Override
    @Transactional
    public void toggleAccountStatus(Integer userId) {
        //lấy taài khoản tu data lên
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản này!"));
        
      //đảo ngược trạng thái,nếu bật thì tắt, nếu tắt thì bật
        user.setStatus(!user.getStatus());
        
        //lưu lại
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void saveAccount(AccountFormDto dto) {
        User user;
        if (dto.getUserId() != null) {
            // Sửa tài khoản
            user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản cần sửa!"));
            
            // Cập nhật mật khẩu nếu có nhập mới
            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
        } else {
            // Thêm mới tài khoản
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new RuntimeException("Email đã được sử dụng!");
            }
            
            user = new User();
            user.setCode(dto.getCode());
            user.setStatus(true); // Mặc định mở khóa
            
            // Yêu cầu mật khẩu khi tạo mới
            if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
                throw new RuntimeException("Vui lòng nhập mật khẩu cho tài khoản mới!");
            }
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Cập nhật thông tin chung
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setRole(dto.getRole());
        
        userRepository.save(user);
    }
}
