-- ============================================
-- DataSphere Next 数据库迁移脚本 V1.1.2
-- 增加数据资产管理扩展表
-- ============================================

-- 1. 资产分类表
CREATE TABLE IF NOT EXISTS da_asset_category (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    category_code VARCHAR(64) COMMENT '分类编码',
    category_name VARCHAR(128) NOT NULL COMMENT '分类名称',
    parent_id VARCHAR(64) DEFAULT '0' COMMENT '父分类ID',
    path VARCHAR(512) COMMENT '层级路径',
    level INT DEFAULT 1 COMMENT '层级深度',
    sort_no INT DEFAULT 0 COMMENT '排序号',
    description VARCHAR(512) COMMENT '分类描述',
    status INT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间',
    INDEX idx_parent_id (parent_id),
    INDEX idx_path (path),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产分类';

-- 2. 资产标签表
CREATE TABLE IF NOT EXISTS da_asset_tag (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    tag_name VARCHAR(64) NOT NULL COMMENT '标签名称',
    tag_color VARCHAR(16) DEFAULT '#1890ff' COMMENT '标签颜色',
    description VARCHAR(256) COMMENT '标签描述',
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    status INT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间',
    UNIQUE KEY uk_tag_name (tag_name),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产标签';

-- 3. 数据血缘表
CREATE TABLE IF NOT EXISTS da_lineage (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    asset_id VARCHAR(64) NOT NULL COMMENT '资产ID',
    asset_type VARCHAR(32) COMMENT '资产类型: TABLE/FIELD',
    asset_name VARCHAR(128) COMMENT '资产名称',
    upstream_asset_id VARCHAR(64) COMMENT '上游资产ID',
    upstream_asset_name VARCHAR(128) COMMENT '上游资产名称',
    downstream_asset_id VARCHAR(64) COMMENT '下游资产ID',
    downstream_asset_name VARCHAR(128) COMMENT '下游资产名称',
    relation_type VARCHAR(32) COMMENT '血缘关系类型: LINEAGE/DEPENDENCY/IMPACT',
    relation_desc VARCHAR(256) COMMENT '关系描述',
    transform_logic TEXT COMMENT '转换逻辑/SQL',
    last_parse_time DATETIME COMMENT '最后解析时间',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间',
    INDEX idx_asset_id (asset_id),
    INDEX idx_upstream (upstream_asset_id),
    INDEX idx_downstream (downstream_asset_id),
    INDEX idx_relation_type (relation_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据血缘';

-- 4. 资产使用记录表
CREATE TABLE IF NOT EXISTS da_asset_usage (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    asset_id VARCHAR(64) NOT NULL COMMENT '资产ID',
    user_id VARCHAR(64) COMMENT '用户ID',
    user_name VARCHAR(64) COMMENT '用户名',
    operation_type VARCHAR(32) COMMENT '操作类型: VIEW/DOWNLOAD/API_CALL',
    operation_desc VARCHAR(256) COMMENT '操作描述',
    access_ip VARCHAR(64) COMMENT '访问IP',
    request_params TEXT COMMENT '请求参数',
    response_result TEXT COMMENT '响应结果',
    execution_time BIGINT COMMENT '执行时长(ms)',
    access_time DATETIME COMMENT '访问时间',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间',
    INDEX idx_asset_id (asset_id),
    INDEX idx_user_id (user_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_access_time (access_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产使用记录';

-- 5. 初始化资产分类数据
INSERT INTO da_asset_category (id, category_code, category_name, parent_id, path, level, sort_no, description, status, create_time) VALUES
('1', 'ROOT', '根分类', '0', '/1', 1, 1, '资产根分类', 1, NOW()),
('2', 'TABLE', '数据表', '1', '/1/2', 2, 1, '数据库表资产', 1, NOW()),
('3', 'API', 'API接口', '1', '/1/3', 2, 2, 'API接口资产', 1, NOW()),
('4', 'REPORT', '数据报表', '1', '/1/4', 2, 3, '数据报表资产', 1, NOW()),
('5', 'BUSINESS', '业务数据', '2', '/1/2/5', 3, 1, '业务数据表', 1, NOW()),
('6', 'MASTER', '主数据', '2', '/1/2/6', 3, 2, '主数据表', 1, NOW());

-- 6. 初始化常用标签
INSERT INTO da_asset_tag (id, tag_name, tag_color, description, status, create_time) VALUES
('1', '核心业务', '#f5222d', '核心业务数据', 1, NOW()),
('2', '敏感数据', '#faad14', '包含敏感信息的数据', 1, NOW()),
('3', '公开数据', '#52c41a', '可公开访问的数据', 1, NOW()),
('4', '内部使用', '#1890ff', '仅限内部使用的数据', 1, NOW()),
('5', '高频访问', '#722ed1', '访问频次较高的数据', 1, NOW());
