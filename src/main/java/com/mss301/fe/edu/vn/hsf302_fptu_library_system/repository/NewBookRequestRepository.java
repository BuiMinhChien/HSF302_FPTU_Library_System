package com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.ENewBookRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.NewBookRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NewBookRequestRepository extends JpaRepository<NewBookRequest, Integer> {

    // phn tranh lọc theo yêu cầu của người dùng
    @Query("""
        SELECT r FROM NewBookRequest r 
        WHERE r.user.userId = :userId 
        AND (:status IS NULL OR r.status = :status)
    """)
    Page<NewBookRequest> searchByUser(
            @Param("userId") Integer userId,
            @Param("status") ENewBookRequestStatus status,
            Pageable pageable
    );
    // thủ thư sẽ lấy tất cả yêu cầu của hệ thống , có lọc traạng thái
    @Query("""
        SELECT r FROM NewBookRequest r 
        WHERE (:status IS NULL OR r.status = :status)
    """)
    Page<NewBookRequest> searchAllRequests(
            @Param("status") ENewBookRequestStatus status,
            Pageable pageable
    );

}