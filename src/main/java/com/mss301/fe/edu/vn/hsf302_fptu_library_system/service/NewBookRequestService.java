package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ENewBookRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.NewBookRequest;
import org.springframework.data.domain.Page;

public interface NewBookRequestService {
    //xem ds yêu cầu
    Page<NewBookRequest> searchMyRequests(ENewBookRequestStatus status, int page, int size);

    //tạo mới yêu caauf
    void createRequest(NewBookRequest request);
}