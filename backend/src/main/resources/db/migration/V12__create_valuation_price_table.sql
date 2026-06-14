-- V12: Create valuation_price table
CREATE TABLE IF NOT EXISTS valuation_price (
    id BIGSERIAL PRIMARY KEY,
    city VARCHAR(50),
    district VARCHAR(50),
    address VARCHAR(500),
    unit_price DECIMAL(12, 2),
    total_price DECIMAL(14, 2),
    area DECIMAL(14, 2),
    valuation_time DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
