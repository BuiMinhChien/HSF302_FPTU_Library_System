package com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EFineStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "fines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fine extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fineId;

    @OneToOne
    @JoinColumn(name = "borrow_id")
    private BorrowHistory borrowHistory;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private BigDecimal amount;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String reason;

    @Enumerated(EnumType.STRING)
    private EFineStatus status;

    @OneToMany(mappedBy = "fine")
    private List<FinePayment> payments;
}
