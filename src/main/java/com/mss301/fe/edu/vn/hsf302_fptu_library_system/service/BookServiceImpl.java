package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBookCopyStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EFilePurpose;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.*;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.*;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.AuthorRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BookCopyRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.BookRepository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final BookCopyRepository bookCopyRepository;
    private final S3Service s3Service;

    @Override
    public BookDetailDto getBookDetail(Integer bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found"));
        List<BookCopy> bookCopies = bookCopyRepository.findByBook_BookIdAndDeleteFlagFalse(bookId);
        long totalCopies = bookCopies.size();
        long availableCopies = bookCopies
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

    @Override
    public Page<BookListDto> searchBooks(String keyword, String searchType, int page, int size) {
        // Tạo đối tượng phân trang,lấy size sách, trang thứ page trang 0, lấy 10 item
        Pageable pageable = PageRequest.of(page, size);
        // Chọn query theo loại tìm kiếm
        Page<Book> bookPage;
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
        // Chuyển từng Book entity sang BookListDto (chỉ lấy những trường cần thiết)
        return bookPage.map(book -> {
            List<BookCopy> bookCopies = bookCopyRepository.findByBook_BookIdAndDeleteFlagFalse(book.getBookId());
            long availableCopies = bookCopies
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

    @Override
    public Page<BookFormDto> getAllBooksForAdmin(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookId").descending());
        Page<Book> bookPage = bookRepository.searchAdminBooks(keyword, pageable);
        return bookPage.map(book -> {
            // Đếm số bản sách còn trống
            List<BookCopy> bookCopies = bookCopyRepository.findByBook_BookIdAndDeleteFlagFalse(book.getBookId());
            long availableCopies = bookCopies != null ? bookCopies.stream()
                    .filter(copy -> copy.getStatus() == EBookCopyStatus.AVAILABLE).count() : 0;
            return BookFormDto.builder()
                    .bookId(book.getBookId())
                    .isbn(book.getIsbn())
                    .title(book.getTitle())
                    .publisher(book.getPublisher())
                    .publishYear(book.getPublishYear())
                    .authorNames(book.getAuthors().stream().map(author -> author.getAuthorName()).toList())
                    .totalCopies(bookCopies != null ? bookCopies.size() : 0)
                    .availableCopies((int) availableCopies)
                    .bookCoverUrl(book.getBookCover() != null ? book.getBookCover().getFileUrl() : null)
                    .build();
        });
    }

    @Override
    public BookFormDto getBookFormById(Integer id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách"));
        List<BookCopy> bookCopies = bookCopyRepository.findByBook_BookIdAndDeleteFlagFalse(book.getBookId());
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
                .bookCopies(
                        bookCopies.stream()
                                .map(copy -> BookCopyFormDto.builder()
                                        .copyId(copy.getCopyId())
                                        .barcode(copy.getBarcode())
                                        .status(copy.getStatus())
                                        .build())
                                .toList()
                )
                .build();
    }

    @Override
    public void saveBook(BookFormDto form) {
        Book book;
        if (form.getBookId() == null) {
            book = new Book();
        } else {
            book = bookRepository.findById(form.getBookId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sách"));
        }
        Optional<Book> existedBook = bookRepository.findByIsbn(form.getIsbn());
        if (existedBook.isPresent() && !existedBook.get().getBookId().equals(form.getBookId())) {
            throw new IllegalArgumentException("ISBN đã tồn tại");
        }
        // Thông tin sách
        book.setIsbn(form.getIsbn());
        book.setTitle(form.getTitle());
        book.setPublisher(form.getPublisher());
        book.setPublishYear(form.getPublishYear());
        book.setDescription(form.getDescription());
        book.setCategories(categoryRepository.findAllById(form.getCategoryIds()));
        book.setAuthors(authorRepository.findAllById(form.getAuthorIds()));
        MultipartFile bookCoverFile = form.getBookCoverFile();
        if (bookCoverFile != null && !bookCoverFile.isEmpty()) {
            try {
                // Upload trước
                UploadResult upload = s3Service.uploadFile(bookCoverFile);
                // Upload thành công mới xóa ảnh cũ
                if (book.getBookCover() != null && book.getBookCover().getS3Key() != null) {
                    s3Service.deleteFileWithKey(book.getBookCover().getS3Key());
                }
                AppFile appFile = new AppFile();
                appFile.setFileName(bookCoverFile.getOriginalFilename());
                appFile.setFileUrl(upload.url());
                appFile.setS3Key(upload.key());
                String original = bookCoverFile.getOriginalFilename();
                if (original != null && original.contains(".")) {
                    appFile.setExtension(original.substring(original.lastIndexOf('.') + 1));
                }
                appFile.setPurpose(EFilePurpose.BOOK_COVER);
                book.setBookCover(appFile);
            } catch (IOException e) {
                throw new RuntimeException("Không thể tải ảnh lên S3", e);
            } catch (Exception e) {
                throw new RuntimeException("Có lỗi xảy ra khi xử lý ảnh", e);
            }
        }
        bookRepository.save(book);

//        lấy ra các bản coppy hiện có
        List<BookCopy> currentCopies = bookCopyRepository.findByBook_BookIdAndDeleteFlagFalse(book.getBookId());
//        dùng map thay list để get Id nhanh hơn
        Map<Integer, BookCopy> currentMap = new HashMap<>();
        for (BookCopy copy : currentCopies) {
            currentMap.put(copy.getCopyId(), copy);
        }

        List<BookCopy> saveList = new ArrayList<>(); //danh sách chứa các bản copy cả cũ lẫn mới
        Set<Integer> remainIds = new HashSet<>(); //danh sách các id từ form truyền về đã có sẵn trong db -> giữ lại
        for (BookCopyFormDto dto : form.getBookCopies()) { //duyệt các book copy từ form
            BookCopy copy;
            // Update
            if (dto.getCopyId() != null) { //nếu có id chứng tỏ là bản copy cũ có sẵn từ db
                copy = currentMap.get(dto.getCopyId()); //lấy ra chính xác bản copy đó theo id
                if (copy == null) {
                    throw new RuntimeException("Book copy không tồn tại");
                }
                remainIds.add(copy.getCopyId()); //thêm vào danh sách các id có sẵn cần giữ lại
            } else {
                // Create
                copy = new BookCopy(); //là bản copy mới hẳn -> cần tạo mới
                copy.setBook(book);
            }
            // Kiểm tra barcode unique
            Optional<BookCopy> existed = bookCopyRepository.findByBarcodeAndDeleteFlagFalse(dto.getBarcode());
            if (existed.isPresent()
                    && !Objects.equals(existed.get().getCopyId(), dto.getCopyId())) {
                throw new IllegalArgumentException("Barcode '" + dto.getBarcode() + "' đã tồn tại");
            }
//            cập nhật thông tin bản copy
            copy.setBarcode(dto.getBarcode());
            copy.setStatus(dto.getStatus());
            saveList.add(copy);
        }
        // Lưu các bản sao
        bookCopyRepository.saveAll(saveList);
        // Xóa các BookCopy có trong db từ trước nhưng không còn trên form update -> xoá đi
        List<BookCopy> deleteList = currentCopies.stream().filter(c -> !remainIds.contains(c.getCopyId())).toList();
        if (!deleteList.isEmpty()) {
            deleteList.forEach(copy -> copy.setDeleteFlag(true));
            bookCopyRepository.saveAll(deleteList);
        }
    }

    @Override
    public void deleteBook(Integer id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách"));
        book.setDeleteFlag(true);
        List<BookCopy> copies = bookCopyRepository.findByBook_BookIdAndDeleteFlagFalse(id);
        for (BookCopy copy : copies) {
            copy.setDeleteFlag(true);
        }
        bookCopyRepository.saveAll(copies);
        bookRepository.save(book);
    }
}
