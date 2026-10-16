-- ========================================
-- 企业工单系统 数据库初始化脚本
-- 数据库：qiyegongdan
-- ========================================
CREATE DATABASE IF NOT EXISTS qiyegongdan DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
USE qiyegongdan;

-- ---------- 用户与权限 ----------
DROP TABLE IF EXISTS role_permission;
DROP TABLE IF EXISTS permissions;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT 'BCrypt密码',
    name        VARCHAR(50)  COMMENT '姓名',
    email       VARCHAR(100) COMMENT '邮箱',
    phone       VARCHAR(20)  COMMENT '手机号',
    avatar      VARCHAR(255) COMMENT '头像',
    role_code   VARCHAR(20)  NOT NULL DEFAULT 'CUSTOMER' COMMENT '角色编码',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
    created_at  DATETIME,
    updated_at  DATETIME,
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE roles (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_code  VARCHAR(20) NOT NULL UNIQUE,
    role_name  VARCHAR(50) NOT NULL,
    remark     VARCHAR(200)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE permissions (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    perm_code  VARCHAR(50) NOT NULL UNIQUE,
    perm_name  VARCHAR(50) NOT NULL,
    remark     VARCHAR(200)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE role_permission (
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------- 工单类型与自定义字段 ----------
DROP TABLE IF EXISTS ticket_field_value;
DROP TABLE IF EXISTS custom_field;
DROP TABLE IF EXISTS ticket_type;

CREATE TABLE ticket_type (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(50) NOT NULL,
    code        VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(500),
    group_id    BIGINT COMMENT '负责客服组id',
    status      TINYINT NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE custom_field (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    ticket_type_id BIGINT NOT NULL,
    field_name     VARCHAR(50) NOT NULL,
    field_key      VARCHAR(50) NOT NULL,
    field_type     VARCHAR(20) NOT NULL COMMENT 'text/number/select/date/textarea',
    options_json   TEXT COMMENT 'select选项json',
    required       TINYINT NOT NULL DEFAULT 0,
    sort           INT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------- 工单核心 ----------
DROP TABLE IF EXISTS ticket_assign;
DROP TABLE IF EXISTS ticket_status_log;
DROP TABLE IF EXISTS ticket_attachment;
DROP TABLE IF EXISTS ticket_comment;
DROP TABLE IF EXISTS ticket;

CREATE TABLE ticket (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    ticket_no     VARCHAR(30) NOT NULL UNIQUE,
    title         VARCHAR(200) NOT NULL,
    description   TEXT,
    priority      VARCHAR(10) NOT NULL DEFAULT 'MEDIUM' COMMENT 'LOW/MEDIUM/HIGH/URGENT',
    status        VARCHAR(15) NOT NULL DEFAULT 'NEW' COMMENT 'NEW/ASSIGNED/PROCESSING/RESOLVED/CLOSED/REJECTED',
    type_id       BIGINT,
    creator_id    BIGINT,
    assignee_id   BIGINT,
    group_id      BIGINT,
    created_at    DATETIME,
    updated_at    DATETIME,
    handled_at    DATETIME,
    resolved_at   DATETIME,
    closed_at     DATETIME,
    resolve_remark VARCHAR(500),
    deleted       TINYINT NOT NULL DEFAULT 0,
    INDEX idx_status (status),
    INDEX idx_type (type_id),
    INDEX idx_assignee (assignee_id),
    INDEX idx_creator (creator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ticket_field_value (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    ticket_id   BIGINT NOT NULL,
    field_id    BIGINT NOT NULL,
    field_key   VARCHAR(50),
    field_value VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ticket_comment (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    ticket_id   BIGINT NOT NULL,
    user_id     BIGINT,
    content     VARCHAR(2000),
    is_internal TINYINT NOT NULL DEFAULT 0,
    created_at  DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ticket_attachment (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    ticket_id   BIGINT NOT NULL,
    uploader_id BIGINT,
    file_name   VARCHAR(200),
    file_path   VARCHAR(500),
    file_size   BIGINT,
    file_type   VARCHAR(20),
    created_at  DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ticket_status_log (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    ticket_id   BIGINT NOT NULL,
    from_status VARCHAR(15),
    to_status   VARCHAR(15),
    operator_id BIGINT,
    remark      VARCHAR(500),
    created_at  DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ticket_assign (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    ticket_id   BIGINT NOT NULL,
    operator_id BIGINT,
    assignee_id BIGINT,
    mode        VARCHAR(10) COMMENT 'AUTO/MANUAL',
    remark      VARCHAR(500),
    created_at  DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------- 操作日志 ----------
DROP TABLE IF EXISTS operation_log;

CREATE TABLE operation_log (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT,
    username   VARCHAR(50),
    module     VARCHAR(50),
    action     VARCHAR(50),
    detail     VARCHAR(500),
    ip         VARCHAR(50),
    created_at DATETIME,
    INDEX idx_module (module)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================
-- 初始化数据
-- ========================================
-- 角色/权限
INSERT INTO roles (role_code, role_name, remark) VALUES
('ADMIN', '管理员', '系统管理，全权限'),
('AGENT', '客服', '处理工单'),
('CUSTOMER', '普通用户', '提交工单');

INSERT INTO permissions (perm_code, perm_name, remark) VALUES
('user:manage', '用户管理', '用户增删改查'),
('role:manage', '角色权限管理', '角色权限配置'),
('ticket:create', '创建工单', ''),
('ticket:process', '处理工单', ''),
('ticket:assign', '分配工单', ''),
('ticket:view', '查看工单', ''),
('statistics:view', '查看统计', ''),
('log:view', '查看日志', '');

-- 用户数据由后端启动时的 DataInitializer 自动创建（默认密码 123456）

-- 角色权限
INSERT INTO role_permission (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8),
(2, 3), (2, 4), (2, 5), (2, 6), (2, 7),
(3, 3), (3, 6);

-- 工单类型
INSERT INTO ticket_type (name, code, description, group_id, status) VALUES
('故障报修', 'FAULT', '设备或功能故障', NULL, 1),
('咨询答疑', 'CONSULT', '功能使用咨询', NULL, 1),
('功能建议', 'SUGGEST', '产品功能建议', NULL, 1);

-- 自定义字段示例（故障报修）
INSERT INTO custom_field (ticket_type_id, field_name, field_key, field_type, options_json, required, sort) VALUES
(1, '故障设备', 'device', 'text', NULL, 1, 1),
(1, '紧急程度', 'urgency', 'select', '["紧急","一般","可等待"]', 1, 2),
(1, '故障时间', 'fault_time', 'date', NULL, 0, 3),
(1, '详细描述', 'detail', 'textarea', NULL, 0, 4);