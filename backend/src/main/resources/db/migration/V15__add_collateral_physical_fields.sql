-- V15: Add physical-condition fields to collateral

ALTER TABLE collateral
    ADD COLUMN IF NOT EXISTS is_primary BOOLEAN,
    ADD COLUMN IF NOT EXISTS actual_use VARCHAR(100),
    ADD COLUMN IF NOT EXISTS occupancy_status VARCHAR(100),
    ADD COLUMN IF NOT EXISTS decoration VARCHAR(100),
    ADD COLUMN IF NOT EXISTS orientation VARCHAR(50),
    ADD COLUMN IF NOT EXISTS current_floor VARCHAR(50),
    ADD COLUMN IF NOT EXISTS indoor_height VARCHAR(50),
    ADD COLUMN IF NOT EXISTS space_layout TEXT,
    ADD COLUMN IF NOT EXISTS facilities_condition TEXT,
    ADD COLUMN IF NOT EXISTS maintenance_condition TEXT,
    ADD COLUMN IF NOT EXISTS parcel_shape TEXT,
    ADD COLUMN IF NOT EXISTS terrain TEXT,
    ADD COLUMN IF NOT EXISTS land_level TEXT,
    ADD COLUMN IF NOT EXISTS soil_condition TEXT,
    ADD COLUMN IF NOT EXISTS land_development_level TEXT,
    ADD COLUMN IF NOT EXISTS landscape TEXT,
    ADD COLUMN IF NOT EXISTS surrounding_environment TEXT;
