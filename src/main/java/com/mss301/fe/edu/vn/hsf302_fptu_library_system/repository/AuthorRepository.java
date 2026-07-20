package com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
    // Tìm kiếm tác giả theo tên (phân trang)
    @Query("SELECT a FROM Author a WHERE (:keyword IS NULL OR LOWER(a.authorName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Author> findByNameContaining(@Param("keyword") String keyword, Pageable pageable);
    boolean existsByAuthorNameAndAuthorIdNot(String authorName, Integer authorId);
}