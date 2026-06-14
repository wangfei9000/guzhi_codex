-- V28: Create report template table and menu permission

CREATE TABLE IF NOT EXISTS report_template (
    id BIGSERIAL PRIMARY KEY,
    template_name VARCHAR(100) NOT NULL,
    template_content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT '启用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP TRIGGER IF EXISTS trg_report_template_updated_at ON report_template;
CREATE TRIGGER trg_report_template_updated_at
    BEFORE UPDATE ON report_template
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

INSERT INTO sys_permission (perm_name, perm_code, parent_id, type, path, icon, sort_order)
SELECT '报告模版', 'project:report-template', id, 'MENU', '/report/template-list', 'FileTextOutlined', 5
FROM sys_permission
WHERE perm_code = 'project:manage'
ON CONFLICT (perm_code) DO NOTHING;

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
CROSS JOIN sys_permission p
WHERE r.role_code = 'ROLE_ADMIN'
  AND p.perm_code = 'project:report-template'
ON CONFLICT (role_id, permission_id) DO NOTHING;
