-- V14: Add client, mortgagor, and borrower fields to project

ALTER TABLE project
    ADD COLUMN IF NOT EXISTS client_name VARCHAR(200),
    ADD COLUMN IF NOT EXISTS mortgagor_name VARCHAR(200),
    ADD COLUMN IF NOT EXISTS mortgagor_id_card VARCHAR(50),
    ADD COLUMN IF NOT EXISTS mortgagor_phone VARCHAR(20),
    ADD COLUMN IF NOT EXISTS borrower_name VARCHAR(200),
    ADD COLUMN IF NOT EXISTS borrower_id_card VARCHAR(50);
