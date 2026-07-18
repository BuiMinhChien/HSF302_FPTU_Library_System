package com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.FinePaymentHistoryViewDTO;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.FinePayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FinePaymentRepository extends JpaRepository<FinePayment, Integer> {

    @Query("""
            SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
            FROM FinePayment p
            WHERE p.fine.fineId = :fineId AND p.deleteFlag = false
            """)
    boolean existsByFineId(@Param("fineId") Integer fineId);

    @Query("""
            SELECT new com.mss301.fe.edu.vn.hsf302_fptu_library_system.dto.FinePaymentHistoryViewDTO(
                p.paymentId,
                f.fineId,
                COALESCE(b.title, ''),
                f.reason,
                p.amount,
                p.paymentDate,
                p.paymentMethod
            )
            FROM FinePayment p
            JOIN p.fine f
            JOIN f.user u
            LEFT JOIN f.borrowHistory bh
            LEFT JOIN bh.copy c
            LEFT JOIN c.book b
            WHERE p.deleteFlag = false
            AND u.userId = :userId
            AND (
                :bookTitle IS NULL
                OR :bookTitle = ''
                OR LOWER(COALESCE(b.title, ''))
                    LIKE LOWER(CONCAT('%', :bookTitle, '%'))
            )
            """)
    Page<FinePaymentHistoryViewDTO> searchByUser(
            @Param("userId") Integer userId,
            @Param("bookTitle") String bookTitle,
            Pageable pageable
    );
}