package com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ERole;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    // Dành cho Admin: Lấy danh sách tài khoản
    @Query("""
        SELECT u FROM User u 
        WHERE (:keyword IS NULL OR :keyword = '' 
               OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) 
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) 
               OR LOWER(u.code) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:role IS NULL OR u.role = :role)
    """)
    Page<User> searchAccounts(
            @Param("keyword") String keyword,
            @Param("role") ERole role,
            Pageable pageable
    );
}
