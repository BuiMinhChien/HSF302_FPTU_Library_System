package com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.entity.BorrowHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface BorrowHistoryRepository extends JpaRepository<BorrowHistory, Integer> {
    //cái phaanf query naày chỉ viết do phan truong hop nguoi dung nhap null thi nó ko có hàm sẵn hỗ trợ
    @Query("""
        SELECT bh
        FROM BorrowHistory bh
        WHERE bh.user.userId = :userId
        AND (
            :keyword IS NULL
            OR :keyword = ''
            OR LOWER(bh.copy.book.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    """)

    Page<BorrowHistory> search(
            @Param("userId") Integer userId,   // ID của user đang đăng nhập
            @Param("keyword") String keyword,  // từ khóa tìm kiếm (tên sách)
            Pageable pageable                  // thông tin phân trang (page, size, sort)
    );
    // Dành cho thủ thư có thể xem, kiếm người mượn sách tuần này
    @Query("""
        SELECT bh FROM BorrowHistory bh
        WHERE bh.borrowDate >= :startDate AND bh.borrowDate <= :endDate
        AND (:keyword IS NULL OR :keyword = ''
             OR LOWER(bh.user.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(bh.user.code) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(bh.copy.book.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:isReturned IS NULL 
             OR (:isReturned = true AND bh.returnDate IS NOT NULL) 
             OR (:isReturned = false AND bh.returnDate IS NULL))
    """)
    Page<BorrowHistory> findBorrowersThisWeek(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate,
            @Param("keyword") String keyword,
            @Param("isReturned") Boolean isReturned,
            Pageable pageable
    );
}
//param là gán giá trị vô câu query