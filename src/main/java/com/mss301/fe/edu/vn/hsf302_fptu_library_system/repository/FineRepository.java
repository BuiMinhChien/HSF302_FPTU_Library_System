package com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FineRepository extends JpaRepository<Fine, Integer> {

    @Query("""
            SELECT f FROM Fine f
            LEFT JOIN FETCH f.borrowHistory bh
            LEFT JOIN FETCH bh.copy c
            LEFT JOIN FETCH c.book
            WHERE f.user.userId = :userId AND f.deleteFlag = false
            ORDER BY f.createdAt DESC
            """)
    List<Fine> findByUserWithDetails(@Param("userId") Integer userId);

    @Query("""
            SELECT f FROM Fine f
            JOIN FETCH f.user
            WHERE f.fineId = :fineId AND f.deleteFlag = false
            """)
    Optional<Fine> findByFineIdWithUser(@Param("fineId") Integer fineId);

    boolean existsByBorrowHistory_BorrowIdAndDeleteFlagFalse(Integer borrowId);

    // Lấy danh sách phiếu phạt tạo mới từ sau thời điểm chỉ định
    List<Fine> findByCreatedAtAfter(java.time.LocalDateTime startDate);
}
