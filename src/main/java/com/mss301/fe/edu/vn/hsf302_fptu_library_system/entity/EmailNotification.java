package com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EEmailStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailNotification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer emailId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String recipientEmail;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String subject;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    private LocalDateTime sentDate;

    @Enumerated(EnumType.STRING)
    private EEmailStatus status;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String errorMessage;
}
