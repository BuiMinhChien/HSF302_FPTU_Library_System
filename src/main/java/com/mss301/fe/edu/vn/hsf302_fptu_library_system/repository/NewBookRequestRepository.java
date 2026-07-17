package com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ENewBookRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.NewBookRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NewBookRequestRepository extends JpaRepository<NewBookRequest, Integer> {

    // Phân trang và lọc yêu cầu của độc giả theo từ khóa, trạng thái và ngày tạo
    @Query("""
        SELECT r FROM NewBookRequest r 
        WHERE r.user.userId = :userId 
        AND (:status IS NULL OR r.status = :status)
        AND (
            :keyword IS NULL OR :keyword = ''
            OR LOWER(r.bookTitle) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(r.authorName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        AND (:fromDate IS NULL OR r.requestDate >= :fromDate)
        AND (:toDate IS NULL OR r.requestDate <= :toDate)
    """)
    Page<NewBookRequest> searchByUser(
            @Param("userId") Integer userId,
            @Param("keyword") String keyword,
            @Param("status") ENewBookRequestStatus status,
            @Param("fromDate") java.time.LocalDateTime fromDate,
            @Param("toDate") java.time.LocalDateTime toDate,
            Pageable pageable
    );
    // Thủ thư/Admin lấy tất cả yêu cầu, hỗ trợ lọc theo từ khóa, trạng thái và ngày gửi
    @Query("""
        SELECT r FROM NewBookRequest r 
        WHERE (:status IS NULL OR r.status = :status)
        AND (
            :keyword IS NULL OR :keyword = ''
            OR LOWER(r.bookTitle) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(r.authorName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(r.user.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(r.user.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        AND (:fromDate IS NULL OR r.requestDate >= :fromDate)
        AND (:toDate IS NULL OR r.requestDate <= :toDate)
    """)
    Page<NewBookRequest> searchAllRequests(
            @Param("keyword") String keyword,
            @Param("status") ENewBookRequestStatus status,
            @Param("fromDate") java.time.LocalDateTime fromDate,
            @Param("toDate") java.time.LocalDateTime toDate,
            Pageable pageable
    );

}