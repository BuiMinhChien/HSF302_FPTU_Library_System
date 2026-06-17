package com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer authorId;

    @Column(name = "author_name", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String authorName;

    @Column(name = "biography", columnDefinition = "NVARCHAR(MAX)")
    private String biography;

    @ManyToMany(mappedBy = "authors")
    private Set<Book> books;
}
