package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ENewBookRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.NewBookRequest;
import org.springframework.data.domain.Page;

public interface NewBookRequestService {
    // Xem danh sách yêu cầu của bản thân có lọc và phân trang
    Page<NewBookRequest> searchMyRequests(String keyword, ENewBookRequestStatus status, java.time.LocalDate fromDate, java.time.LocalDate toDate, int page, int size);

    //tạo mới yêu caauf
    void createRequest(NewBookRequest request);
    // Dành cho admin/thủ thư lấy tất cả danh sách
    Page<NewBookRequest> searchAllRequests(String keyword, ENewBookRequestStatus status, java.time.LocalDate fromDate, int page, int size);

    // hàm duyệt yêu cầu
    void approveRequest(Integer requestId);

    // hàm từ chối có kèm lý do
    void rejectRequest(Integer requestId, String rejectionReason);
}
