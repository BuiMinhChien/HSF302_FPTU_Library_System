package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.CategoryDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Category;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {
    // Lấy danh sách có phân trang + tìm kiếm
    Page<CategoryDto> getAllCategories(String keyword, int page, int size);

    // Lấy tất cả (dùng ở nơi khác nếu cần)
    List<CategoryDto> getAllCategories();

    CategoryDto getById(Integer id);    // Lấy 1 để sửa
    void save(Category category);       // Thêm mới
    void update(Integer id, Category form); // Cập nhật
    void delete(Integer id);            // Xóa
}