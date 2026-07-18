package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ERole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountFormDto {
    private Integer userId;
    private String code;
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String address;
    private ERole role;
    private Boolean status;
}
