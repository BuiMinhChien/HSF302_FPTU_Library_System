package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.CategoryDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Category;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream().map(category -> {
            int bookCount = category.getBooks() != null ? category.getBooks().size() : 0;
            return CategoryDto.builder()
                    .categoryId(category.getCategoryId())
                    .categoryName(category.getCategoryName())
                    .description(category.getDescription())
                    .bookCount(bookCount)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục"));
        int bookCount = category.getBooks() != null ? category.getBooks().size() : 0;
        return CategoryDto.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .description(category.getDescription())
                .bookCount(bookCount)
                .build();
    }

    @Override
    public void save(Category category) {
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại");
        }
        categoryRepository.save(category);
    }

    @Override
    public void update(Integer id, Category form) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục"));

        // Cập nhật thông tin
        category.setCategoryName(form.getCategoryName());
        category.setDescription(form.getDescription());
        categoryRepository.save(category);
    }

    @Override
    public void delete(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục"));

        if (category.getBooks() != null && !category.getBooks().isEmpty()) {
            throw new IllegalStateException("Không thể xóa danh mục đang chứa sách. Vui lòng xóa sách trước.");
        }
        categoryRepository.delete(category);
    }
}