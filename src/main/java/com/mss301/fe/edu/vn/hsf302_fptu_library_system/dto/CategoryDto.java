package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Integer categoryId;
    private String categoryName;
    private String description;
    private int bookCount;
}