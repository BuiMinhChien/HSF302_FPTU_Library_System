package com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    // Tìm theo tiêu đề (title) - LIKE '%Java%' tìm tất cả các keyy
    @Query("SELECT b FROM Book b WHERE b.deleteFlag = false AND (:keyword IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Book> findByTitleContaining(@Param("keyword") String keyword, Pageable pageable);
     //Tìm tác giả
    @Query("SELECT DISTINCT b FROM Book b JOIN b.authors a WHERE b.deleteFlag = false AND (:keyword IS NULL OR :keyword = '' OR LOWER(a.authorName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Book> findByAuthorContaining(@Param("keyword") String keyword , Pageable pageable);

    // Tìm theo nhà xuất bản
    @Query("SELECT b FROM Book b WHERE b.deleteFlag = false AND (:keyword IS NULL OR LOWER(b.publisher) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Book> findByPublisherContaining(@Param("keyword") String keyword, Pageable pageable);
    // Tìm theo ISBN
    @Query("SELECT b FROM Book b WHERE b.deleteFlag = false AND (:keyword IS NULL OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Book> findByIsbnContaining(@Param("keyword") String keyword, Pageable pageable);

    // Tìm kiếm sách đa trường (Title, Author, Publisher, ISBN) cho Admin
    @Query("""
    SELECT DISTINCT b
    FROM Book b
    LEFT JOIN b.authors a
    WHERE b.deleteFlag = false
      AND (
            :keyword IS NULL
            OR :keyword = ''
            OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(a.authorName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(b.publisher) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
    """)
    Page<Book> searchAdminBooks(@Param("keyword") String keyword, Pageable pageable);

    Optional<Book> findByIsbn(String isbn);
}