package com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRequest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requestId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Enumerated(EnumType.STRING)
    private EBorrowRequestStatus status;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String rejectionReason;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    private LocalDateTime approvedDate;
}
