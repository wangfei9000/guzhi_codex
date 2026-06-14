-- V5: Create report seal table

CREATE TABLE IF NOT EXISTS report_seal (
    id BIGSERIAL PRIMARY KEY,
    report_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    sealed_report_url VARCHAR(500),
    sealer VARCHAR(50),
    seal_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (report_id) REFERENCES valuation_report(id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);
DROP TRIGGER IF EXISTS trg_report_seal_updated_at ON report_seal;
CREATE TRIGGER trg_report_seal_updated_at
    BEFORE UPDATE ON report_seal
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
