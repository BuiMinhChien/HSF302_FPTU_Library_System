package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.AuthorFormDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AuthorService {
    Page<AuthorFormDto> getAllAuthors(String keyword, int page, int size);
    List<AuthorFormDto> getAllAuthorsForCreate();
    AuthorFormDto getById(Integer id);
    void save(AuthorFormDto form);
    void update(Integer id, AuthorFormDto form);
    void delete(Integer id);
}