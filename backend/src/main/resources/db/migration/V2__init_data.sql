-- V2: Insert default data
-- Default admin password: admin123 (BCrypt encoded)
INSERT INTO sys_user (username, password, email, nickname, status) VALUES
('admin', '$2b$10$mvgRIIDqsSJQxF5UG1arBeVQmNTWKs7iYDJHLuEu6HN8C5FSd65ti', 'admin@example.com', '超级管理员', 1);

INSERT INTO sys_role (role_name, role_code, description) VALUES
('超级管理员', 'ROLE_ADMIN', '拥有所有权限'),
('普通用户', 'ROLE_USER', '基础权限');

INSERT INTO sys_permission (perm_name, perm_code, parent_id, type, path, icon, sort_order) VALUES
('系统管理', 'system:manage', NULL, 'MENU', '/system', 'SettingOutlined', 1),
('用户管理', 'user:list', 1, 'MENU', '/system/user', 'UserOutlined', 1),
('用户创建', 'user:create', 2, 'BUTTON', NULL, NULL, 1),
('用户编辑', 'user:update', 2, 'BUTTON', NULL, NULL, 2),
('用户删除', 'user:delete', 2, 'BUTTON', NULL, NULL, 3),
('角色管理', 'role:list', 1, 'MENU', '/system/role', 'TeamOutlined', 2),
('角色创建', 'role:create', 6, 'BUTTON', NULL, NULL, 1),
('角色编辑', 'role:update', 6, 'BUTTON', NULL, NULL, 2),
('角色删除', 'role:delete', 6, 'BUTTON', NULL, NULL, 3),
('权限管理', 'perm:list', 1, 'MENU', '/system/permission', 'SafetyOutlined', 3),
('权限创建', 'perm:create', 10, 'BUTTON', NULL, NULL, 1),
('权限编辑', 'perm:update', 10, 'BUTTON', NULL, NULL, 2),
('权限删除', 'perm:delete', 10, 'BUTTON', NULL, NULL, 3),
('文件管理', 'file:manage', NULL, 'MENU', '/file', 'FileOutlined', 2),
('文件上传', 'file:upload', 14, 'BUTTON', NULL, NULL, 1),
('通知中心', 'notification:view', NULL, 'MENU', '/notification', 'BellOutlined', 3);

-- Assign all permissions to admin role
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- Assign basic permissions to user role
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 2, id FROM sys_permission WHERE perm_code IN ('file:manage', 'file:upload', 'notification:view');

-- Assign admin role to admin user
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);
