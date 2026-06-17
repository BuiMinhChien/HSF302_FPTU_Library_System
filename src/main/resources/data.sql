DELETE
FROM categories
WHERE category_name IN (N'Công nghệ thông tin', N'Khoa học dữ liệu', N'Trí tuệ nhân tạo', N'Kinh doanh', N'Ngoại ngữ');
INSERT INTO categories (category_name, description)
VALUES (N'Công nghệ thông tin', N'Sách về lập trình và phát triển phần mềm'),
       (N'Khoa học dữ liệu', N'Sách về Data Science và Big Data'),
       (N'Trí tuệ nhân tạo', N'Sách về Machine Learning và AI'),
       (N'Kinh doanh', N'Sách về quản trị và kinh doanh'),
       (N'Ngoại ngữ', N'Sách học ngoại ngữ');

DELETE
FROM authors
WHERE author_name IN (N'Robert C. Martin', N'Joshua Bloch', N'Martin Fowler', N'Andrew Ng', N'James Clear', N'Eric Evans');
INSERT INTO authors (author_name, biography)
VALUES (N'Robert C. Martin', N'Tác giả Clean Code'),
       (N'Joshua Bloch', N'Tác giả Effective Java'),
       (N'Martin Fowler', N'Chuyên gia Software Architecture'),
       (N'Andrew Ng', N'Chuyên gia AI và Machine Learning'),
       (N'James Clear', N'Tác giả Atomic Habits'),
       (N'Eric Evans', N'Tác giả Domain Driven Design');

DELETE
FROM users
WHERE code IN ('ADMIN001', 'LIB001', 'SE180001', 'SE180002', 'SE180003');
INSERT INTO users (code, full_name, email, password, phone, address, role, status)
VALUES ('ADMIN001', N'System Administrator', 'admin@fpt.edu.vn', '$2a$12$dn1ur5pSdrEtTtXuAB78yOSrnEJBh6IJA1cWA.qpNKfHxCoVmfTt.', '0901111111', N'Hà Nội', 'ADMIN', 1),
       ('LIB001', N'Nguyễn Văn Thư', 'librarian@fpt.edu.vn', '$2a$12$dn1ur5pSdrEtTtXuAB78yOSrnEJBh6IJA1cWA.qpNKfHxCoVmfTt.', '0902222222', N'Hà Nội', 'LIBRARIAN', 1),
       ('SE180001', N'Bùi Minh Chiến', 'chiense180001@fpt.edu.vn', '$2a$12$dn1ur5pSdrEtTtXuAB78yOSrnEJBh6IJA1cWA.qpNKfHxCoVmfTt.', '0903333333', N'Hà Nội', 'READER', 1),
       ('SE180002', N'Nguyễn Văn A', 'vana@fpt.edu.vn', '$2a$12$dn1ur5pSdrEtTtXuAB78yOSrnEJBh6IJA1cWA.qpNKfHxCoVmfTt.', '0904444444', N'Hà Nội', 'READER', 1),
       ('SE180003', N'Trần Thị B', 'thib@fpt.edu.vn', '$2a$12$dn1ur5pSdrEtTtXuAB78yOSrnEJBh6IJA1cWA.qpNKfHxCoVmfTt.', '0905555555', N'Hà Nội', 'READER', 1);

DELETE
FROM books
WHERE isbn IN ('LIB-ISBN-001', 'LIB-ISBN-002', 'LIB-ISBN-003', 'LIB-ISBN-004', 'LIB-ISBN-005', 'LIB-ISBN-006');
INSERT INTO books (isbn, title, publisher, publish_year, category_id, description)
VALUES ('LIB-ISBN-001', N'Clean Code', N'Prentice Hall', 2008, 1, N'Best practices for writing clean code'),
       ('LIB-ISBN-002', N'Effective Java', N'Addison Wesley', 2018, 1, N'Java best practices'),
       ('LIB-ISBN-003', N'Refactoring', N'Addison Wesley', 2019, 1, N'Improving software design'),
       ('LIB-ISBN-004', N'Machine Learning Yearning', N'DeepLearning.ai', 2018, 3, N'Guide to ML projects'),
       ('LIB-ISBN-005', N'Atomic Habits', N'Avery', 2018, 4, N'Build good habits'),
       ('LIB-ISBN-006', N'Domain Driven Design', N'Pearson', 2003, 1, N'DDD principles');

DELETE
FROM book_author
WHERE book_id IN (SELECT book_id
                  FROM books
                  WHERE isbn LIKE 'LIB-ISBN-%');
INSERT INTO book_author (book_id, author_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5),
       (6, 6);

DELETE
FROM book_copies
WHERE barcode LIKE 'LIB-COPY-%';
INSERT INTO book_copies (book_id, barcode, status)
VALUES (1, 'LIB-COPY-001', 'AVAILABLE'),
       (1, 'LIB-COPY-002', 'BORROWED'),
       (2, 'LIB-COPY-003', 'AVAILABLE'),
       (2, 'LIB-COPY-004', 'AVAILABLE'),
       (3, 'LIB-COPY-005', 'AVAILABLE'),
       (3, 'LIB-COPY-006', 'DAMAGED'),
       (4, 'LIB-COPY-007', 'AVAILABLE'),
       (4, 'LIB-COPY-008', 'AVAILABLE'),
       (5, 'LIB-COPY-009', 'BORROWED'),
       (5, 'LIB-COPY-010', 'AVAILABLE'),
       (6, 'LIB-COPY-011', 'AVAILABLE'),
       (6, 'LIB-COPY-012', 'LOST');