package com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fine_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinePayment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentId;

    @ManyToOne
    @JoinColumn(name = "fine_id")
    private Fine fine;

    private BigDecimal amount;

    private LocalDateTime paymentDate;

    private String paymentMethod;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String note;
}
