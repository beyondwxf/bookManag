USE library_management;

INSERT INTO roles (id, name, code, description) VALUES
    (1, '系统管理员', 'ADMIN', '负责系统配置、账号管理与全局统计'),
    (2, '图书管理员', 'LIBRARIAN', '负责图书副本、读者档案、借阅与归还'),
    (3, '读者', 'READER', '负责图书检索与个人借阅查询');

INSERT INTO users (id, username, password, display_name, role_id, phone, status, deleted) VALUES
    (1, 'admin', '{noop}Admin@123', '系统管理员', 1, '13800000001', 'ACTIVE', 0),
    (2, 'librarian', '{noop}Admin@123', '一号馆员', 2, '13800000002', 'ACTIVE', 0),
    (3, 'reader01', '{noop}Admin@123', '张读者', 3, '13800000003', 'ACTIVE', 0);

INSERT INTO reader_profiles (id, user_id, reader_no, name, phone, email, id_card, status, deleted) VALUES
    (1, 3, 'R000003', '张读者', '13800000003', 'reader01@example.com', '110101199901010011', 'ACTIVE', 0);

INSERT INTO book_categories (id, name, code, sort_order, description, deleted) VALUES
    (1, '计算机', 'CS', 1, '计算机科学与软件工程', 0),
    (2, '文学', 'LIT', 2, '文学与经典阅读', 0),
    (3, '管理', 'MGT', 3, '管理与商业类图书', 0);

INSERT INTO books (id, category_id, isbn, title, author, publisher, publish_date, description, deleted) VALUES
    (1, 1, '9787115428028', 'Java 核心技术', 'Cay S. Horstmann', '机械工业出版社', '2023-01-10', '面向 Java 开发的核心语法与实践。', 0),
    (2, 1, '9787302659718', 'Spring Boot 实战', 'Craig Walls', '清华大学出版社', '2024-03-20', '覆盖 Spring Boot 开发与部署的实践指南。', 0),
    (3, 2, '9787020002207', '活着', '余华', '人民文学出版社', '2012-08-01', '经典文学作品。', 0);

INSERT INTO book_copies (id, book_id, barcode, location_code, status, version, deleted) VALUES
    (1, 1, 'BC20260001', 'A-01-01', 'AVAILABLE', 0, 0),
    (2, 1, 'BC20260002', 'A-01-02', 'BORROWED', 0, 0),
    (3, 2, 'BC20260003', 'A-02-01', 'AVAILABLE', 0, 0),
    (4, 3, 'BC20260004', 'B-01-01', 'AVAILABLE', 0, 0);

INSERT INTO loan_records (id, loan_no, reader_id, book_copy_id, borrowed_by, returned_by, borrow_time, due_date, return_time, status, deleted) VALUES
    (1, 'L202605010001', 1, 2, 2, NULL, DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 5 DAY), NULL, 'BORROWED', 0);
