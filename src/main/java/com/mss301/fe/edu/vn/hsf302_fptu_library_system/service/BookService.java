package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BookDetailDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BookListDto;
import org.springframework.data.domain.Page;

public interface BookService {
    BookDetailDto getBookDetail(Integer bookId);

    // Thêm method mới: tìm kiếm và phân trang danh sách
    // keyword: từ khoá tìm kiếm
    // searchType: loại tìm kiếm (title, author, publisher, isbn, all)
    // page: trang hiện tại (bắt đầu từ 0)
    // size: số sách mỗi trang
    Page<BookListDto> searchBooks(String keyword, String searchType,int page, int size);
}
