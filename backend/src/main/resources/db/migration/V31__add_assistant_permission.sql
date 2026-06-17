-- V31: Add my assistant menu permission and grant it to all existing roles

INSERT INTO sys_permission (perm_name, perm_code, parent_id, type, path, icon, sort_order)
VALUES ('我的助理', 'assistant:use', NULL, 'MENU', '/assistant', 'MessageOutlined', 5)
ON CONFLICT (perm_code) DO UPDATE SET
    perm_name = EXCLUDED.perm_name,
    parent_id = EXCLUDED.parent_id,
    type = EXCLUDED.type,
    path = EXCLUDED.path,
    icon = EXCLUDED.icon,
    sort_order = EXCLUDED.sort_order;

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
CROSS JOIN sys_permission p
WHERE p.perm_code = 'assistant:use'
ON CONFLICT (role_id, permission_id) DO NOTHING;
