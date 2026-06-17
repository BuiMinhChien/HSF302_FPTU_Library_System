package com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBookCopyStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book_copies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCopy extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer copyId;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(unique = true, nullable = false)
    private String barcode;

    @Enumerated(EnumType.STRING)
    private EBookCopyStatus status;
}
