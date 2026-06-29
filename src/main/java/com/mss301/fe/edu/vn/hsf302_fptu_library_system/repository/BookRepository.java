package com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    // Tìm theo tiêu đề (title) - LIKE không phân biệt hoa thường
    @Query("SELECT b FROM Book b WHERE (:keyword IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Book> findByTitleContaining(@Param("keyword") String keyword, Pageable pageable);
     //Tìm tác giả
    @Query("SELECT DISTINCT b FROM Book b JOIN b.authors a WHERE (:keyword IS NULL OR LOWER(a.authorName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Book> findByAuthorContaining(@Param("keyword") String keyword , Pageable pageable);

    // Tìm theo nhà xuất bản
    @Query("SELECT b FROM Book b WHERE (:keyword IS NULL OR LOWER(b.publisher) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Book> findByPublisherContaining(@Param("keyword") String keyword, Pageable pageable);
    // Tìm theo ISBN
    @Query("SELECT b FROM Book b WHERE (:keyword IS NULL OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Book> findByIsbnContaining(@Param("keyword") String keyword, Pageable pageable);


}