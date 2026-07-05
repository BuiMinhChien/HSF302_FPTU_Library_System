package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ERole;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.User;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    @Override
    public Page<User> searchAccounts(String keyword, ERole role, int page, int size) {
        // mặc định sắp xếp theo danh sách tăng dần
        Pageable pageable = PageRequest.of(page, size, Sort.by("code").ascending());
        return userRepository.searchAccounts(keyword, role, pageable);
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
}
