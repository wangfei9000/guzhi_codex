-- V23: Add bank valuation list permission

INSERT INTO sys_permission (perm_name, perm_code, parent_id, type, path, icon, sort_order)
SELECT '估值列表', 'bank:valuation-list', id, 'MENU', '/bank/valuation-list', 'UnorderedListOutlined', 3
FROM sys_permission
WHERE perm_code = 'bank:manage'
ON CONFLICT (perm_code) DO NOTHING;

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
CROSS JOIN sys_permission p
WHERE r.role_code = 'ROLE_ADMIN'
  AND p.perm_code = 'bank:valuation-list'
ON CONFLICT (role_id, permission_id) DO NOTHING;
