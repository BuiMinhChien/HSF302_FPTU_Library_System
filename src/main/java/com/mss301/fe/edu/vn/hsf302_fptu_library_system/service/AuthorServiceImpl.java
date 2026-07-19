package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EFilePurpose;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.AuthorFormDto;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.AppFile;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Author;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
//
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
        Author author = new Author();
        author.setAuthorName(form.getAuthorName());
        author.setBiography(form.getBiography());

        if (form.getAvatarUrl() != null && !form.getAvatarUrl().isBlank()) {
            AppFile avatar = new AppFile();
            avatar.setFileName("author-avatar.jpg");
            avatar.setFileUrl(form.getAvatarUrl());
            avatar.setExtension("jpg");
            avatar.setPurpose(EFilePurpose.AUTHOR_AVATAR);
            author.setAvatar(avatar);
        }
        authorRepository.save(author);
    }

    @Override
    public void update(Integer id, AuthorFormDto form) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tác giả"));

        author.setAuthorName(form.getAuthorName());
        author.setBiography(form.getBiography());

        if (form.getAvatarUrl() != null && !form.getAvatarUrl().isBlank()) {
            if (author.getAvatar() == null) {
                AppFile avatar = new AppFile();
                avatar.setFileName("author-avatar.jpg");
                avatar.setExtension("jpg");
                avatar.setPurpose(EFilePurpose.AUTHOR_AVATAR);
                author.setAvatar(avatar);
            }
            author.getAvatar().setFileUrl(form.getAvatarUrl());
        } else {
            author.setAvatar(null);
        }
        authorRepository.save(author);
    }

    @Override
    public void delete(Integer id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tác giả"));

        if (author.getBooks() != null && !author.getBooks().isEmpty()) {
            throw new IllegalStateException("Không thể xóa tác giả đã có sách trong hệ thống.");
        }
        authorRepository.delete(author);
    }
}