package com.mss301.fe.edu.vn.hsf302_fptu_library_system.mapper;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBookCopyStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.AuthorInfoDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BookDetailDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Author;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {
    public BookDetailDto toDTO(Book book) {
        long totalCopies = book.getBookCopies() == null ? 0 : book.getBookCopies().size();
        long availableCopies = book.getBookCopies() == null ? 0
                : book.getBookCopies()
                .stream()
                .filter(copy ->
                        copy.getStatus() == EBookCopyStatus.AVAILABLE)
                .count();
        long borrowedCopies = book.getBookCopies() == null ? 0
                : book.getBookCopies()
                .stream()
                .filter(copy -> copy.getStatus() == EBookCopyStatus.BORROWED)
                .count();
        return BookDetailDto.builder()
                .bookId(book.getBookId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .publisher(book.getPublisher())
                .publishYear(book.getPublishYear())
                .description(book.getDescription())
                .categories(
                        book.getCategories()
                                .stream()
                                .map(category -> category.getCategoryName())
                                .toList()
                )
                .authors(
                        book.getAuthors()
                                .stream()
                                .map(this::toAuthorInfoDto)
                                .toList()
                )
                .bookCoverUrl(book.getBookCover() != null ? book.getBookCover().getFileUrl() : null)
                .totalCopies(totalCopies)
                .availableCopies(availableCopies)
                .borrowedCopies(borrowedCopies)
                .canBorrow(availableCopies > 0)
                .build();
    }

    private AuthorInfoDto toAuthorInfoDto(Author author) {
        return AuthorInfoDto.builder()
                .authorId(author.getAuthorId())
                .authorName(author.getAuthorName())
                .avatarUrl(author.getAvatar() != null ? author.getAvatar().getFileUrl() : null)
                .biography(author.getBiography())
                .build();
    }
}
