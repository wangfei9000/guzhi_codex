-- V21: Add bank business manual valuation permissions

INSERT INTO sys_permission (perm_name, perm_code, parent_id, type, path, icon, sort_order)
VALUES ('银行业务', 'bank:manage', NULL, 'MENU', '/bank', 'BankOutlined', 4)
ON CONFLICT (perm_code) DO NOTHING;

INSERT INTO sys_permission (perm_name, perm_code, parent_id, type, path, icon, sort_order)
SELECT '人工估值', 'bank:manual-valuation', id, 'MENU', '/bank/manual-valuation', 'CalculatorOutlined', 1
FROM sys_permission
WHERE perm_code = 'bank:manage'
ON CONFLICT (perm_code) DO NOTHING;

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
CROSS JOIN sys_permission p
WHERE r.role_code = 'ROLE_ADMIN'
  AND p.perm_code IN ('bank:manage', 'bank:manual-valuation')
ON CONFLICT (role_id, permission_id) DO NOTHING;
