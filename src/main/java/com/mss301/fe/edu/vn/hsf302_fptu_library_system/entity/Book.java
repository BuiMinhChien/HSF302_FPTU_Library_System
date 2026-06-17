package com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookId;

    @Column(unique = true, nullable = false, length = 20)
    private String isbn;

    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String title;

    @Column(columnDefinition = "NVARCHAR(200)")
    private String publisher;

    private Integer publishYear;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors;

    @OneToMany(mappedBy = "book")
    private List<BookCopy> bookCopies;

    @OneToMany(mappedBy = "book")
    private List<BorrowRequest> borrowRequests;
}
