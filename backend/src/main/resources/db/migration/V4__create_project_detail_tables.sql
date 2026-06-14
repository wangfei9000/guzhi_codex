-- V4: Create project detail tables

-- 抵押物表
CREATE TABLE IF NOT EXISTS collateral (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    collateral_code VARCHAR(50) NOT NULL UNIQUE,
    collateral_type VARCHAR(50),
    collateral_name VARCHAR(200),
    collateral_address VARCHAR(255),
    building_area DECIMAL(14, 2),
    land_area DECIMAL(14, 2),
    community_name VARCHAR(100),
    building VARCHAR(50),
    unit_name VARCHAR(50),
    door_number VARCHAR(50),
    build_year INT,
    construction_land DECIMAL(14, 2),
    land_acquisition DECIMAL(14, 2),
    floor_area_ratio DECIMAL(10, 4),
    above_ground_ratio DECIMAL(10, 4),
    civil_defense_area DECIMAL(14, 2),
    underground_ratio DECIMAL(10, 4),
    greening_rate DECIMAL(10, 4),
    building_density DECIMAL(10, 4),
    building_height DECIMAL(10, 2),
    floor_count INT,
    household_count INT,
    parking_count INT,
    parking_ratio DECIMAL(10, 4),
    completion_date DATE,
    property_rights_years INT,
    land_use_years INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);
DROP TRIGGER IF EXISTS trg_collateral_updated_at ON collateral;
CREATE TRIGGER trg_collateral_updated_at
    BEFORE UPDATE ON collateral
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 估价报告表
CREATE TABLE IF NOT EXISTS valuation_report (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    report_code VARCHAR(50) NOT NULL UNIQUE,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    unit_price DECIMAL(12, 2),
    valuation_result TEXT,
    area_evaluation TEXT,
    surrounding_transactions TEXT,
    liquidity_analysis TEXT,
    floor_plan TEXT,
    land_grant_deduction DECIMAL(14, 2),
    decoration_new_rate DECIMAL(5, 4),
    equipment_new_rate DECIMAL(5, 4),
    report_url VARCHAR(500),
    bank_suggestion TEXT,
    land_plot TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);
DROP TRIGGER IF EXISTS trg_valuation_report_updated_at ON valuation_report;
CREATE TRIGGER trg_valuation_report_updated_at
    BEFORE UPDATE ON valuation_report
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 估价方法表
CREATE TABLE IF NOT EXISTS valuation_method (
    id BIGSERIAL PRIMARY KEY,
    method_code VARCHAR(50) NOT NULL UNIQUE,
    method_name VARCHAR(100) NOT NULL,
    weight DECIMAL(5, 4),
    unit_price DECIMAL(12, 2),
    appraiser_signature VARCHAR(50),
    description TEXT,
    report_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (report_id) REFERENCES valuation_report(id) ON DELETE CASCADE
);
DROP TRIGGER IF EXISTS trg_valuation_method_updated_at ON valuation_method;
CREATE TRIGGER trg_valuation_method_updated_at
    BEFORE UPDATE ON valuation_method
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 勘查表
CREATE TABLE IF NOT EXISTS survey (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    survey_code VARCHAR(50) NOT NULL UNIQUE,
    surveyor VARCHAR(50),
    receptionist VARCHAR(50),
    receptionist_phone VARCHAR(20),
    survey_date DATE,
    start_time TIME,
    end_time TIME,
    property_cert_verified BOOLEAN,
    ownership_dispute TEXT,
    remark TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);
DROP TRIGGER IF EXISTS trg_survey_updated_at ON survey;
CREATE TRIGGER trg_survey_updated_at
    BEFORE UPDATE ON survey
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 勘查照片表
CREATE TABLE IF NOT EXISTS survey_photo (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    survey_id BIGINT NOT NULL,
    photo_code VARCHAR(50) NOT NULL UNIQUE,
    photo_path VARCHAR(500),
    photo_description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    FOREIGN KEY (survey_id) REFERENCES survey(id) ON DELETE CASCADE
);

-- 权属信息表
CREATE TABLE IF NOT EXISTS ownership_info (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    ownership_name VARCHAR(100) NOT NULL,
    ownership_value TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);
DROP TRIGGER IF EXISTS trg_ownership_info_updated_at ON ownership_info;
CREATE TRIGGER trg_ownership_info_updated_at
    BEFORE UPDATE ON ownership_info
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 报告审核表
CREATE TABLE IF NOT EXISTS report_review (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    report_id BIGINT NOT NULL,
    reviewer VARCHAR(50),
    review_date DATE,
    review_opinion TEXT,
    review_result VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    FOREIGN KEY (report_id) REFERENCES valuation_report(id) ON DELETE CASCADE
);
DROP TRIGGER IF EXISTS trg_report_review_updated_at ON report_review;
CREATE TRIGGER trg_report_review_updated_at
    BEFORE UPDATE ON report_review
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
