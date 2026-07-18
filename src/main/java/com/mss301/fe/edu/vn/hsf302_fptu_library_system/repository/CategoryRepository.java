package com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    // Kiểm tra trùng tên khi thêm mới
    boolean existsByCategoryName(String categoryName);

    // Tìm kiếm theo tên + phân trang
    @Query("SELECT c FROM Category c WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Category> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}