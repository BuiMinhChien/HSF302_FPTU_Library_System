package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBookCopyStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ENewBookRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowHistory;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Fine;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.NewBookRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class LibraryStatisticServiceImpl implements LibraryStatisticService {

    private final UserRepository userRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BorrowRequestRepository borrowRequestRepository;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final NewBookRequestRepository newBookRequestRepository;
    private final BorrowHistoryRepository borrowHistoryRepository;
    private final FineRepository fineRepository;

    // ─────────────────────────────────────────────────────────────────────
    // THỦ THƯ: Tài nguyên kho sách + vận hành hiện tại + 4 biểu đồ biến động
    // ─────────────────────────────────────────────────────────────────────
    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // Tài nguyên kho sách
        stats.put("totalBooks", bookRepository.count());
        stats.put("totalBookCopies", bookCopyRepository.count());
        stats.put("totalCategories", categoryRepository.count());
        stats.put("totalAuthors", authorRepository.count());

        // Vận hành hiện tại
        stats.put("totalBorrowed", bookCopyRepository.countByStatusAndDeleteFlagFalse(EBookCopyStatus.BORROWED));
        stats.put("totalAvailable", bookCopyRepository.countByStatusAndDeleteFlagFalse(EBookCopyStatus.AVAILABLE));
        stats.put("totalPending",
                (long) borrowRequestRepository
                        .findByStatusOrderByCreatedAtAsc(EBorrowRequestStatus.PENDING).size());
        stats.put("totalWaiting",
                (long) borrowRequestRepository
                        .findByStatusOrderByCreatedAtAsc(EBorrowRequestStatus.WAITING).size());

        // Hoạt động giao dịch toàn thời gian
        stats.put("totalBorrowHistories", borrowHistoryRepository.count());
        stats.put("totalFines", fineRepository.count());
        stats.put("totalBorrowRequests", borrowRequestRepository.count());

        // 4 biểu đồ biến động 7 ngày gần đây
        LocalDateTime sevenDaysAgo = LocalDate.now().minusDays(6).atStartOfDay();

        List<BorrowHistory>  recentBorrows       = borrowHistoryRepository.findByBorrowDateAfter(sevenDaysAgo);
        List<Fine>           recentFines         = fineRepository.findByCreatedAtAfter(sevenDaysAgo);
        List<BorrowRequest>  recentBorrowReqs    = borrowRequestRepository.findByCreatedAtAfter(sevenDaysAgo);
        List<NewBookRequest> recentNewBookReqs   = newBookRequestRepository.findByCreatedAtAfter(sevenDaysAgo);

        List<String> trendLabels            = new ArrayList<>();
        List<Long>   trendValues            = new ArrayList<>();
        List<Long>   trendFinesValues       = new ArrayList<>();
        List<Long>   trendBorrowReqValues   = new ArrayList<>();
        List<Long>   trendNewBookReqValues  = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            trendLabels.add(date.format(fmt));

            trendValues.add(recentBorrows.stream()
                    .filter(bh -> bh.getBorrowDate() != null && bh.getBorrowDate().toLocalDate().isEqual(date))
                    .count());
            trendFinesValues.add(recentFines.stream()
                    .filter(f -> f.getCreatedAt() != null && f.getCreatedAt().toLocalDate().isEqual(date))
                    .count());
            trendBorrowReqValues.add(recentBorrowReqs.stream()
                    .filter(br -> br.getCreatedAt() != null && br.getCreatedAt().toLocalDate().isEqual(date))
                    .count());
            trendNewBookReqValues.add(recentNewBookReqs.stream()
                    .filter(nbr -> nbr.getCreatedAt() != null && nbr.getCreatedAt().toLocalDate().isEqual(date))
                    .count());
        }

        stats.put("trendLabels",              trendLabels);
        stats.put("trendValues",              trendValues);
        stats.put("trendFinesValues",         trendFinesValues);
        stats.put("trendBorrowRequestsValues",trendBorrowReqValues);
        stats.put("trendNewBookRequestsValues",trendNewBookReqValues);

        return stats;
    }

    // ─────────────────────────────────────────────────────────────────────
    // ADMIN: Quản lý hệ thống – tài khoản + hoạt động tổng thể hệ thống
    // ─────────────────────────────────────────────────────────────────────
    @Override
    public Map<String, Object> getAdminStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // 1. Tổng quan nhanh
        stats.put("totalUsers",           userRepository.count());
        stats.put("totalFines",           fineRepository.count());
        stats.put("totalBorrowHistories", borrowHistoryRepository.count());

        // 2. Trạng thái yêu cầu mượn hiện tại
        stats.put("pendingBorrowRequests",
                (long) borrowRequestRepository
                        .findByStatusOrderByCreatedAtAsc(EBorrowRequestStatus.PENDING).size());
        stats.put("waitingBorrowRequests",
                (long) borrowRequestRepository
                        .findByStatusOrderByCreatedAtAsc(EBorrowRequestStatus.WAITING).size());

        // 4. Yêu cầu bổ sung sách mới theo trạng thái
        stats.put("newBookRequestPending",
                newBookRequestRepository.countByStatus(ENewBookRequestStatus.PENDING));
        stats.put("newBookRequestApproved",
                newBookRequestRepository.countByStatus(ENewBookRequestStatus.APPROVED));
        stats.put("newBookRequestRejected",
                newBookRequestRepository.countByStatus(ENewBookRequestStatus.REJECTED));

        return stats;
    }
}
