package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.CategoryDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Category;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {
    Page<CategoryDto> getAllCategories(String keyword, int page, int size);

    List<CategoryDto> getAllCategories();

    CategoryDto getById(Integer id);
    void save(Category category);
    void update(Integer id, Category form);
    void delete(Integer id);
}