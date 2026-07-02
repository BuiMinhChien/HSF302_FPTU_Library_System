package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BorrowHistoryOptionDTO;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.FineCreateRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.FineViewDTO;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Fine;

import java.util.List;

public interface FineService {

    List<FineViewDTO> getFinesForCurrentReader();

    void createFine(FineCreateRequest request);

    List<BorrowHistoryOptionDTO> getBorrowOptionsForCreateFine();

    Fine getFineForPayment(Integer fineId);

    void markFineAsPaid(Integer fineId);
}
