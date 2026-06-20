package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorInfoDto {
    private Integer authorId;
    private String authorName;
    private String biography;
    private String avatarUrl;
}
