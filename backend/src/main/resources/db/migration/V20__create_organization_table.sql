-- V20: Create organization table and link users to organizations

CREATE TABLE IF NOT EXISTS sys_organization (
    id BIGSERIAL PRIMARY KEY,
    organization_type VARCHAR(100),
    organization_name VARCHAR(200) NOT NULL,
    contact_name VARCHAR(100),
    contact_phone VARCHAR(30),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP TRIGGER IF EXISTS trg_sys_organization_updated_at ON sys_organization;
CREATE TRIGGER trg_sys_organization_updated_at
    BEFORE UPDATE ON sys_organization
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

ALTER TABLE sys_user
    ADD COLUMN IF NOT EXISTS organization_id BIGINT;

ALTER TABLE sys_user
    DROP CONSTRAINT IF EXISTS fk_sys_user_organization;
ALTER TABLE sys_user
    ADD CONSTRAINT fk_sys_user_organization
    FOREIGN KEY (organization_id) REFERENCES sys_organization(id) ON DELETE SET NULL;

INSERT INTO sys_permission (perm_name, perm_code, parent_id, type, path, icon, sort_order)
SELECT '机构管理', 'organization:list', id, 'MENU', '/system/organization', 'BankOutlined', 4
FROM sys_permission
WHERE perm_code = 'system:manage'
ON CONFLICT (perm_code) DO NOTHING;

INSERT INTO sys_permission (perm_name, perm_code, parent_id, type, path, icon, sort_order)
SELECT '机构创建', 'organization:create', id, 'BUTTON', NULL, NULL, 1
FROM sys_permission
WHERE perm_code = 'organization:list'
ON CONFLICT (perm_code) DO NOTHING;

INSERT INTO sys_permission (perm_name, perm_code, parent_id, type, path, icon, sort_order)
SELECT '机构编辑', 'organization:update', id, 'BUTTON', NULL, NULL, 2
FROM sys_permission
WHERE perm_code = 'organization:list'
ON CONFLICT (perm_code) DO NOTHING;

INSERT INTO sys_permission (perm_name, perm_code, parent_id, type, path, icon, sort_order)
SELECT '机构删除', 'organization:delete', id, 'BUTTON', NULL, NULL, 3
FROM sys_permission
WHERE perm_code = 'organization:list'
ON CONFLICT (perm_code) DO NOTHING;

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
CROSS JOIN sys_permission p
WHERE r.role_code = 'ROLE_ADMIN'
  AND p.perm_code IN ('organization:list', 'organization:create', 'organization:update', 'organization:delete')
ON CONFLICT (role_id, permission_id) DO NOTHING;
