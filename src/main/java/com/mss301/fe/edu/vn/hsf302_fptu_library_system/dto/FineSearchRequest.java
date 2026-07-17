package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EFineStatus;
import lombok.Data;

@Data
public class FineSearchRequest {
    private String bookTitle;
    private String fullName;
    private String studentCode;
    private EFineStatus status;
    private int page = 0;
    private int size = 5;
}
