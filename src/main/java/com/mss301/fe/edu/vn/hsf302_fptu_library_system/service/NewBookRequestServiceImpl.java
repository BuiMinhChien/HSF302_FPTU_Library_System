package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ENewBookRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.NewBookRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.User;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.NewBookRequestRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.util.CommonFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NewBookRequestServiceImpl implements NewBookRequestService {
    private final NewBookRequestRepository newBookRequestRepository;
    private final CommonFunction commonFunction;
    @Override
    public Page<NewBookRequest> searchMyRequests(ENewBookRequestStatus status, int page, int size) {
        // Tự động lấy User đang đăng nhập hiện tại
        User currentUser = commonFunction.getCurrentUser();
        //biến phaan trang
        Pageable pageable = PageRequest.of(page, size, Sort.by("requestDate").descending());
        return newBookRequestRepository.searchByUser(currentUser.getUserId(), status, pageable);
    }

    @Override
    @Transactional
    public void createRequest(NewBookRequest request) {
        //tìm xem ông nào đang đăng nhập để gán vào làm chủ nhân yêu cầu
        User currentUser = commonFunction.getCurrentUser();
        request.setUser(currentUser);
        //ép trạng thái ban đầu luôn luôn là chờ duyệt
        request.setStatus(ENewBookRequestStatus.PENDING);
        request.setRequestDate(LocalDateTime.now());
        newBookRequestRepository.save(request);
    }
    @Override
    public Page<NewBookRequest> searchAllRequests(ENewBookRequestStatus status, int page, int size) {
        // Sắp xếp ngày tạo từ mới nhất xuống cũ nhất
        Pageable pageable = PageRequest.of(page, size, Sort.by("requestDate").descending());
        // Gọi thẳng hàm mới viết ở Repository
        return newBookRequestRepository.searchAllRequests(status, pageable);
    }
    @Override
    @Transactional
    public void approveRequest(Integer requestId) {
        //tìm cái yêu cầu đó, nếu không có thì quăng ra lỗi
        NewBookRequest request = newBookRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu này!"));
        //chuyển sang trạng thái status
        request.setStatus(ENewBookRequestStatus.APPROVED);
        //đánh dấu người duyệt và ngày duyệt
        request.setApprovedBy(commonFunction.getCurrentUser());
        request.setApprovedDate(LocalDateTime.now());
        // lưu
        newBookRequestRepository.save(request);
    }
    @Override
    @Transactional
    public void rejectRequest(Integer requestId) {
        //tìm ra yêu cầu đó
        NewBookRequest request = newBookRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu này!"));
        request.setStatus(ENewBookRequestStatus.REJECTED);
        request.setApprovedBy(commonFunction.getCurrentUser());
        request.setApprovedDate(LocalDateTime.now());

        // 4. Lưu lại
        newBookRequestRepository.save(request);
    }
}