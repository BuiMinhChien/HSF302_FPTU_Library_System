package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowHistory;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.User;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BorrowHistoryRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.util.CommonFunction;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service        // Đánh dấu đây là tầng Service, Spring sẽ quản lý bean này
@Transactional  // Mọi method trong class này đều chạy trong 1 transaction DB
@RequiredArgsConstructor // Lombok: tự tạo constructor inject các field final bên dưới
public class BorrowHistoryServiceImpl implements BorrowHistoryService {

    private final BorrowHistoryRepository borrowHistoryRepository; // tầng truy cập DB
    private final CommonFunction commonFunction; // utility lấy thông tin user đang login

    @Override
    public Page<BorrowHistory> getCurrentUserHistory(String keyword, int page, int size) {

        // step 1: Lấy thông tin user đang đăng nhập từ Security Context
        // CommonFunction.getCurrentUser() có sẵn ròi
        User user = commonFunction.getCurrentUser();

        // step 2: Tạo đối tượng phân trang
        // - page, size: số trang và số bản ghi mỗi trang
        // - Sort.by("borrowDate").descending(): sắp xếp theo ngày mượn, mới nhất lên đầu
        Pageable pageable = PageRequest.of(page, size, Sort.by("borrowDate").descending());

        // step 3: Gọi rep để query data rồi trả về kết quả
        return borrowHistoryRepository.search(user.getUserId(), keyword, pageable);
    }
}