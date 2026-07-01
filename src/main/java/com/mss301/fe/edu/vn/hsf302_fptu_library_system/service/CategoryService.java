package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.CategoryDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Category;
import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategories();   // Lấy tất cả để hiển thị danh sách
    CategoryDto getById(Integer id);        // Lấy 1 category để hiển thị form sửa
    void save(Category category);           // Thêm mới
    void update(Integer id, Category form); // Cập nhật
    void delete(Integer id);               // Xóa
}