USE quan_ly_luong;

-- Grant attendance permissions for MANAGER (idempotent patch for existing DBs)
INSERT INTO role_permissions (role_id, permission_id, status)
SELECT r.id, p.id, 1
FROM roles r
JOIN permissions p ON p.code IN (
    'VIEW_ATTENDANCE_LIST',
    'VIEW_ATTENDANCE_DETAIL',
    'VIEW_ATTENDANCE_CREATE'
)
LEFT JOIN role_permissions rp
       ON rp.role_id = r.id
      AND rp.permission_id = p.id
WHERE r.code = 'MANAGER'
  AND rp.role_id IS NULL;
