-- V13: Add detailed ownership fields used by report generation and ownership form

ALTER TABLE ownership_info
    ADD COLUMN IF NOT EXISTS registered_address VARCHAR(500),
    ADD COLUMN IF NOT EXISTS registered_building_area DECIMAL(18, 4),
    ADD COLUMN IF NOT EXISTS right_status VARCHAR(100),
    ADD COLUMN IF NOT EXISTS right_registration_date DATE,
    ADD COLUMN IF NOT EXISTS right_cancellation_date DATE,
    ADD COLUMN IF NOT EXISTS property_source VARCHAR(200),
    ADD COLUMN IF NOT EXISTS land_use_right_source VARCHAR(200),
    ADD COLUMN IF NOT EXISTS land_use_start_date DATE,
    ADD COLUMN IF NOT EXISTS land_use_end_date DATE,
    ADD COLUMN IF NOT EXISTS mortgage_info TEXT,
    ADD COLUMN IF NOT EXISTS seizure_info TEXT,
    ADD COLUMN IF NOT EXISTS lease_restriction TEXT,
    ADD COLUMN IF NOT EXISTS other_rights_info TEXT,
    ADD COLUMN IF NOT EXISTS remark TEXT;
