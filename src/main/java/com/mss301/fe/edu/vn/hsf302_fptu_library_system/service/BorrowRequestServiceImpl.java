package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBookCopyStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BorrowRequestDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Book;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.User;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.mapper.BorrowRequestMapper;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BookRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BorrowRequestRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BorrowRequestServiceImpl implements BorrowRequestService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowRequestRepository borrowRequestRepository;
    private final BorrowRequestMapper borrowRequestMapper;

    @Override
    public String createBorrowRequest(Integer bookId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found"));
        long availableCopies = book.getBookCopies()
                .stream()
                .filter(copy -> copy.getStatus() == EBookCopyStatus.AVAILABLE)
                .count();
        if (availableCopies <= 0) {
            return "Không có cuốn sách nào sẵn sàng để mượn";
        }
        boolean existedRequest = borrowRequestRepository.existsByUserAndBookAndStatus(
                user,
                book,
                EBorrowRequestStatus.PENDING
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
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
}
