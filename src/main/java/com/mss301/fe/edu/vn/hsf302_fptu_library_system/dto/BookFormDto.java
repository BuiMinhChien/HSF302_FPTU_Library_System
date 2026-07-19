package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookFormDto {
    private Integer bookId;

    @NotBlank(message = "ISBN không được để trống")
    @Size(max = 20, message = "ISBN không được vượt quá 20 ký tự")
    private String isbn;

    @NotBlank(message = "Tên sách không được để trống")
    @Size(max = 255, message = "Tên sách không được vượt quá 255 ký tự")
    private String title;

    @NotBlank(message = "Tên nhà xuất bản không được để trống")
    @Size(max = 200, message = "Tên nhà xuất bản không được vượt quá 200 ký tự")
    private String publisher;

    @NotNull(message = "Năm xuất bản không được để trống")
    @Min(value = 1000, message = "Năm xuất bản không hợp lệ")
    @Max(value = Year.MAX_VALUE, message = "Năm xuất bản không hợp lệ")
    private Integer publishYear;

    @Size(max = 5000, message = "Mô tả quá dài")
    private String description;

    private MultipartFile bookCoverFile;

    @NotEmpty(message = "Phải chọn ít nhất một thể loại")
    private List<Integer> categoryIds = new ArrayList<>();

    @NotEmpty(message = "Phải chọn ít nhất một tác giả")
    private List<Integer> authorIds = new ArrayList<>();

    @Valid
    private List<BookCopyFormDto> bookCopies = new ArrayList<>();

    // Chỉ dùng hiển thị
    private int totalCopies;
    private int availableCopies;
    private List<String> authorNames = new ArrayList<>();
    private String bookCoverUrl;
}