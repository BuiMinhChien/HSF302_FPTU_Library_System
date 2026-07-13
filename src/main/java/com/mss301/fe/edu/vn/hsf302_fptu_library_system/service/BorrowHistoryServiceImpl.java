package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBookCopyStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowHistoryStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BookCopy;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowHistory;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.User;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BookCopyRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BorrowHistoryRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BorrowRequestRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.util.CommonFunction;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BorrowHistoryServiceImpl implements BorrowHistoryService {

    private final BorrowHistoryRepository borrowHistoryRepository;
    private final BorrowRequestRepository borrowRequestRepository;
    private final BookCopyRepository bookCopyRepository;
    private final CommonFunction commonFunction;

    // ════════════════════════════════════════════════
    // HÀM CŨ — giữ nguyên logic của nhóm
    // ════════════════════════════════════════════════
    @Override
    public Page<BorrowHistory> getCurrentUserHistory(String keyword, int page, int size) {
        User user = commonFunction.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("borrowDate").descending());
        return borrowHistoryRepository.search(user.getUserId(), keyword, pageable);

    }

    @Override
    public BorrowHistory issueBook(Integer requestId) {
        User librarian = commonFunction.getCurrentUser();

        BorrowRequest request = borrowRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu #" + requestId));

        if (request.getStatus() != EBorrowRequestStatus.WAITING) {
            throw new RuntimeException("Yêu cầu này chưa được duyệt, không thể giao sách!");
        }

        BookCopy copy = bookCopyRepository
                .findFirstByBookAndStatus(request.getBook(), EBookCopyStatus.AVAILABLE)
                .orElseThrow(() -> new RuntimeException("Hiện không có bản sao sách nào trống!"));

        copy.setStatus(EBookCopyStatus.BORROWED);
        bookCopyRepository.save(copy);

        request.setStatus(EBorrowRequestStatus.ISSUED);
        borrowRequestRepository.save(request);

        BorrowHistory history = BorrowHistory.builder()
                .user(request.getUser())
                .copy(copy)
                .issuedBy(librarian)
                .borrowDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(14))
                .build();

        return borrowHistoryRepository.save(history);
    }

    @Override
    public BorrowHistory confirmReturn(Integer borrowId) {
        User librarian = commonFunction.getCurrentUser();

        BorrowHistory history = borrowHistoryRepository.findById(borrowId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch sử mượn #" + borrowId));

        if (history.getReturnDate() != null) {
            throw new RuntimeException("Sách này đã được xác nhận trả rồi!");
        }

        history.setReturnDate(LocalDateTime.now());
        history.setReturnConfirmedBy(librarian);
        history.setStatus(EBorrowHistoryStatus.RETURNED);
        borrowHistoryRepository.save(history);

        BookCopy copy = history.getCopy();
        copy.setStatus(EBookCopyStatus.AVAILABLE);
        bookCopyRepository.save(copy);


        List<BorrowRequest> waitingList = borrowRequestRepository
                .findByStatusOrderByCreatedAtAsc(EBorrowRequestStatus.WAITING);

        BorrowRequest nextPerson = waitingList.stream()
                .filter(req -> req.getBook().getBookId().equals(copy.getBook().getBookId()))
                .findFirst()
                .orElse(null);

        if (nextPerson != null) {
            nextPerson.setStatus(EBorrowRequestStatus.APPROVED);
            nextPerson.setApprovedBy(librarian);
            nextPerson.setApprovedDate(LocalDateTime.now());
            borrowRequestRepository.save(nextPerson);
        }

        return history;
    }
}