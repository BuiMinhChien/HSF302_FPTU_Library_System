package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import lombok.Data;

@Data
public class FinePaymentHistorySearchRequest {
    private String bookTitle;
    private int page = 0;
    private int size = 5;
}
