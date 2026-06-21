package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BorrowRequestDto;
import org.springframework.data.domain.Page;

public interface BorrowRequestService {
    String createBorrowRequest(Integer bookId, String email);
    Page<BorrowRequestDto> getCurrentUserRequests(
            String keyword,
            EBorrowRequestStatus status,
            int page,
            int size
    );
    Page<BorrowRequestDto> getAllRequests(
            String bookTitle,
            String fullName,
            String studentCode,
            EBorrowRequestStatus status,
            int page,
            int size
    );
    void cancelPendingRequest(Integer requestId);
    void rejectRequest(Integer requestId, String rejectionReason);
    void approveRequest(Integer requestId);
}
