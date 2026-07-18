package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ERole;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.User;
import org.springframework.data.domain.Page;

public interface AccountService {
    // Lấy danh sách tài khoản theo từ khóa, role và status
    Page<User> searchAccounts(String keyword, ERole role, Boolean status, int page, int size);
    
    //mở hoặc khóa danh sách tài khoản
    void toggleAccountStatus(Integer userId);

    // Lưu tài khoản (Thêm mới hoặc Cập nhật)
    void saveAccount(com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.AccountFormDto dto);
}
