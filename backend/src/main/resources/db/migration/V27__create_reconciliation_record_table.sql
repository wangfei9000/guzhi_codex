-- V27: Create reconciliation record table and bank reconciliation permission

CREATE TABLE IF NOT EXISTS reconciliation_record (
    id BIGSERIAL PRIMARY KEY,
    organization_id BIGINT,
    start_time DATE NOT NULL,
    end_time DATE NOT NULL,
    reconciliation_date DATE,
    result VARCHAR(20) NOT NULL,
    file_url VARCHAR(500),
    remark TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_reconciliation_record_organization_id
    ON reconciliation_record (organization_id);

ALTER TABLE reconciliation_record
    DROP CONSTRAINT IF EXISTS fk_reconciliation_record_organization;
ALTER TABLE reconciliation_record
    ADD CONSTRAINT fk_reconciliation_record_organization
    FOREIGN KEY (organization_id) REFERENCES sys_organization(id) ON DELETE SET NULL;

DROP TRIGGER IF EXISTS trg_reconciliation_record_updated_at ON reconciliation_record;
CREATE TRIGGER trg_reconciliation_record_updated_at
    BEFORE UPDATE ON reconciliation_record
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

INSERT INTO sys_permission (perm_name, perm_code, parent_id, type, path, icon, sort_order)
SELECT '对账', 'bank:reconciliation', id, 'MENU', '/bank/reconciliation', 'AuditOutlined', 6
FROM sys_permission
WHERE perm_code = 'bank:manage'
ON CONFLICT (perm_code) DO NOTHING;

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
CROSS JOIN sys_permission p
WHERE r.role_code = 'ROLE_ADMIN'
  AND p.perm_code = 'bank:reconciliation'
ON CONFLICT (role_id, permission_id) DO NOTHING;
