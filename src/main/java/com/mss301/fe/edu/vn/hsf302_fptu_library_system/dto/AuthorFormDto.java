package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorFormDto {
    private Integer authorId;
    @NotBlank(message = "Tên tác giả không được để trống")
    @Size(max = 100, message = "Tên tác giả không được vượt quá 100 ký tự")
    private String authorName;
    @Size(max = 5000, message = "Tiểu sử không được vượt quá 5000 ký tự")
    private String biography;
    private MultipartFile multipartFile;
    private String avatarUrl;   // Để hiển thị ảnh hiện tại
    private int bookCount;      // Số sách của tác giả
}