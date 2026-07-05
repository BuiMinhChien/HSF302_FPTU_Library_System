package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBookCopyStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BookCopyRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BorrowRequestRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class LibraryStatisticServiceImpl implements LibraryStatisticService {

    private final UserRepository userRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BorrowRequestRepository borrowRequestRepository;

    @Override
    public Map<String, Long> getStatistics() {
        Map<String, Long> stats = new LinkedHashMap<>();

        stats.put("totalUsers", userRepository.count());
        stats.put("totalBorrowed", bookCopyRepository.countByStatus(EBookCopyStatus.BORROWED));
        stats.put("totalAvailable", bookCopyRepository.countByStatus(EBookCopyStatus.AVAILABLE));
        stats.put("totalPending",
                (long) borrowRequestRepository
                        .findByStatusOrderByCreatedAtAsc(EBorrowRequestStatus.PENDING).size());
        stats.put("totalWaiting",
                (long) borrowRequestRepository
                        .findByStatusOrderByCreatedAtAsc(EBorrowRequestStatus.WAITING).size());

        return stats;
    }
}
