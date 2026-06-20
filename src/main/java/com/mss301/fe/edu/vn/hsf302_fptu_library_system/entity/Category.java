package com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    @Column(name = "category_name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String categoryName;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @ManyToMany(mappedBy = "categories")
    private List<Book> books;
}