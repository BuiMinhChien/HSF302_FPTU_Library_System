package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowHistoryStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BorrowHistoryDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowHistory;
import org.springframework.data.domain.Page;

public interface BorrowHistoryService {

    // Hàm CŨ của nhóm — không thay đổi logic
    Page<BorrowHistory> getCurrentUserHistory(String keyword, java.time.LocalDate fromDate, java.time.LocalDate toDate, int page, int size);

    // Hàm MỚI — Giao sách vật lý cho sinh viên
    BorrowHistory issueBook(Integer requestId);

    // Hàm MỚI — Xác nhận sinh viên đã trả sách + FIFO
    BorrowHistory confirmReturn(Integer borrowId);

    Page<BorrowHistoryDto> getActiveBorrows(
            String fullName,
            String bookTitle,
            EBorrowHistoryStatus status,
            int page,
            int size
    );
}