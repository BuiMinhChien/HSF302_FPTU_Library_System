package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBookCopyStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.AuthorInfoDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BookDetailDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BookFormDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.BookListDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Book;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Category;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // ===== METHOD MỚI =====
    @Override
    public Page<BookListDto> searchBooks(String keyword, String searchType, int page, int size) {
        // Tạo đối tượng phân trang,lấy size sách, trang thứ page
        Pageable pageable = PageRequest.of(page, size);
        // Chọn query theo loại tìm kiếm
        Page<Book> bookPage;
        if (keyword == null || keyword.isBlank()) {
            // Không có keyword -> lấy tất cả
            bookPage = bookRepository.findAll(pageable);
        } else {
            switch (searchType != null ? searchType : "title") {
                case "author":
                    bookPage = bookRepository.findByAuthorContaining(keyword, pageable);
                    break;
                case "publisher":
                    bookPage = bookRepository.findByPublisherContaining(keyword, pageable);
                    break;
                case "isbn":
                    bookPage = bookRepository.findByIsbnContaining(keyword, pageable);
                    break;
                default: // "title" hoặc mặc định
                    bookPage = bookRepository.findByTitleContaining(keyword, pageable);
                    break;
            }
        }
        // Chuyển từng Book entity sang BookListDto (chỉ lấy những trường cần thiết)
        return bookPage.map(book -> {
            long availableCopies = book.getBookCopies()
                    .stream()
                    .filter(copy -> copy.getStatus() == EBookCopyStatus.AVAILABLE)
                    .count();
            return BookListDto.builder()
                    .bookId(book.getBookId())
                    .title(book.getTitle())
                    .publisher(book.getPublisher())
                    .publishYear(book.getPublishYear())
                    .bookCoverUrl(book.getBookCover() != null ? book.getBookCover().getFileUrl() : null)
                    .availableCopies(availableCopies)
                    .build();
        });
    }

    // MANAGE BOOK
    @Override
    public Page<BookFormDto> getAllBooksForAdmin(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage;
        if (keyword == null || keyword.isBlank()) {
            bookPage = bookRepository.findAll(pageable);
        } else {
            bookPage = bookRepository.findByTitleContaining(keyword, pageable);
        }

        return bookPage.map(book -> {
            long availableCopies = book.getBookCopies() != null ? book.getBookCopies().stream()
                    .filter(copy -> copy.getStatus() == EBookCopyStatus.AVAILABLE).count() : 0;
            return BookFormDto.builder()
                    .bookId(book.getBookId())
                    .isbn(book.getIsbn())
                    .title(book.getTitle())
                    .publisher(book.getPublisher())
                    .publishYear(book.getPublishYear())
                    .totalCopies(book.getBookCopies() != null ? book.getBookCopies().size() : 0)
                    .availableCopies((int) availableCopies)
                    .bookCoverUrl(book.getBookCover() != null ? book.getBookCover().getFileUrl() : null)
                    .build();
        });
    }

    @Override
    public BookFormDto getBookFormById(Integer id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách"));
        return BookFormDto.builder()
                .bookId(book.getBookId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .publisher(book.getPublisher())
                .publishYear(book.getPublishYear())
                .description(book.getDescription())
                .bookCoverUrl(book.getBookCover() != null ? book.getBookCover().getFileUrl() : null)
                .categoryIds(book.getCategories().stream().map(c -> c.getCategoryId()).collect(java.util.stream.Collectors.toList()))
                .authorIds(book.getAuthors().stream().map(a -> a.getAuthorId()).collect(java.util.stream.Collectors.toList()))
                .build();
    }

    @Override
    public void createBook(BookFormDto form) {
        Book book = new Book();
        book.setIsbn(form.getIsbn());
        book.setTitle(form.getTitle());
        book.setPublisher(form.getPublisher());
        book.setPublishYear(form.getPublishYear());
        book.setDescription(form.getDescription());

        // Lấy list categories và authors từ repository
        // (Trong code thực tế cần inject CategoryRepository và AuthorRepository vào BookServiceImpl)
        // code lược bỏ cho gọn...

        bookRepository.save(book);
    }

    @Override
    public void updateBook(Integer id, BookFormDto form) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách"));
        book.setIsbn(form.getIsbn());
        book.setTitle(form.getTitle());
        book.setPublisher(form.getPublisher());
        book.setPublishYear(form.getPublishYear());
        book.setDescription(form.getDescription());
        bookRepository.save(book);
    }

    @Override
    public void deleteBook(Integer id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách"));
        bookRepository.delete(book);
    }
}
