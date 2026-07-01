package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.AuthorFormDto;
import org.springframework.data.domain.Page;

public interface AuthorService {
    Page<AuthorFormDto> getAllAuthors(String keyword, int page, int size);
    AuthorFormDto getById(Integer id);
    void save(AuthorFormDto form);
    void update(Integer id, AuthorFormDto form);
    void delete(Integer id);
}