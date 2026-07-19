package com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ENewBookRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "new_book_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewBookRequest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer newBookRequestId;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String bookTitle;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String authorName;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String publisher;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String purchaseLink;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String reason;

    private LocalDateTime requestDate;

    @Enumerated(EnumType.STRING)
    private ENewBookRequestStatus status;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String rejectionReason;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    private LocalDateTime approvedDate;
}