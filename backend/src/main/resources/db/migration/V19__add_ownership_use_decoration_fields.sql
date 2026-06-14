-- V19: Add ownership fields shown on the project detail ownership tab

ALTER TABLE ownership_info
    ADD COLUMN IF NOT EXISTS actual_use VARCHAR(200),
    ADD COLUMN IF NOT EXISTS decoration VARCHAR(100);
