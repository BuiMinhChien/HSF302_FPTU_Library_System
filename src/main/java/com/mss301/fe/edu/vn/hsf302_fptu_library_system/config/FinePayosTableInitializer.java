package com.mss301.fe.edu.vn.hsf302_fptu_library_system.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinePayosTableInitializer {

    private final JdbcTemplate jdbcTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void createTableIfNotExists() {
        String sql = """
                IF OBJECT_ID('fine_payos_payments', 'U') IS NULL
                BEGIN
                    CREATE TABLE fine_payos_payments (
                        id               BIGINT IDENTITY(1,1) PRIMARY KEY,
                        fine_id          INT           NOT NULL,
                        reader_id        INT           NOT NULL,
                        order_code       BIGINT        NOT NULL,
                        amount           DECIMAL(18,2) NOT NULL,
                        payment_method   NVARCHAR(20)  NOT NULL DEFAULT N'PAYOS',
                        payment_status   NVARCHAR(20)  NOT NULL DEFAULT N'PENDING',
                        checkout_url     NVARCHAR(500) NULL,
                        transaction_code NVARCHAR(100) NULL,
                        created_at       DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
                        paid_at          DATETIME2     NULL,
                        cancelled_at     DATETIME2     NULL,
                        CONSTRAINT uk_fine_payos_order_code UNIQUE (order_code)
                    );
                    CREATE INDEX idx_fine_payos_fine_id ON fine_payos_payments(fine_id);
                    CREATE INDEX idx_fine_payos_reader_id ON fine_payos_payments(reader_id);
                END
                """;
        try {
            jdbcTemplate.execute(sql);
            log.info("Bảng fine_payos_payments đã sẵn sàng");
        } catch (Exception e) {
            log.error("Không thể tạo bảng fine_payos_payments: {}", e.getMessage());
        }
    }
}
