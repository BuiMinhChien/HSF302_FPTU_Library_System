package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBookCopyStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowHistoryStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BorrowHistoryDto;
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

@Service
@Transactional
@RequiredArgsConstructor
public class BorrowHistoryServiceImpl implements BorrowHistoryService {

    private final BorrowHistoryRepository borrowHistoryRepository;
    private final BorrowRequestRepository borrowRequestRepository;
    private final BookCopyRepository bookCopyRepository;
    private final CommonFunction commonFunction;

    @Override
    public Page<BorrowHistory> getCurrentUserHistory(String keyword, java.time.LocalDate fromDate, java.time.LocalDate toDate, int page, int size) {
        User user = commonFunction.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("borrowDate").descending());

        LocalDateTime startDateTime = (fromDate != null) ? fromDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (toDate != null) ? toDate.atTime(23, 59, 59) : null;

        return borrowHistoryRepository.search(user.getUserId(), keyword, startDateTime, endDateTime, pageable);
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
                .findFirstByBookAndStatusAndDeleteFlagFalse(request.getBook(), EBookCopyStatus.AVAILABLE)
                .orElseThrow(() -> new RuntimeException("Hiện không có bản sao sách nào trống!"));

        copy.setStatus(EBookCopyStatus.BORROWED);
        bookCopyRepository.save(copy);

        request.setStatus(EBorrowRequestStatus.ISSUED);
        borrowRequestRepository.save(request);

        BorrowHistory history = BorrowHistory.builder()
                .user(request.getUser())
                .copy(copy)
                .status(EBorrowHistoryStatus.BORROWING)
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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch sử mượn mã: " + borrowId));

        if (history.getStatus().equals(EBorrowHistoryStatus.RETURNED)) {
            throw new RuntimeException("Sách này đã được xác nhận trả rồi!");
        }

        history.setReturnDate(LocalDateTime.now());
        history.setReturnConfirmedBy(librarian);
        history.setStatus(EBorrowHistoryStatus.RETURNED);
        borrowHistoryRepository.save(history);

        BookCopy copy = history.getCopy();
        copy.setStatus(EBookCopyStatus.AVAILABLE);
        bookCopyRepository.save(copy);

//        List<BorrowRequest> waitingList = borrowRequestRepository
//                .findByStatusOrderByCreatedAtAsc(EBorrowRequestStatus.WAITING);
//
//        BorrowRequest nextPerson = waitingList.stream()
//                .filter(req -> req.getBook().getBookId().equals(copy.getBook().getBookId()))
//                .findFirst()
//                .orElse(null);
//
//        if (nextPerson != null) {
//            nextPerson.setStatus(EBorrowRequestStatus.APPROVED);
//            nextPerson.setApprovedBy(librarian);
//            nextPerson.setApprovedDate(LocalDateTime.now());
//            borrowRequestRepository.save(nextPerson);
//        }
        return history;
    }

    @Override
    public Page<BorrowHistoryDto> getActiveBorrows(
            String fullName,
            String bookTitle,
            EBorrowHistoryStatus status,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "borrowDate")
        );
        return borrowHistoryRepository
                .searchActiveBorrows(
                        fullName,
                        bookTitle,
                        status,
                        pageable
                )
                .map(this::toDto);
    }

    private BorrowHistoryDto toDto(BorrowHistory entity) {
        return BorrowHistoryDto.builder()
                .borrowId(entity.getBorrowId())
                .studentName(entity.getUser().getFullName())
                .studentCode(entity.getUser().getCode())
                .bookTitle(entity.getCopy().getBook().getTitle())
                .borrowDate(entity.getBorrowDate())
                .dueDate(entity.getDueDate())
                .status(entity.getStatus())
                .build();
    }
}