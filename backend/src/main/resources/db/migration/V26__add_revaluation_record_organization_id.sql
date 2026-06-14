-- V26: Add organization id to revaluation records

ALTER TABLE revaluation_record
    ADD COLUMN IF NOT EXISTS organization_id BIGINT;

UPDATE revaluation_record rr
SET organization_id = matched.organization_id
FROM (
    SELECT rp.revaluation_id, MIN(o.id) AS organization_id
    FROM revaluation_project rp
    JOIN project p ON p.project_code = rp.project_code
    JOIN sys_organization o ON o.organization_name = p.client_name
    WHERE p.client_name IS NOT NULL
    GROUP BY rp.revaluation_id
) matched
WHERE rr.id = matched.revaluation_id
  AND rr.organization_id IS NULL;

CREATE INDEX IF NOT EXISTS idx_revaluation_record_organization_id
    ON revaluation_record (organization_id);

ALTER TABLE revaluation_record
    DROP CONSTRAINT IF EXISTS fk_revaluation_record_organization;
ALTER TABLE revaluation_record
    ADD CONSTRAINT fk_revaluation_record_organization
    FOREIGN KEY (organization_id) REFERENCES sys_organization(id) ON DELETE SET NULL;
