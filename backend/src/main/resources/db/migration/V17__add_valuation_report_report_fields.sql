-- V17: Add report-level valuation conclusion fields
ALTER TABLE valuation_report
    ADD COLUMN IF NOT EXISTS total_price DECIMAL(14, 2),
    ADD COLUMN IF NOT EXISTS mortgage_value DECIMAL(14, 2),
    ADD COLUMN IF NOT EXISTS priority_compensation_amount DECIMAL(14, 2),
    ADD COLUMN IF NOT EXISTS priority_compensation_description TEXT,
    ADD COLUMN IF NOT EXISTS value_date DATE,
    ADD COLUMN IF NOT EXISTS report_issue_date DATE,
    ADD COLUMN IF NOT EXISTS valid_start_date DATE,
    ADD COLUMN IF NOT EXISTS valid_end_date DATE,
    ADD COLUMN IF NOT EXISTS valuer1_name VARCHAR(50),
    ADD COLUMN IF NOT EXISTS valuer1_cert_no VARCHAR(50),
    ADD COLUMN IF NOT EXISTS valuer2_name VARCHAR(50),
    ADD COLUMN IF NOT EXISTS valuer2_cert_no VARCHAR(50);
