package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookFormDto {
    private Integer bookId;
    private String isbn;
    private String title;
    private String publisher;
    private Integer publishYear;
    private String description;
    private String bookCoverUrl;
    private List<Integer> categoryIds;   // Danh sách ID category đang chọn
    private List<Integer> authorIds;     // Danh sách ID author đang chọn
    private int totalCopies;             // Tổng số bản in
    private int availableCopies;         // Số bản còn sẵnn
}