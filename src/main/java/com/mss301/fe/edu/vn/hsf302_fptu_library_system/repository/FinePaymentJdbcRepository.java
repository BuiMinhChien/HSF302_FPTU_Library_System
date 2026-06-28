package com.mss301.fe.edu.vn.hsf302_fptu_library_system.repository;

import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EPaymentMethod;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.constant.EPaymentStatus;
import com.mss301.fe.edu.vn.hsf302_fptu_library_system.model.FinePaymentRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FinePaymentJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<FinePaymentRecord> ROW_MAPPER = (rs, rowNum) ->
            FinePaymentRecord.builder()
                    .id(rs.getLong("id"))
                    .fineId(rs.getInt("fine_id"))
                    .readerId(rs.getInt("reader_id"))
                    .orderCode(rs.getLong("order_code"))
                    .amount(rs.getBigDecimal("amount"))
                    .paymentMethod(EPaymentMethod.valueOf(rs.getString("payment_method")))
                    .paymentStatus(EPaymentStatus.valueOf(rs.getString("payment_status")))
                    .checkoutUrl(rs.getString("checkout_url"))
                    .transactionCode(rs.getString("transaction_code"))
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .paidAt(Optional.ofNullable(rs.getTimestamp("paid_at"))
                            .map(Timestamp::toLocalDateTime).orElse(null))
                    .cancelledAt(Optional.ofNullable(rs.getTimestamp("cancelled_at"))
                            .map(Timestamp::toLocalDateTime).orElse(null))
                    .build();

    public Long insertPending(FinePaymentRecord record) {
        String sql = """
                INSERT INTO fine_payos_payments
                (fine_id, reader_id, order_code, amount, payment_method, payment_status, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, record.getFineId());
            ps.setInt(2, record.getReaderId());
            ps.setLong(3, record.getOrderCode());
            ps.setBigDecimal(4, record.getAmount());
            ps.setString(5, record.getPaymentMethod().name());
            ps.setString(6, record.getPaymentStatus().name());
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Không thể tạo bản ghi thanh toán");
        }
        return key.longValue();
    }

    public void updateCheckoutUrl(Long id, String checkoutUrl) {
        jdbcTemplate.update(
                "UPDATE fine_payos_payments SET checkout_url = ? WHERE id = ?",
                checkoutUrl, id);
    }

    public Optional<FinePaymentRecord> findByOrderCode(Long orderCode) {
        List<FinePaymentRecord> list = jdbcTemplate.query(
                "SELECT * FROM fine_payos_payments WHERE order_code = ?",
                ROW_MAPPER, orderCode);
        return list.stream().findFirst();
    }

    public boolean existsPendingByFineId(Integer fineId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM fine_payos_payments WHERE fine_id = ? AND payment_status = 'PENDING'",
                Integer.class, fineId);
        return count != null && count > 0;
    }

    public void markSuccess(Long id, String transactionCode) {
        jdbcTemplate.update("""
                UPDATE fine_payos_payments
                SET payment_status = 'SUCCESS', transaction_code = ?, paid_at = ?
                WHERE id = ?
                """, transactionCode, Timestamp.valueOf(LocalDateTime.now()), id);
    }

    public void markFailed(Long id) {
        jdbcTemplate.update(
                "UPDATE fine_payos_payments SET payment_status = 'FAILED' WHERE id = ?",
                id);
    }

    public void markCancelled(Long id) {
        jdbcTemplate.update("""
                UPDATE fine_payos_payments
                SET payment_status = 'CANCELLED', cancelled_at = ?
                WHERE id = ?
                """, Timestamp.valueOf(LocalDateTime.now()), id);
    }
}
