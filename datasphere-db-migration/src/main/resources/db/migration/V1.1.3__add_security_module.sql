-- ============================================
-- DataSphere Next 数据库迁移脚本 V1.1.3
-- 增加数据安全管理扩展表
-- ============================================

-- 1. 脱敏规则表
CREATE TABLE IF NOT EXISTS ds_mask_rule (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    rule_name VARCHAR(128) NOT NULL COMMENT '规则名称',
    datasource_id VARCHAR(64) COMMENT '数据源ID',
    table_name VARCHAR(128) COMMENT '表名',
    column_name VARCHAR(128) COMMENT '字段名',
    algorithm VARCHAR(32) COMMENT '脱敏算法: MASK_ALL/MASK_PARTIAL/HASH/REPLACE/RANDOM/NULLIFY',
    algorithm_params TEXT COMMENT '算法参数(JSON)',
    description VARCHAR(512) COMMENT '规则描述',
    status INT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间',
    INDEX idx_datasource (datasource_id),
    INDEX idx_table_column (table_name, column_name),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='脱敏规则';

-- 2. 访问审计日志表
CREATE TABLE IF NOT EXISTS ds_access_log (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    user_id VARCHAR(64) COMMENT '用户ID',
    user_name VARCHAR(64) COMMENT '用户名',
    resource_type VARCHAR(32) COMMENT '资源类型: TABLE/API/REPORT',
    resource_id VARCHAR(64) COMMENT '资源ID',
    resource_name VARCHAR(128) COMMENT '资源名称',
    operation_type VARCHAR(32) COMMENT '操作类型: SELECT/INSERT/UPDATE/DELETE/EXPORT',
    operation_desc VARCHAR(256) COMMENT '操作描述',
    access_ip VARCHAR(64) COMMENT '访问IP',
    request_params TEXT COMMENT '请求参数',
    response_result TEXT COMMENT '响应结果',
    access_result INT DEFAULT 1 COMMENT '访问结果: 0-失败, 1-成功',
    error_message VARCHAR(512) COMMENT '错误信息',
    execution_time BIGINT COMMENT '执行时长(ms)',
    access_time DATETIME COMMENT '访问时间',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间',
    INDEX idx_user_id (user_id),
    INDEX idx_resource (resource_type, resource_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_access_time (access_time),
    INDEX idx_access_result (access_result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访问审计日志';

-- 3. 敏感字段识别表
CREATE TABLE IF NOT EXISTS ds_sensitive_field (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    datasource_id VARCHAR(64) NOT NULL COMMENT '数据源ID',
    table_name VARCHAR(128) NOT NULL COMMENT '表名',
    column_name VARCHAR(128) NOT NULL COMMENT '字段名',
    sensitive_type VARCHAR(32) COMMENT '敏感类型: PHONE/EMAIL/ID_CARD/NAME/ADDRESS/BANK_CARD/PASSWORD/SALARY/MEDICAL',
    sensitivity_level INT DEFAULT 3 COMMENT '敏感度等级: 1-低, 2-中, 3-高',
    is_confirmed INT DEFAULT 0 COMMENT '是否已确认: 0-待确认, 1-已确认',
    detect_method VARCHAR(32) COMMENT '识别方式: AUTO-自动, MANUAL-手动',
    confirm_by VARCHAR(64) COMMENT '确认人',
    confirm_time DATETIME COMMENT '确认时间',
    description VARCHAR(512) COMMENT '描述',
    status INT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间',
    INDEX idx_datasource (datasource_id),
    INDEX idx_table (table_name),
    INDEX idx_sensitive_type (sensitive_type),
    INDEX idx_is_confirmed (is_confirmed),
    UNIQUE KEY uk_field (datasource_id, table_name, column_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感字段识别';

-- 4. 初始化脱敏规则示例
INSERT INTO ds_mask_rule (id, rule_name, datasource_id, table_name, column_name, algorithm, algorithm_params, description, status, create_time) VALUES
('1', '手机号脱敏', NULL, NULL, 'phone', 'MASK_PARTIAL', '{"prefix":3,"suffix":4}', '手机号部分遮盖脱敏', 1, NOW()),
('2', '身份证脱敏', NULL, NULL, 'id_card', 'MASK_PARTIAL', '{"prefix":3,"suffix":4}', '身份证部分遮盖脱敏', 1, NOW()),
('3', '邮箱脱敏', NULL, NULL, 'email', 'MASK_PARTIAL', '{"prefix":2,"suffix":4}', '邮箱部分遮盖脱敏', 1, NOW()),
('4', '密码脱敏', NULL, NULL, 'password', 'NULLIFY', NULL, '密码置空脱敏', 1, NOW()),
('5', '姓名脱敏', NULL, NULL, 'name', 'MASK_PARTIAL', '{"prefix":1,"suffix":0}', '姓名部分遮盖脱敏', 1, NOW());
