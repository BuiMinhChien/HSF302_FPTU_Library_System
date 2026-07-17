package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthorFormDto {
    private Integer authorId;
    private String authorName;
    private String biography;
    private String avatarUrl;   // Để hiển thị ảnh hiện tại
    private int bookCount;      // Số sách của tác giả
}