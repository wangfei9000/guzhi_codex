-- V10: Redesign ownership_info table with all required fields
-- Each project has a single ownership info record (1:1 relationship)

-- Drop old trigger
DROP TRIGGER IF EXISTS trg_ownership_info_updated_at ON ownership_info;

-- Add new columns (existing records will have NULL values)
ALTER TABLE ownership_info
    ADD COLUMN IF NOT EXISTS right_holder VARCHAR(200),
    ADD COLUMN IF NOT EXISTS right_certificate_number VARCHAR(200),
    ADD COLUMN IF NOT EXISTS borrower_name VARCHAR(100),
    ADD COLUMN IF NOT EXISTS borrower_id_card VARCHAR(50),
    ADD COLUMN IF NOT EXISTS building_structure VARCHAR(100),
    ADD COLUMN IF NOT EXISTS usage VARCHAR(200),
    ADD COLUMN IF NOT EXISTS current_floor VARCHAR(50),
    ADD COLUMN IF NOT EXISTS total_floors INT,
    ADD COLUMN IF NOT EXISTS right_nature VARCHAR(200),
    ADD COLUMN IF NOT EXISTS right_type VARCHAR(200),
    ADD COLUMN IF NOT EXISTS co_ownership VARCHAR(200),
    ADD COLUMN IF NOT EXISTS land_use_years INT,
    ADD COLUMN IF NOT EXISTS property_unit_number VARCHAR(200),
    ADD COLUMN IF NOT EXISTS shared_land_area DECIMAL(18, 4),
    ADD COLUMN IF NOT EXISTS allocated_land_area DECIMAL(18, 4),
    ADD COLUMN IF NOT EXISTS build_year INT,
    ADD COLUMN IF NOT EXISTS build_year_source VARCHAR(200),
    ADD COLUMN IF NOT EXISTS online_signing_date DATE,
    ADD COLUMN IF NOT EXISTS contract_number VARCHAR(200),
    ADD COLUMN IF NOT EXISTS report_issue_date DATE,
    ADD COLUMN IF NOT EXISTS valuation_time_point DATE,
    ADD COLUMN IF NOT EXISTS old_community_renovation BOOLEAN,
    ADD COLUMN IF NOT EXISTS area_prosperity VARCHAR(200),
    ADD COLUMN IF NOT EXISTS market_prosperity VARCHAR(200),
    ADD COLUMN IF NOT EXISTS house_ownership_certificate VARCHAR(200),
    ADD COLUMN IF NOT EXISTS state_land_use_certificate_number VARCHAR(200),
    ADD COLUMN IF NOT EXISTS land_use VARCHAR(200),
    ADD COLUMN IF NOT EXISTS qiu_quan_number VARCHAR(200),
    ADD COLUMN IF NOT EXISTS land_use_area DECIMAL(18, 4);

-- Drop old columns
ALTER TABLE ownership_info
    DROP COLUMN IF EXISTS ownership_name,
    DROP COLUMN IF EXISTS ownership_value;

-- Add unique constraint on project_id (1:1 relationship)
ALTER TABLE ownership_info
    ADD CONSTRAINT IF NOT EXISTS uk_ownership_info_project_id UNIQUE (project_id);

-- Recreate trigger
CREATE TRIGGER trg_ownership_info_updated_at
    BEFORE UPDATE ON ownership_info
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
