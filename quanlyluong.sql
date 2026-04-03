-- Full bootstrap for local development after accidental DROP.
-- Usage:
--   mariadb -uroot -p123456 < scripts/sql/bootstrap_full_reset.sql

DROP DATABASE IF EXISTS quan_ly_luong;
CREATE DATABASE quan_ly_luong CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE quan_ly_luong;

CREATE TABLE departments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    address TEXT,
    dob DATE,
    department_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_employees_department FOREIGN KEY (department_id) REFERENCES departments(id)
);

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(50) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status BIT NOT NULL,
    employee_id INT UNIQUE,
    CONSTRAINT fk_users_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(100),
    status BIT NOT NULL
);

CREATE TABLE permissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    code VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    status BIT
);

CREATE TABLE user_roles (
    user_id INT,
    role_id INT,
    status BIT,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE role_permissions (
    role_id INT,
    permission_id INT,
    status BIT,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE TABLE contracts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT,
    contract_type VARCHAR(50),
    base_salary DECIMAL(15,2),
    salary_coefficient DECIMAL(5,2),
    start_date DATE,
    end_date DATE,
    CONSTRAINT fk_contracts_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

CREATE TABLE attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT,
    work_date DATE,
    check_in TIME,
    check_out TIME,
    working_hours DECIMAL(5,2),
    CONSTRAINT fk_attendance_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

CREATE TABLE rewards (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT,
    amount DECIMAL(15,2),
    reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rewards_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

CREATE TABLE salaries (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT,
    `month` INT,
    `year` INT,
    base_salary DECIMAL(15,2),
    allowance DECIMAL(15,2),
    deduction DECIMAL(15,2),
    total_salary DECIMAL(15,2),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_salary_employee_month_year UNIQUE (employee_id, `month`, `year`),
    CONSTRAINT fk_salaries_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

CREATE TABLE token_black_list (
    id INT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    expiry_date DATE
);

INSERT INTO departments(name, description) VALUES
('IT', 'Cong nghe thong tin'),
('HR', 'Nhan su'),
('Finance', 'Tai chinh'),
('Marketing', 'Tiep thi'),
('Sales', 'Kinh doanh');

INSERT INTO employees(name, email, phone, address, dob, department_id) VALUES
('Admin System', 'admin@gmail.com', '0999999999', 'HN', '1990-01-01', 1),
('Nguyen Van A', 'a@gmail.com', '0123456789', 'HN', '2000-01-01', 1),
('Tran Thi B', 'b@gmail.com', '0123456790', 'HCM', '1999-02-02', 2),
('Le Van C', 'c@gmail.com', '0123456791', 'DN', '1998-03-03', 3),
('Pham Thi D', 'd@gmail.com', '0123456792', 'HP', '1997-04-04', 4),
('Hoang Van E', 'e@gmail.com', '0123456793', 'HN', '1996-05-05', 5);

INSERT INTO users(email, username, password, status, employee_id) VALUES
('admin@gmail.com', 'admin', '123456', 1, 1),
('a@gmail.com', 'userA', '123456', 1, 2),
('b@gmail.com', 'userB', '123456', 1, 3),
('c@gmail.com', 'userC', '123456', 1, 4),
('d@gmail.com', 'userD', '123456', 1, 5),
('e@gmail.com', 'userE', '123456', 1, 6);

INSERT INTO roles(name, code, description, status) VALUES
('USER', 'USER', 'Nhan vien', 1),
('HR', 'HR', 'Nhan su', 1),
('KETOAN', 'KETOAN', 'Ke toan', 1),
('ADMIN', 'ADMIN', 'Quan tri', 1),
('MANAGER', 'MANAGER', 'Quan ly', 1);

INSERT INTO permissions(name, code, description, status) VALUES
('Xem danh sach user', 'VIEW_USER_LIST', 'Xem danh sach user', 1),
('Xem chi tiet user', 'VIEW_USER_DETAIL', 'Xem chi tiet user', 1),
('Tao user', 'VIEW_USER_CREATE', 'Tao user', 1),
('Sua user', 'VIEW_USER_UPDATE', 'Sua user', 1),
('Xoa user', 'VIEW_USER_DELETE', 'Xoa user', 1),

('Xem danh sach employee', 'VIEW_EMPLOYEE_LIST', 'Xem danh sach nhan vien', 1),
('Xem chi tiet employee', 'VIEW_EMPLOYEE_DETAIL', 'Xem chi tiet nhan vien', 1),
('Tao employee', 'VIEW_EMPLOYEE_CREATE', 'Tao nhan vien', 1),
('Sua employee', 'VIEW_EMPLOYEE_UPDATE', 'Sua nhan vien', 1),
('Xoa employee', 'VIEW_EMPLOYEE_DELETE', 'Xoa nhan vien', 1),

('Xem danh sach attendance', 'VIEW_ATTENDANCE_LIST', 'Xem cham cong', 1),
('Xem chi tiet attendance', 'VIEW_ATTENDANCE_DETAIL', 'Xem chi tiet cham cong', 1),
('Tao attendance', 'VIEW_ATTENDANCE_CREATE', 'Cham cong', 1),
('Sua attendance', 'VIEW_ATTENDANCE_UPDATE', 'Sua cham cong', 1),
('Xoa attendance', 'VIEW_ATTENDANCE_DELETE', 'Xoa cham cong', 1),

('Xem danh sach reward', 'VIEW_REWARD_LIST', 'Xem thuong', 1),
('Xem chi tiet reward', 'VIEW_REWARD_DETAIL', 'Xem chi tiet thuong', 1),
('Tao reward', 'VIEW_REWARD_CREATE', 'Tao thuong', 1),
('Sua reward', 'VIEW_REWARD_UPDATE', 'Sua thuong', 1),
('Xoa reward', 'VIEW_REWARD_DELETE', 'Xoa thuong', 1),

('Xem danh sach salary', 'VIEW_SALARY_LIST', 'Xem bang luong', 1),
('Xem chi tiet salary', 'VIEW_SALARY_DETAIL', 'Xem chi tiet bang luong', 1),
('Tao salary', 'VIEW_SALARY_CREATE', 'Tinh luong', 1),
('Sua salary', 'VIEW_SALARY_UPDATE', 'Sua bang luong', 1),
('Xoa salary', 'VIEW_SALARY_DELETE', 'Xoa bang luong', 1),

('Xem danh sach department', 'VIEW_DEPARTMENT_LIST', 'Xem danh sach phong ban', 1),
('Xem chi tiet department', 'VIEW_DEPARTMENT_DETAIL', 'Xem chi tiet phong ban', 1),
('Tao department', 'VIEW_DEPARTMENT_CREATE', 'Tao phong ban', 1),
('Sua department', 'VIEW_DEPARTMENT_UPDATE', 'Sua phong ban', 1),
('Xoa department', 'VIEW_DEPARTMENT_DELETE', 'Xoa phong ban', 1),

('Xem danh sach permission', 'VIEW_PERMISSION_LIST', 'Xem danh sach quyen', 1),
('Xem chi tiet permission', 'VIEW_PERMISSION_DETAIL', 'Xem chi tiet quyen', 1),
('Tao permission', 'VIEW_PERMISSION_CREATE', 'Tao quyen', 1),
('Sua permission', 'VIEW_PERMISSION_UPDATE', 'Sua quyen', 1),
('Xoa permission', 'VIEW_PERMISSION_DELETE', 'Xoa quyen', 1),

('Xem danh sach role', 'VIEW_ROLE_LIST', 'Xem danh sach role', 1),
('Xem chi tiet role', 'VIEW_ROLE_DETAIL', 'Xem chi tiet role', 1),
('Tao role', 'VIEW_ROLE_CREATE', 'Tao role', 1),
('Sua role', 'VIEW_ROLE_UPDATE', 'Sua role', 1),
('Xoa role', 'VIEW_ROLE_DELETE', 'Xoa role', 1),

('Xem danh sach contract', 'VIEW_CONTRACT_LIST', 'Xem danh sach hop dong', 1),
('Xem chi tiet contract', 'VIEW_CONTRACT_DETAIL', 'Xem chi tiet hop dong', 1),
('Tao contract', 'VIEW_CONTRACT_CREATE', 'Tao hop dong', 1),
('Sua contract', 'VIEW_CONTRACT_UPDATE', 'Sua hop dong', 1),
('Xoa contract', 'VIEW_CONTRACT_DELETE', 'Xoa hop dong', 1),

('Xem danh sach token black list', 'VIEW_TOKEN_BLACK_LIST_LIST', 'Xem danh sach token black list', 1),
('Xem chi tiet token black list', 'VIEW_TOKEN_BLACK_LIST_DETAIL', 'Xem chi tiet token black list', 1),
('Tao token black list', 'VIEW_TOKEN_BLACK_LIST_CREATE', 'Tao token black list', 1),
('Sua token black list', 'VIEW_TOKEN_BLACK_LIST_UPDATE', 'Sua token black list', 1),
('Xoa token black list', 'VIEW_TOKEN_BLACK_LIST_DELETE', 'Xoa token black list', 1);

INSERT INTO user_roles(user_id, role_id, status)
SELECT u.id, r.id, 1
FROM users u
JOIN roles r ON r.code = 'ADMIN'
WHERE u.username = 'admin';

INSERT INTO user_roles(user_id, role_id, status)
SELECT u.id, r.id, 1
FROM users u
JOIN roles r ON r.code = 'USER'
WHERE u.username IN ('userA', 'userE');

INSERT INTO user_roles(user_id, role_id, status)
SELECT u.id, r.id, 1
FROM users u
JOIN roles r ON r.code = 'HR'
WHERE u.username = 'userB';

INSERT INTO user_roles(user_id, role_id, status)
SELECT u.id, r.id, 1
FROM users u
JOIN roles r ON r.code = 'KETOAN'
WHERE u.username = 'userC';

INSERT INTO user_roles(user_id, role_id, status)
SELECT u.id, r.id, 1
FROM users u
JOIN roles r ON r.code = 'MANAGER'
WHERE u.username = 'userD';

INSERT INTO role_permissions(role_id, permission_id, status)
SELECT r.id, p.id, 1
FROM roles r
JOIN permissions p
WHERE r.code = 'ADMIN';

INSERT INTO role_permissions(role_id, permission_id, status)
SELECT r.id, p.id, 1
FROM roles r
JOIN permissions p
WHERE r.code = 'HR'
AND p.code IN (
    'VIEW_EMPLOYEE_LIST',
    'VIEW_EMPLOYEE_DETAIL',
    'VIEW_EMPLOYEE_CREATE',
    'VIEW_EMPLOYEE_UPDATE',
    'VIEW_EMPLOYEE_DELETE',
    'VIEW_ATTENDANCE_LIST',
    'VIEW_ATTENDANCE_DETAIL',
    'VIEW_ATTENDANCE_CREATE',
    'VIEW_ATTENDANCE_UPDATE',
    'VIEW_ATTENDANCE_DELETE',
    'VIEW_REWARD_LIST',
    'VIEW_REWARD_DETAIL',
    'VIEW_REWARD_CREATE',
    'VIEW_REWARD_UPDATE',
    'VIEW_REWARD_DELETE',
    'VIEW_SALARY_LIST',
    'VIEW_SALARY_DETAIL'
);

INSERT INTO role_permissions(role_id, permission_id, status)
SELECT r.id, p.id, 1
FROM roles r
JOIN permissions p
WHERE r.code = 'KETOAN'
AND p.code IN (
    'VIEW_ATTENDANCE_LIST',
    'VIEW_ATTENDANCE_DETAIL',
    'VIEW_ATTENDANCE_CREATE',
    'VIEW_SALARY_LIST',
    'VIEW_SALARY_DETAIL',
    'VIEW_SALARY_CREATE',
    'VIEW_SALARY_UPDATE'
);

INSERT INTO role_permissions(role_id, permission_id, status)
SELECT r.id, p.id, 1
FROM roles r
JOIN permissions p
WHERE r.code = 'USER'
AND p.code IN (
    'VIEW_USER_DETAIL',
    'VIEW_ATTENDANCE_LIST',
    'VIEW_ATTENDANCE_DETAIL',
    'VIEW_ATTENDANCE_CREATE'
);

INSERT INTO role_permissions(role_id, permission_id, status)
SELECT r.id, p.id, 1
FROM roles r
JOIN permissions p
WHERE r.code = 'MANAGER'
AND p.code IN (
    'VIEW_ATTENDANCE_LIST',
    'VIEW_ATTENDANCE_DETAIL',
    'VIEW_ATTENDANCE_CREATE'
);

INSERT INTO contracts(employee_id, contract_type, base_salary, salary_coefficient, start_date, end_date) VALUES
(2, 'FULLTIME', 10000000, 1.5, '2024-01-01', '2025-01-01'),
(3, 'PARTTIME', 5000000, 1.2, '2024-01-01', '2025-01-01'),
(4, 'FULLTIME', 12000000, 1.6, '2024-01-01', '2025-01-01'),
(5, 'FULLTIME', 9000000, 1.3, '2024-01-01', '2025-01-01'),
(6, 'PARTTIME', 6000000, 1.1, '2024-01-01', '2025-01-01');

INSERT INTO attendance(employee_id, work_date, check_in, check_out, working_hours) VALUES
(2, '2026-03-01', '08:00:00', '17:00:00', 8),
(3, '2026-03-01', '08:30:00', '17:30:00', 8),
(4, '2026-03-01', '09:00:00', '18:00:00', 8),
(5, '2026-03-01', '08:15:00', '17:15:00', 8),
(6, '2026-03-01', '08:45:00', '17:45:00', 8);

INSERT INTO rewards(employee_id, amount, reason) VALUES
(2, 1000000, 'Thuong KPI'),
(3, 500000, 'Thuong chuyen can'),
(4, 1500000, 'Thuong du an'),
(5, 700000, 'Thuong thang'),
(6, 300000, 'Thuong nho');

INSERT INTO salaries(employee_id, `month`, `year`, base_salary, allowance, deduction, total_salary, status) VALUES
(2, 3, 2026, 10000000, 2000000, 200000, 11800000, 'DRAFT'),
(3, 3, 2026, 5000000, 1000000, 100000, 5900000, 'DRAFT'),
(4, 3, 2026, 12000000, 2500000, 300000, 14200000, 'DRAFT'),
(5, 3, 2026, 9000000, 1500000, 150000, 10350000, 'DRAFT'),
(6, 3, 2026, 6000000, 800000, 50000, 6750000, 'DRAFT');

CREATE INDEX idx_employees_department_id ON employees(department_id);
CREATE INDEX idx_users_employee_id ON users(employee_id);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);
CREATE INDEX idx_attendance_employee_id_work_date ON attendance(employee_id, work_date);
CREATE INDEX idx_contracts_employee_start_date ON contracts(employee_id, start_date);
CREATE INDEX idx_rewards_employee_created_at ON rewards(employee_id, created_at);

CREATE OR REPLACE VIEW vw_salary_enriched_export AS
SELECT
    a.id AS attendance_id,
    e.id AS employee_id,
    e.name AS employee_name,
    e.email AS employee_email,
    d.name AS department_name,
    u.username AS username,
    r.code AS role_code,
    p.code AS permission_code,
    c.contract_type AS contract_type,
    c.base_salary AS contract_base_salary,
    c.salary_coefficient AS salary_coefficient,
    a.work_date AS work_date,
    a.working_hours AS working_hours,
    COALESCE(rew.amount, 0) AS reward_amount
FROM attendance a
JOIN employees e ON e.id = a.employee_id
LEFT JOIN departments d ON d.id = e.department_id
LEFT JOIN users u ON u.employee_id = e.id
LEFT JOIN user_roles ur ON ur.user_id = u.id
LEFT JOIN roles r ON r.id = ur.role_id
LEFT JOIN role_permissions rp ON rp.role_id = r.id
LEFT JOIN permissions p ON p.id = rp.permission_id
LEFT JOIN contracts c ON c.employee_id = e.id
LEFT JOIN rewards rew
       ON rew.employee_id = e.id
      AND DATE(rew.created_at) = a.work_date;

DELIMITER //
CREATE PROCEDURE seed_attendance(IN p_days INT)
BEGIN
    DECLARE v_day INT DEFAULT 1;
    DECLARE v_employee_id INT;

    START TRANSACTION;
    WHILE v_day <= p_days DO
        SET v_employee_id = 2;
        WHILE v_employee_id <= 6 DO
            INSERT INTO attendance(employee_id, work_date, check_in, check_out, working_hours)
            VALUES (
                v_employee_id,
                DATE_ADD('2026-01-01', INTERVAL (v_day - 1) DAY),
                '08:00:00',
                '17:00:00',
                8.00
            );
            SET v_employee_id = v_employee_id + 1;
        END WHILE;
        SET v_day = v_day + 1;
    END WHILE;
    COMMIT;
END //
DELIMITER ;

CREATE DATABASE IF NOT EXISTS hr_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE OR REPLACE VIEW hr_test.v_salary_overview AS
SELECT
    s.id AS salary_id,
    s.employee_id,
    e.name AS employee_name,
    d.name AS department_name,
    s.month,
    s.year,
    s.total_salary,
    s.status
FROM quan_ly_luong.salaries s
JOIN quan_ly_luong.employees e ON e.id = s.employee_id
LEFT JOIN quan_ly_luong.departments d ON d.id = e.department_id;