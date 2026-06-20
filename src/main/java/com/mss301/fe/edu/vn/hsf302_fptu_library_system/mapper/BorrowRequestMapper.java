package com.mss301.fe.edu.vn.hsf302_fptu_library_system.mapper;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BorrowRequestDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowRequest;
import org.springframework.stereotype.Component;

@Component
public class BorrowRequestMapper {
    public BorrowRequestDto toDTO(BorrowRequest request) {
        return BorrowRequestDto.builder()
                .requestId(request.getRequestId())
                .bookId(request.getBook().getBookId())
                .bookTitle(request.getBook().getTitle())
                .userId(request.getUser().getUserId())
                .studentCode(request.getUser().getCode())
                .username(request.getUser().getFullName())
                .status(request.getStatus())
                .rejectionReason(request.getRejectionReason())
                .approvedById(
                        request.getApprovedBy() != null
                                ? request.getApprovedBy().getUserId()
                                : null
                )
                .approvedByName(
                        request.getApprovedBy() != null
                                ? request.getApprovedBy().getFullName()
                                : null
                )
                .approvedDate(request.getApprovedDate())
                .createdDate(request.getCreatedAt())
                .build();
    }
}
