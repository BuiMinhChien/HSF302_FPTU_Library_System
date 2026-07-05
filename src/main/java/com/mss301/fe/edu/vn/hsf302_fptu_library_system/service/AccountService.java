package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ERole;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.User;
import org.springframework.data.domain.Page;

public interface AccountService {
    //lấy danh sách tài khoản
    Page<User> searchAccounts(String keyword, ERole role, int page, int size);
    
    //mở hoặc khóa danh sách tài khoản
    void toggleAccountStatus(Integer userId);
}
