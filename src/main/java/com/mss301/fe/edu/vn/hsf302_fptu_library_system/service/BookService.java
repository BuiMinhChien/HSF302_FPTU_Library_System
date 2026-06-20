package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BookDetailDto;

public interface BookService {
    BookDetailDto getBookDetail(Integer bookId);
}
