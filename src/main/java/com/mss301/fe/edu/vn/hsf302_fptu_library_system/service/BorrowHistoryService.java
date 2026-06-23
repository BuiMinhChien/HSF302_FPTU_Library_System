package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowHistory;
import org.springframework.data.domain.Page;

public interface BorrowHistoryService {
    // Lấy danh sách lịch sử mượn sách của user đang đăng nhập
    // keyword từ khóa tìm kiếm theo tên sách (có thể null)
    // page trang hiện tại (bắt đầu từ 0)
    // size số bản ghi mỗi trang
    Page<BorrowHistory> getCurrentUserHistory(String keyword, int page, int size);
}