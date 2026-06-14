-- V9: Add business permissions and assign to roles

INSERT INTO sys_permission (perm_name, perm_code, parent_id, type, path, icon, sort_order) VALUES
('个人业务', 'project:manage', NULL, 'MENU', '/project', NULL, 4),
('项目列表', 'project:list', (SELECT id FROM sys_permission WHERE perm_code = 'project:manage'), 'MENU', '/project/list', NULL, 1),
('项目创建', 'project:create', (SELECT id FROM sys_permission WHERE perm_code = 'project:list'), 'BUTTON', NULL, NULL, 1),
('外勘列表', 'project:surv', (SELECT id FROM sys_permission WHERE perm_code = 'project:manage'), 'MENU', '/survey/list', NULL, 2),
('报告列表', 'project:report', (SELECT id FROM sys_permission WHERE perm_code = 'project:manage'), 'MENU', '/report/list', NULL, 3),
('盖章列表', 'project:seal', (SELECT id FROM sys_permission WHERE perm_code = 'project:manage'), 'MENU', '/seal/list', NULL, 4);

-- Assign all business permissions to admin
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'ROLE_ADMIN'
  AND p.perm_code IN ('project:manage', 'project:list', 'project:create', 'project:surv', 'project:report', 'project:seal')
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- Assign view permissions to user role
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'ROLE_USER'
  AND p.perm_code IN ('project:list', 'project:surv', 'project:report', 'project:seal')
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- Assign view permissions to surveyor role
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'ROLE_SURVEYOR'
  AND p.perm_code IN ('project:list', 'project:surv', 'project:report', 'project:seal')
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
