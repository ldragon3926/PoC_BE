USE quan_ly_luong;

-- Indexes to optimize multi-table joins used by Kafka Connect source query/view.
CREATE INDEX IF NOT EXISTS idx_employees_department_id ON employees(department_id);
CREATE INDEX IF NOT EXISTS idx_users_employee_id ON users(employee_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX IF NOT EXISTS idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX IF NOT EXISTS idx_role_permissions_permission_id ON role_permissions(permission_id);
CREATE INDEX IF NOT EXISTS idx_attendance_employee_id_work_date ON attendance(employee_id, work_date);
CREATE INDEX IF NOT EXISTS idx_contracts_employee_start_date ON contracts(employee_id, start_date);
CREATE INDEX IF NOT EXISTS idx_rewards_employee_created_at ON rewards(employee_id, created_at);

-- A wide view (4-7+ fields from multiple tables) to feed Kafka Connect JDBC Source.
CREATE OR REPLACE VIEW vw_salary_enriched_export AS
SELECT
    a.id AS attendance_id,                 -- Incrementing key for connector offset
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
