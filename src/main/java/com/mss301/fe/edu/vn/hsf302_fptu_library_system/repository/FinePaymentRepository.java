package com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.FinePayment;
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
}
