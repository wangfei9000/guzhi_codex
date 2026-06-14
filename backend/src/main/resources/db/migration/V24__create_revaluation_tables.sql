-- V24: Create revaluation tables and one-click revaluation permission

CREATE TABLE IF NOT EXISTS revaluation_record (
    id BIGSERIAL PRIMARY KEY,
    organization_id BIGINT,
    revaluation_date DATE,
    result VARCHAR(20) NOT NULL,
    file_url VARCHAR(500),
    remark TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_revaluation_record_organization_id
    ON revaluation_record (organization_id);

DROP TRIGGER IF EXISTS trg_revaluation_record_updated_at ON revaluation_record;
CREATE TRIGGER trg_revaluation_record_updated_at
    BEFORE UPDATE ON revaluation_record
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TABLE IF NOT EXISTS revaluation_project (
    id BIGSERIAL PRIMARY KEY,
    revaluation_id BIGINT NOT NULL,
    project_code VARCHAR(50) NOT NULL,
    unit_price DECIMAL(12, 2),
    total_price DECIMAL(14, 2),
    remark TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_revaluation_project_revaluation_id
    ON revaluation_project (revaluation_id);

DROP TRIGGER IF EXISTS trg_revaluation_project_updated_at ON revaluation_project;
CREATE TRIGGER trg_revaluation_project_updated_at
    BEFORE UPDATE ON revaluation_project
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

INSERT INTO sys_permission (perm_name, perm_code, parent_id, type, path, icon, sort_order)
SELECT '一键复估', 'bank:revaluation', id, 'MENU', '/bank/revaluation', 'RedoOutlined', 4
FROM sys_permission
WHERE perm_code = 'bank:manage'
ON CONFLICT (perm_code) DO NOTHING;

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
CROSS JOIN sys_permission p
WHERE r.role_code = 'ROLE_ADMIN'
  AND p.perm_code = 'bank:revaluation'
ON CONFLICT (role_id, permission_id) DO NOTHING;
