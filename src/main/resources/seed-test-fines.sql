-- Chạy file này trong SSMS nếu DB đã có sẵn dữ liệu (spring.sql.init.mode=never)

IF NOT EXISTS (SELECT 1 FROM borrow_histories WHERE user_id = 3 AND copy_id = 1 AND return_date IS NOT NULL)
    INSERT INTO borrow_histories (user_id, copy_id, issued_by, borrow_date, due_date, return_date, return_confirmed_by, delete_flag)
    VALUES (3, 1, 2, '2026-05-01', '2026-05-15', '2026-05-22', 2, 0);

IF NOT EXISTS (SELECT 1 FROM borrow_histories WHERE user_id = 4 AND copy_id = 3 AND return_date IS NOT NULL)
    INSERT INTO borrow_histories (user_id, copy_id, issued_by, borrow_date, due_date, return_date, return_confirmed_by, delete_flag)
    VALUES (4, 3, 2, '2026-04-10', '2026-04-24', '2026-05-01', 2, 0);

IF NOT EXISTS (SELECT 1 FROM borrow_histories WHERE user_id = 5 AND copy_id = 5 AND return_date IS NOT NULL)
    INSERT INTO borrow_histories (user_id, copy_id, issued_by, borrow_date, due_date, return_date, return_confirmed_by, delete_flag)
    VALUES (5, 5, 2, '2026-03-01', '2026-03-15', '2026-03-20', 2, 0);

INSERT INTO fines (borrow_id, user_id, amount, reason, status, delete_flag)
SELECT bh.borrow_id, bh.user_id, v.amount, v.reason, 'UNPAID', 0
FROM (VALUES
    (3, 1, 50000, N'Trả trễ sách Clean Code 7 ngày'),
    (4, 3, 75000, N'Trả trễ sách Effective Java'),
    (5, 5, 30000, N'Trả trễ sách Refactoring')
) AS v(user_id, copy_id, amount, reason)
JOIN borrow_histories bh ON bh.user_id = v.user_id AND bh.copy_id = v.copy_id AND bh.return_date IS NOT NULL
WHERE NOT EXISTS (
    SELECT 1 FROM fines f WHERE f.borrow_id = bh.borrow_id AND f.delete_flag = 0
);

SELECT f.fine_id, u.email, u.full_name, b.title, f.amount, f.reason, f.status
FROM fines f
JOIN users u ON u.user_id = f.user_id
JOIN borrow_histories bh ON bh.borrow_id = f.borrow_id
JOIN book_copies bc ON bc.copy_id = bh.copy_id
JOIN books b ON b.book_id = bc.book_id
WHERE f.delete_flag = 0
ORDER BY f.fine_id;
