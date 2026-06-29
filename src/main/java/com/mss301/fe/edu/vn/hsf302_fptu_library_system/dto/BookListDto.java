package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookListDto {
private Integer bookId;
private String title;
private String publisher;
private Integer publishYear;
private String bookCoverUrl;
private  Long availableCopies;
}

