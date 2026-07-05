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

    @Query("""
            SELECT bh FROM BorrowHistory bh
            LEFT JOIN FETCH bh.user u
            LEFT JOIN FETCH bh.copy c
            LEFT JOIN FETCH c.book
            WHERE bh.returnDate IS NOT NULL
            AND NOT EXISTS (
                SELECT 1 FROM Fine f
                WHERE f.borrowHistory.borrowId = bh.borrowId AND f.deleteFlag = false
            )
            ORDER BY bh.returnDate DESC
            """)
    java.util.List<BorrowHistory> findReturnedWithoutFine();

    // Lấy danh sách sách đang mượn chưa trả (returnDate còn trống)
    java.util.List<BorrowHistory> findByReturnDateIsNull();
}

//param là gán giá trị vô câu query