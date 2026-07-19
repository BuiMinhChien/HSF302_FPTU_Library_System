package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BookDetailDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BookFormDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BookListDto;
import org.springframework.data.domain.Page;

public interface BookService {
    BookDetailDto getBookDetail(Integer bookId);
    Page<BookListDto> searchBooks(String keyword, String searchType,int page, int size);
    Page<BookFormDto> getAllBooksForAdmin(String keyword, int page, int size);
    BookFormDto getBookFormById(Integer id);
    void saveBook(BookFormDto form);
    void deleteBook(Integer id);
}
