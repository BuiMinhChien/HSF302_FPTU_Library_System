package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BorrowHistoryRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowHistory;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBookCopyStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BorrowRequestDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Book;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BookCopy;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.User;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.mapper.BorrowRequestMapper;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BookCopyRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BookRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BorrowRequestRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.UserRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.util.CommonFunction;
import jakarta.persistence.EntityNotFoundException;
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
public class BorrowRequestServiceImpl implements BorrowRequestService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowRequestRepository borrowRequestRepository;
    private final BorrowRequestMapper borrowRequestMapper;
    private final CommonFunction commonFunction;
    private final EmailService emailService;
    private final BookCopyRepository bookCopyRepository;
    private final BorrowHistoryRepository borrowHistoryRepository;

    @Override
    public String createBorrowRequest(Integer bookId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found"));
        boolean existedRequest =
                borrowRequestRepository.existsByUserAndBookAndStatusNotIn(
                        user,
                        book,
                        List.of(
                                EBorrowRequestStatus.REJECTED,
                                EBorrowRequestStatus.CANCELLED,
                                EBorrowRequestStatus.EXPIRED
                        )
                );
        if (existedRequest) {
            return "Bạn đã mượn cuốn sách này";
        }
        BorrowRequest request = BorrowRequest.builder()
                .user(user)
                .book(book)
                .status(EBorrowRequestStatus.PENDING)
                .build();
        borrowRequestRepository.save(request);
        return null;
    }

    @Override
    public Page<BorrowRequestDto> getCurrentUserRequests(
            String keyword,
            EBorrowRequestStatus status,
            int page,
            int size
    ) {
        User user = commonFunction.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return borrowRequestRepository
                .search(
                        user.getUserId(),
                        keyword,
                        status,
                        pageable
                )
                .map(borrowRequestMapper::toDTO);
    }

    @Override
    public void cancelPendingRequest(Integer requestId) {
        User user = commonFunction.getCurrentUser();
        BorrowRequest request = borrowRequestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Request not found"));
        if (!request.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }
        if (request.getStatus() != EBorrowRequestStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be deleted");
        }
        request.setStatus(EBorrowRequestStatus.CANCELLED);
        borrowRequestRepository.save(request);
    }

    @Override
    public Page<BorrowRequestDto> getAllRequests(
            String bookTitle,
            String fullName,
            String studentCode,
            EBorrowRequestStatus status,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return borrowRequestRepository
                .search(
                        bookTitle,
                        fullName,
                        studentCode,
                        status,
                        pageable
                )
                .map(borrowRequestMapper::toDTO);
    }

    @Override
    public void rejectRequest(
            Integer requestId,
            String rejectionReason
    ) {
        User user = commonFunction.getCurrentUser();
        BorrowRequest request = borrowRequestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Borrow request not found"));
        request.setStatus(EBorrowRequestStatus.REJECTED);
        request.setRejectionReason(rejectionReason);
        request.setApprovedBy(user);
        request.setApprovedDate(LocalDateTime.now());
        borrowRequestRepository.save(request);
    }

    @Override
    public void approveRequest(Integer requestId) {
        User librarian = commonFunction.getCurrentUser();
        BorrowRequest request = borrowRequestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Borrow request not found"));
        if (request.getStatus() != EBorrowRequestStatus.PENDING) {
            throw new RuntimeException("Request is not pending");
        }
        BookCopy availableCopy = request.getBook()
                .getBookCopies()
                .stream()
                .filter(copy -> copy.getStatus() == EBookCopyStatus.AVAILABLE)
                .findFirst()
                .orElse(null);
        request.setApprovedBy(librarian);
        request.setApprovedDate(LocalDateTime.now());
        if (availableCopy != null) {
            request.setStatus(EBorrowRequestStatus.WAITING);
            request.setReservedCopy(availableCopy);
            availableCopy.setStatus(EBookCopyStatus.RESERVED);
            bookCopyRepository.save(availableCopy);
            try {
                emailService.sendWaitingBookNotification(
                        request.getUser().getEmail(),
                        request.getUser().getFullName(),
                        request.getBook().getTitle()
                );
            } catch (Exception e) {
                throw new RuntimeException("Send mail failed");
            }
        } else {
            request.setStatus(EBorrowRequestStatus.APPROVED);
        }
        borrowRequestRepository.save(request);
        //
    }
    @Override
    public Page<BorrowRequestDto> getBorrowersThisWeek(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "approvedDate"));
        return borrowRequestRepository.findBorrowersThisWeek(keyword, pageable)
                .map(borrowRequestMapper::toDTO);
    }
    @Override
    public void issueBook(Integer requestId) {
        User librarian = commonFunction.getCurrentUser();
        BorrowRequest request = borrowRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Borrow request not found"));

        if (request.getStatus() != EBorrowRequestStatus.WAITING) {
            throw new RuntimeException("Request is not in WAITING status");
        }

        request.setStatus(EBorrowRequestStatus.ISSUED);
        borrowRequestRepository.save(request);
        BookCopy copy = request.getReservedCopy();
        if (copy != null) {
            copy.setStatus(EBookCopyStatus.BORROWED);
            bookCopyRepository.save(copy);
        }
        BorrowHistory history = BorrowHistory.builder()
                .user(request.getUser())
                .copy(copy)
                .issuedBy(librarian)
                .borrowDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(7))
                .build();
        borrowHistoryRepository.save(history);
    }
}
