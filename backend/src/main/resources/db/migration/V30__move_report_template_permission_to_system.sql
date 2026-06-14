-- V30: Move report template menu under system management

UPDATE sys_permission child
SET parent_id = parent.id,
    sort_order = 5,
    path = '/report/template-list',
    icon = 'FileTextOutlined'
FROM sys_permission parent
WHERE child.perm_code = 'project:report-template'
  AND parent.perm_code = 'system:manage';

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT DISTINCT rp.role_id, system_menu.id
FROM sys_role_permission rp
JOIN sys_permission report_template ON report_template.id = rp.permission_id
JOIN sys_permission system_menu ON system_menu.perm_code = 'system:manage'
WHERE report_template.perm_code = 'project:report-template'
ON CONFLICT (role_id, permission_id) DO NOTHING;
