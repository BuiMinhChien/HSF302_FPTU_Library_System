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
        User user = commonFunction.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("borrowDate").descending());
        return borrowHistoryRepository.search(user.getUserId(), keyword, pageable);

    }
    @Override
    public Page<BorrowHistory> getBorrowersThisWeek(String keyword, Boolean isReturned, int page, int size) {
        //Tính toán ngày bắt đầu và kết thúc của tuần này
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        //thuws 2 lúc 00:00
        java.time.LocalDateTime startOfWeek = now.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                .withHour(0).withMinute(0).withSecond(0);
        // Chủ nhật lúc 23:59:59
        java.time.LocalDateTime endOfWeek = now.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY))
                .withHour(23).withMinute(59).withSecond(59);
        // Tạo đối tượng phân trang sắp xếp ngaày mượn gần nhất lên đầu
        Pageable pageable = PageRequest.of(page, size, Sort.by("borrowDate").descending());
        return borrowHistoryRepository.findBorrowersThisWeek(startOfWeek, endOfWeek, keyword, isReturned, pageable);
    }
}