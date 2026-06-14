-- V25: Add bank revaluation list permission

INSERT INTO sys_permission (perm_name, perm_code, parent_id, type, path, icon, sort_order)
SELECT '复估列表', 'bank:revaluation-list', id, 'MENU', '/bank/revaluation-list', 'FileExcelOutlined', 5
FROM sys_permission
WHERE perm_code = 'bank:manage'
ON CONFLICT (perm_code) DO NOTHING;

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
CROSS JOIN sys_permission p
WHERE r.role_code = 'ROLE_ADMIN'
  AND p.perm_code = 'bank:revaluation-list'
ON CONFLICT (role_id, permission_id) DO NOTHING;
