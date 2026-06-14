-- V7: Add short code and status to survey table

ALTER TABLE survey ADD COLUMN IF NOT EXISTS code VARCHAR(4);
ALTER TABLE survey ADD COLUMN IF NOT EXISTS survey_status VARCHAR(20) NOT NULL DEFAULT '未查勘';
