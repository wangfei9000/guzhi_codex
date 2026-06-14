-- V11: Add valuation fields to project table
ALTER TABLE project
    ADD COLUMN IF NOT EXISTS valuation_unit_price DECIMAL(12, 2),
    ADD COLUMN IF NOT EXISTS valuation_total_price DECIMAL(14, 2),
    ADD COLUMN IF NOT EXISTS building_area DECIMAL(14, 2);
