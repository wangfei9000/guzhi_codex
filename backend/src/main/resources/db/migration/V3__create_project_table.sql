-- V3: Create project table

CREATE TABLE IF NOT EXISTS project (
    id BIGSERIAL PRIMARY KEY,
    project_code VARCHAR(50) NOT NULL UNIQUE,
    project_name VARCHAR(200) NOT NULL,
    city VARCHAR(50),
    district VARCHAR(50),
    area VARCHAR(100),
    address VARCHAR(255),
    registrar VARCHAR(50),
    registration_date DATE,
    client_contact VARCHAR(50),
    client_phone VARCHAR(20),
    valuation_purpose VARCHAR(100),
    valuation_time DATE,
    expected_price DECIMAL(14, 2),
    status VARCHAR(20) NOT NULL DEFAULT '未评估',
    remark TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP TRIGGER IF EXISTS trg_project_updated_at ON project;
CREATE TRIGGER trg_project_updated_at
    BEFORE UPDATE ON project
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
