-- V8: Add base permissions for surveyor role and notification view for user role

-- Assign notification view and file upload to surveyor role
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'ROLE_SURVEYOR'
  AND p.perm_code IN ('file:upload', 'notification:view');

-- Assign notification view to user role (if not already)
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'ROLE_USER'
  AND p.perm_code = 'notification:view'
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
