package com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ERole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDto {
    private String fullName;
    private String code;
    private String email;
    private String phone;
    private String address;
    private ERole role;
}
