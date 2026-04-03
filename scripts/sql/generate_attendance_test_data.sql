USE quan_ly_luong;

DELIMITER //

CREATE OR REPLACE PROCEDURE seed_attendance(IN p_days INT)
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

-- Example: generate ~18,250 rows (5 employees x 3,650 days)
-- CALL seed_attendance(3650);
