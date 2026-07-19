package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ERole;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountFormDto {
    private Integer userId;

    @NotBlank(message = "Mã người dùng không được để trống")
    @Size(max = 50, message = "Mã người dùng không được vượt quá 50 ký tự")
    private String code;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 255, message = "Họ và tên không được vượt quá 255 ký tự")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 255, message = "Email không được vượt quá 255 ký tự")
    private String email;

    @Pattern(regexp = "\\d{10}", message = "Số điện thoại phải gồm đúng 10 chữ số")
    private String phone;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;

    @NotNull(message = "Vai trò không được để trống")
    private ERole role;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean status;

    private MultipartFile avatarFile;

    private String avatarUrl;
}
