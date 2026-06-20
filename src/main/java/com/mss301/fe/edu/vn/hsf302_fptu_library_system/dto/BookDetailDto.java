package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BookDetailDto {
    private Integer bookId;
    private String isbn;
    private String title;
    private String publisher;
    private Integer publishYear;
    private String description;
    private List<String> categories;
    private String bookCoverUrl;
    // thống kê số lượng
    private Long totalCopies;
    private Long availableCopies;
    private Long borrowedCopies;
    // phục vụ nút đăng ký mượn
    private Boolean canBorrow;
    private List<AuthorInfoDto> authors;
}
