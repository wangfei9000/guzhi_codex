-- V16: Add structured photo category to survey_photo
ALTER TABLE survey_photo
    ADD COLUMN IF NOT EXISTS photo_category VARCHAR(50);

CREATE INDEX IF NOT EXISTS idx_survey_photo_project_category
    ON survey_photo(project_id, photo_category);
