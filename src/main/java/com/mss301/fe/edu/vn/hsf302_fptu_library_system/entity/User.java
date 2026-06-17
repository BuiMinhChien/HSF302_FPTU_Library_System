package com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ERole;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(name = "full_name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String fullName;

    @Column(unique = true)
    private String email;

    private String password;

    private String phone;

    @Column(name = "address", columnDefinition = "NVARCHAR(255)")
    private String address;

    @Enumerated(EnumType.STRING)
    private ERole role;

    private Boolean status;

    @OneToMany(mappedBy = "user")
    private List<BorrowRequest> borrowRequests;

    @OneToMany(mappedBy = "user")
    private List<BorrowHistory> borrowHistories;

    @OneToMany(mappedBy = "user")
    private List<Fine> fines;
}
