-- 10 phiếu phạt UNPAID cho user_id = 3 (chien181004@gmail.com)
-- Mỗi borrow_id chỉ được 1 phiếu phạt (unique) → tạo 10 phiếu mượn đã trả riêng

INSERT INTO borrow_histories (user_id, copy_id, issued_by, borrow_date, due_date, return_date, return_confirmed_by, delete_flag)
SELECT v.user_id, v.copy_id, 2, v.borrow_date, v.due_date, v.return_date, 2, 0
FROM (VALUES
    (3, 7,  '2026-01-05', '2026-01-19', '2026-01-25'),
    (3, 8,  '2026-01-20', '2026-02-03', '2026-02-10'),
    (3, 10, '2026-02-11', '2026-02-25', '2026-03-05'),
    (3, 11, '2026-03-06', '2026-03-20', '2026-03-28'),
    (3, 12, '2026-03-29', '2026-04-12', '2026-04-20'),
    (3, 3,  '2026-04-21', '2026-05-05', '2026-05-12'),
    (3, 4,  '2026-05-13', '2026-05-27', '2026-06-03'),
    (3, 5,  '2026-05-14', '2026-05-28', '2026-06-05'),
    (3, 6,  '2026-05-15', '2026-05-29', '2026-06-06'),
    (3, 1,  '2026-05-16', '2026-05-30', '2026-06-07')
) AS v(user_id, copy_id, borrow_date, due_date, return_date)
WHERE NOT EXISTS (
    SELECT 1 FROM borrow_histories bh
    WHERE bh.user_id = v.user_id
      AND bh.copy_id = v.copy_id
      AND bh.borrow_date = v.borrow_date
      AND bh.delete_flag = 0
);

INSERT INTO fines (borrow_id, user_id, amount, reason, status, delete_flag)
SELECT bh.borrow_id, 3, v.amount, v.reason, 'UNPAID', 0
FROM (VALUES
    (7,  10000, N'Phạt trễ hạn #1 - Machine Learning Yearning'),
    (8,  15000, N'Phạt trễ hạn #2 - Machine Learning Yearning'),
    (10, 20000, N'Phạt trễ hạn #3 - Atomic Habits'),
    (11, 25000, N'Phạt trễ hạn #4 - Domain Driven Design'),
    (12, 30000, N'Phạt trễ hạn #5 - Domain Driven Design'),
    (3,  35000, N'Phạt trễ hạn #6 - Effective Java'),
    (4,  40000, N'Phạt trễ hạn #7 - Effective Java'),
    (5,  45000, N'Phạt trễ hạn #8 - Refactoring'),
    (6,  50000, N'Phạt trễ hạn #9 - Refactoring'),
    (1,  55000, N'Phạt trễ hạn #10 - Clean Code')
) AS v(copy_id, amount, reason)
JOIN borrow_histories bh ON bh.user_id = 3 AND bh.copy_id = v.copy_id AND bh.return_date IS NOT NULL AND bh.delete_flag = 0
WHERE NOT EXISTS (
    SELECT 1 FROM fines f WHERE f.borrow_id = bh.borrow_id AND f.delete_flag = 0
);

-- Nếu chưa có phiếu phạt cho borrow_id = 4 (user 3, copy 1 lần mượn đầu)
IF EXISTS (SELECT 1 FROM borrow_histories WHERE borrow_id = 4 AND user_id = 3)
   AND NOT EXISTS (SELECT 1 FROM fines WHERE borrow_id = 4 AND delete_flag = 0)
    INSERT INTO fines (borrow_id, user_id, amount, reason, status, delete_flag)
    VALUES (4, 3, 50000, N'Trả trễ sách Clean Code 7 ngày', 'UNPAID', 0);

SELECT f.fine_id, f.borrow_id, u.email, b.title, f.amount, f.reason, f.status
FROM fines f
JOIN users u ON u.user_id = f.user_id
JOIN borrow_histories bh ON bh.borrow_id = f.borrow_id
JOIN book_copies bc ON bc.copy_id = bh.copy_id
JOIN books b ON b.book_id = bc.book_id
WHERE f.user_id = 3 AND f.delete_flag = 0
ORDER BY f.fine_id;
