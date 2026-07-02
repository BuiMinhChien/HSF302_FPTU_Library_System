package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder // tạo đối tượng
@NoArgsConstructor
@AllArgsConstructor
public class BookListDto {
private Integer bookId;
private String title;
private String publisher; //nsb
private Integer publishYear; //năm sb
private String bookCoverUrl;
private  Long availableCopies; //số bản rỗi
}

