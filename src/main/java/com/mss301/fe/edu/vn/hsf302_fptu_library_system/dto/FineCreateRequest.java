package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FineCreateRequest {

    @NotNull(message = "Vui lòng chọn phiếu mượn")
    private Integer borrowRecordId;

    @NotBlank(message = "Lý do phạt không được để trống")
    @Size(max = 2000, message = "Lý do phạt tối đa 2000 ký tự")
    private String reason;

    @NotNull(message = "Số tiền phạt không được để trống")
    @DecimalMin(value = "1", message = "Số tiền phạt phải lớn hơn 0")
    private BigDecimal amount;
}
