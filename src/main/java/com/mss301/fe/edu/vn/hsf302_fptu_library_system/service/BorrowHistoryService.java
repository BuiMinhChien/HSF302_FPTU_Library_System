package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowHistory;
import org.springframework.data.domain.Page;

public interface BorrowHistoryService {
    // Lấy danh sách lịch sử mượn sách của user đang đăng nhập
    Page<BorrowHistory> getCurrentUserHistory(String keyword, int page, int size);
    //phần dành cho thủ thư lấy lịch sử người muon sach trong tuan
    Page<BorrowHistory> getBorrowersThisWeek(String keyword, Boolean isReturned, int page, int size);
}