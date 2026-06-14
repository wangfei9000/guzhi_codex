-- V22: Add project valuation type and bank auto valuation permission

ALTER TABLE project
    ADD COLUMN IF NOT EXISTS valuation_type VARCHAR(20);

INSERT INTO sys_permission (perm_name, perm_code, parent_id, type, path, icon, sort_order)
SELECT '自动估值', 'bank:auto-valuation', id, 'MENU', '/bank/auto-valuation', 'ThunderboltOutlined', 2
FROM sys_permission
WHERE perm_code = 'bank:manage'
ON CONFLICT (perm_code) DO NOTHING;

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
CROSS JOIN sys_permission p
WHERE r.role_code = 'ROLE_ADMIN'
  AND p.perm_code = 'bank:auto-valuation'
ON CONFLICT (role_id, permission_id) DO NOTHING;
