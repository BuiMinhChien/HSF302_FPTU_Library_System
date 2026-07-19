package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBookCopyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCopyFormDto {
    private Integer copyId;

    @NotBlank(message = "Barcode không được để trống")
    @Size(max = 100)
    private String barcode;

    @NotNull(message = "Trạng thái không được để trống")
    private EBookCopyStatus status;
}
