-- V1: Create core tables

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    nickname VARCHAR(50),
    status INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
DROP TRIGGER IF EXISTS trg_sys_user_updated_at ON sys_user;
CREATE TRIGGER trg_sys_user_updated_at
    BEFORE UPDATE ON sys_user
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
DROP TRIGGER IF EXISTS trg_sys_role_updated_at ON sys_role;
CREATE TRIGGER trg_sys_role_updated_at
    BEFORE UPDATE ON sys_role
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGSERIAL PRIMARY KEY,
    perm_name VARCHAR(50) NOT NULL,
    perm_code VARCHAR(100) NOT NULL UNIQUE,
    parent_id BIGINT DEFAULT NULL,
    type VARCHAR(20) NOT NULL,
    path VARCHAR(200),
    icon VARCHAR(50),
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
DROP TRIGGER IF EXISTS trg_sys_permission_updated_at ON sys_permission;
CREATE TRIGGER trg_sys_permission_updated_at
    BEFORE UPDATE ON sys_permission
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT uk_user_role UNIQUE (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    CONSTRAINT uk_role_perm UNIQUE (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES sys_permission(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sys_notification (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
DROP TRIGGER IF EXISTS trg_sys_notification_updated_at ON sys_notification;
CREATE TRIGGER trg_sys_notification_updated_at
    BEFORE UPDATE ON sys_notification
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TABLE IF NOT EXISTS sys_file_record (
    id BIGSERIAL PRIMARY KEY,
    original_name VARCHAR(255) NOT NULL,
    stored_name VARCHAR(255) NOT NULL UNIQUE,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    content_type VARCHAR(100),
    upload_user_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
DROP TRIGGER IF EXISTS trg_sys_file_record_updated_at ON sys_file_record;
CREATE TRIGGER trg_sys_file_record_updated_at
    BEFORE UPDATE ON sys_file_record
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
