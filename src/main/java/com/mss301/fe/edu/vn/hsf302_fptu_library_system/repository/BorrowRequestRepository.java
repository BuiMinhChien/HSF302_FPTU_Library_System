package com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EBorrowRequestStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.Book;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowRequest;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, Integer> {
    boolean existsByUserAndBookAndStatus(
            User user,
            Book book,
            EBorrowRequestStatus status
    );

    List<BorrowRequest> findByStatusOrderByCreatedAtAsc(EBorrowRequestStatus status);

    List<BorrowRequest> findByStatusAndUpdatedAtBefore(
            EBorrowRequestStatus status,
            LocalDateTime expiredTime
    );

    boolean existsByUserAndBookAndStatusNotIn(
            User user,
            Book book,
            List<EBorrowRequestStatus> statuses
    );

    Page<BorrowRequest> findByUser_UserId(
            Integer userId,
            Pageable pageable
    );

    @Query("""
        SELECT br
        FROM BorrowRequest br
        WHERE br.user.userId = :userId
        AND (
            :keyword IS NULL
            OR LOWER(br.book.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        AND (
            :status IS NULL
            OR br.status = :status
        )
    """)
    Page<BorrowRequest> search(
            @Param("userId") Integer userId,
            @Param("keyword") String keyword,
            @Param("status") EBorrowRequestStatus status,
            Pageable pageable
    );

    @Query("""
        SELECT br
        FROM BorrowRequest br
        WHERE (
            :bookTitle IS NULL
            OR :bookTitle = ''
            OR LOWER(br.book.title)
                LIKE LOWER(CONCAT('%', :bookTitle, '%'))
        )
        AND (
            :fullName IS NULL
            OR :fullName = ''
            OR LOWER(br.user.fullName)
                LIKE LOWER(CONCAT('%', :fullName, '%'))
        )
        AND (
            :studentCode IS NULL
            OR :studentCode = ''
            OR LOWER(br.user.code)
                LIKE LOWER(CONCAT('%', :studentCode, '%'))
        )
        AND (
            :status IS NULL
            OR br.status = :status
        )
    """)
    Page<BorrowRequest> search(
            @Param("bookTitle") String bookTitle,
            @Param("fullName") String fullName,
            @Param("studentCode") String studentCode,
            @Param("status") EBorrowRequestStatus status,
            Pageable pageable
    );
    // lấy danh sách sách mượn ngay từ trạng thái waiting
    @Query("""
        SELECT br
        FROM BorrowRequest br
        WHERE br.status = 'WAITING'
        AND (
            :keyword IS NULL
            OR :keyword = ''
            OR LOWER(br.book.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(br.user.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(br.user.code) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    """)
    Page<BorrowRequest> findBorrowersThisWeek(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
