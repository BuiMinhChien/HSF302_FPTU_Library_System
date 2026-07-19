package com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBookCopyStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Book;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Integer> {
    Optional<BookCopy> findFirstByBookAndStatusAndDeleteFlagFalse(Book book, EBookCopyStatus status);

    // Đếm số bản sao sách theo trạng thái (AVAILABLE, BORROWED...)
    // Spring Data JPA tự tạo câu SQL: SELECT COUNT(*) FROM book_copies WHERE status = ?
    long countByStatusAndDeleteFlagFalse(EBookCopyStatus status);

    List<BookCopy> findByBook_BookIdAndDeleteFlagFalse(Integer bookId);
    Optional<BookCopy> findByBarcodeAndDeleteFlagFalse(String barcode);
}
