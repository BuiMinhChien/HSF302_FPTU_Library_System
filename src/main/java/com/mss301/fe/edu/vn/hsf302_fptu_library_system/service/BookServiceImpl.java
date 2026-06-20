package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBookCopyStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.AuthorInfoDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BookDetailDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Book;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Category;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BookRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Override
    public BookDetailDto getBookDetail(Integer bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found"));
        long totalCopies = book.getBookCopies().size();
        long availableCopies = book.getBookCopies()
                .stream()
                .filter(copy -> copy.getStatus() == EBookCopyStatus.AVAILABLE)
                .count();
        long borrowedCopies = totalCopies - availableCopies;
        List<AuthorInfoDto> authors = book.getAuthors()
                .stream()
                .map(author -> AuthorInfoDto.builder()
                        .authorId(author.getAuthorId())
                        .authorName(author.getAuthorName())
                        .biography(author.getBiography())
                        .avatarUrl(
                                author.getAvatar() != null
                                        ? author.getAvatar().getFileUrl()
                                        : null
                        )
                        .build())
                .toList();
        book.getCategories().forEach(category ->
                System.out.println(category.getCategoryId()
                        + " - "
                        + category.getCategoryName()));
        List<String> categories = book.getCategories()
                .stream()
                .map(Category::getCategoryName)
                .toList();
        return BookDetailDto.builder()
                .bookId(book.getBookId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .publisher(book.getPublisher())
                .publishYear(book.getPublishYear())
                .description(book.getDescription())
                .categories(categories)
                .bookCoverUrl(
                        book.getBookCover() != null
                                ? book.getBookCover().getFileUrl()
                                : null
                )
                .totalCopies(totalCopies)
                .availableCopies(availableCopies)
                .borrowedCopies(borrowedCopies)
                .canBorrow(availableCopies > 0)
                .authors(authors)
                .build();
    }
}
