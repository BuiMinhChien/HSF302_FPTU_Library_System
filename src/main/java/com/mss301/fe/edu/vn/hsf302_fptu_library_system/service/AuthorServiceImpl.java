package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EFilePurpose;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.AuthorFormDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.UploadResult;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.AppFile;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Author;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.AuthorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final S3Service s3Service;

    @Override
    public Page<AuthorFormDto> getAllAuthors(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Author> authorPage;
        if (keyword == null || keyword.isBlank()) {
            authorPage = authorRepository.findAll(pageable);
        } else {
            authorPage = authorRepository.findByNameContaining(keyword, pageable);
        }

        return authorPage.map(author -> {
            int bookCount = author.getBooks() != null ? author.getBooks().size() : 0;
            return AuthorFormDto.builder()
                    .authorId(author.getAuthorId())
                    .authorName(author.getAuthorName())
                    .biography(author.getBiography())
                    .avatarUrl(author.getAvatar() != null ? author.getAvatar().getFileUrl() : null)
                    .bookCount(bookCount)
                    .build();
        });
    }

    @Override
    public List<AuthorFormDto> getAllAuthorsForCreate() {
        return authorRepository.findAll()
                .stream()
                .map(author -> {
                    int bookCount = author.getBooks() != null ? author.getBooks().size() : 0;
                    return AuthorFormDto.builder()
                            .authorId(author.getAuthorId())
                            .authorName(author.getAuthorName())
                            .biography(author.getBiography())
                            .avatarUrl(author.getAvatar() != null ? author.getAvatar().getFileUrl() : null)
                            .bookCount(bookCount)
                            .build();
                })
                .toList();
    }

    @Override
    public AuthorFormDto getById(Integer id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tác giả"));
        int bookCount = author.getBooks() != null ? author.getBooks().size() : 0;
        return AuthorFormDto.builder()
                .authorId(author.getAuthorId())
                .authorName(author.getAuthorName())
                .biography(author.getBiography())
                .avatarUrl(author.getAvatar() != null ? author.getAvatar().getFileUrl() : null)
                .bookCount(bookCount)
                .build();
    }

    @Override
    public void save(AuthorFormDto form) {
        Author author;
        if (form.getAuthorId() == null) {
            author = new Author();
        } else {
            author = authorRepository.findById(form.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tác giả"));
        }
        author.setAuthorName(form.getAuthorName().trim());
        author.setBiography(form.getBiography());

        MultipartFile avatar = form.getMultipartFile();
        if (avatar != null && !avatar.isEmpty()) {
            try {
                // Upload ảnh mới
                UploadResult upload = s3Service.uploadFile(avatar);
                // Upload thành công mới xóa ảnh cũ
                if (author.getAvatar() != null && author.getAvatar().getS3Key() != null) {
                    s3Service.deleteFileWithKey(author.getAvatar().getS3Key());
                }
                AppFile appFile = new AppFile();
                appFile.setFileName(avatar.getOriginalFilename());
                appFile.setFileUrl(upload.url());
                appFile.setS3Key(upload.key());
                String original = avatar.getOriginalFilename();
                if (original != null && original.contains(".")) {
                    appFile.setExtension(original.substring(original.lastIndexOf('.') + 1));
                }
                appFile.setPurpose(EFilePurpose.AUTHOR_AVATAR);
                author.setAvatar(appFile);
            } catch (IOException e) {
                throw new RuntimeException("Không thể tải ảnh lên S3", e);
            } catch (Exception e) {
                throw new RuntimeException("Có lỗi xảy ra khi xử lý ảnh", e);
            }
        }
        authorRepository.save(author);
    }

    @Override
    public void delete(Integer id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tác giả"));
        if (author.getBooks() != null && !author.getBooks().isEmpty()) {
            throw new IllegalStateException("Không thể xóa tác giả đã có sách trong hệ thống");
        }
        authorRepository.delete(author);
    }
}