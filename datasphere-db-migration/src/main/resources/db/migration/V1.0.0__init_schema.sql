-- ============================================
-- DataSphere Next 数据库初始化脚本 V1.0.0
-- 创建所有核心表结构
-- ============================================

-- 1. 数据源管理模块表
-- ----------------------------

-- 数据源分类表
CREATE TABLE IF NOT EXISTS datasource_classify (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    classify_code VARCHAR(64) NOT NULL COMMENT '分类编码',
    classify_name VARCHAR(128) COMMENT '分类名称',
    order_no INT DEFAULT 0 COMMENT '序号',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识: 0-未删除, 1-已删除',
    delete_time DATETIME COMMENT '删除时间',
    UNIQUE KEY uk_classify_code (classify_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据源分类';

-- 数据源类型表
CREATE TABLE IF NOT EXISTS datasource_type (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    data_type VARCHAR(64) NOT NULL COMMENT '数据源类型编码',
    data_type_name VARCHAR(128) COMMENT '数据源类型名称',
    classify_code VARCHAR(64) COMMENT '分类编码',
    classify_id VARCHAR(64) COMMENT '分类ID',
    driver VARCHAR(256) COMMENT '驱动类名',
    url VARCHAR(512) COMMENT 'URL模板',
    img VARCHAR(256) COMMENT '图标',
    order_no INT DEFAULT 0 COMMENT '序号',
    status VARCHAR(32) DEFAULT 'ENABLE' COMMENT '状态: ENABLE/DISABLE',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间',
    UNIQUE KEY uk_data_type (data_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据源类型';

-- 数据源信息表
CREATE TABLE IF NOT EXISTS datasource_info (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    ds_name VARCHAR(128) NOT NULL COMMENT '数据源名称',
    ds_type VARCHAR(64) COMMENT '数据源类型',
    ds_config JSON COMMENT '数据源配置(JSON)',
    env_profile VARCHAR(64) COMMENT '环境配置',
    note VARCHAR(512) COMMENT '描述',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据源信息';

-- 2. 数据集成模块表
-- ----------------------------

-- 数据项目表
CREATE TABLE IF NOT EXISTS di_data_project (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    project_code VARCHAR(64) COMMENT '项目编码',
    project_name VARCHAR(128) NOT NULL COMMENT '项目名称',
    description VARCHAR(512) COMMENT '描述',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据项目';

-- 数据管道表
CREATE TABLE IF NOT EXISTS di_data_pipeline (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    pipeline_code VARCHAR(64) COMMENT '管道编码',
    pipeline_name VARCHAR(128) NOT NULL COMMENT '管道名称',
    project_id VARCHAR(64) COMMENT '项目ID',
    source_ds_id VARCHAR(64) COMMENT '源数据源ID',
    target_ds_id VARCHAR(64) COMMENT '目标数据源ID',
    engine_type VARCHAR(64) COMMENT '引擎类型: SeaTunnel/DataConnect',
    cron_expression VARCHAR(128) COMMENT 'Cron表达式',
    description VARCHAR(512) COMMENT '描述',
    status INT DEFAULT 0 COMMENT '状态: 0-草稿, 1-已发布, 2-运行中, 3-已停止',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据管道';

-- 管道连接器表
CREATE TABLE IF NOT EXISTS di_data_pipeline_connector (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    pipeline_id VARCHAR(64) COMMENT '管道ID',
    connector_type VARCHAR(32) COMMENT '连接器类型: SOURCE/SINK/TRANSFORM',
    connector_name VARCHAR(128) COMMENT '连接器名称',
    plugin_type VARCHAR(64) COMMENT '插件类型: Jdbc/Kafka/Console等',
    config JSON COMMENT '配置信息(JSON)',
    order_no INT DEFAULT 0 COMMENT '排序号',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管道连接器';

-- 数据作业日志表
CREATE TABLE IF NOT EXISTS di_data_job_log (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    job_id VARCHAR(64) COMMENT '作业ID',
    pipeline_id VARCHAR(64) COMMENT '管道ID',
    pipeline_name VARCHAR(128) COMMENT '管道名称',
    engine_type VARCHAR(64) COMMENT '引擎类型',
    start_time DATETIME COMMENT '启动时间',
    end_time DATETIME COMMENT '结束时间',
    read_rows BIGINT DEFAULT 0 COMMENT '读取行数',
    write_rows BIGINT DEFAULT 0 COMMENT '写入行数',
    status INT DEFAULT 0 COMMENT '状态: 0-运行中, 1-成功, 2-失败',
    error_msg TEXT COMMENT '错误信息',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据作业日志';

-- 3. 数据标准模块表
-- ----------------------------

-- 数据集表
CREATE TABLE IF NOT EXISTS dn_dataset (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    dataset_code VARCHAR(64) COMMENT '数据集编码',
    dataset_name VARCHAR(128) NOT NULL COMMENT '数据集名称',
    dataset_type VARCHAR(32) COMMENT '数据集类型: SQL/API',
    datasource_id VARCHAR(64) COMMENT '数据源ID',
    sql_content TEXT COMMENT 'SQL语句',
    description VARCHAR(512) COMMENT '描述',
    status INT DEFAULT 0 COMMENT '状态: 0-草稿, 1-已发布',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据集';

-- 数据元表
CREATE TABLE IF NOT EXISTS dn_data_element (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    element_code VARCHAR(64) COMMENT '数据元编码',
    element_name VARCHAR(128) NOT NULL COMMENT '数据元名称',
    definition VARCHAR(512) COMMENT '数据元定义',
    data_type VARCHAR(64) COMMENT '数据类型',
    data_length INT COMMENT '数据长度',
    allowable_values VARCHAR(512) COMMENT '允许值',
    source VARCHAR(256) COMMENT '标准来源',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据元';

-- 指标表
CREATE TABLE IF NOT EXISTS dn_indicator (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    indicator_code VARCHAR(64) COMMENT '指标编码',
    indicator_name VARCHAR(128) NOT NULL COMMENT '指标名称',
    definition VARCHAR(512) COMMENT '指标定义',
    caliber VARCHAR(512) COMMENT '计算口径',
    stat_period VARCHAR(64) COMMENT '统计周期',
    datasource_id VARCHAR(64) COMMENT '数据源ID',
    indicator_sql TEXT COMMENT '指标SQL',
    unit VARCHAR(64) COMMENT '单位',
    status INT DEFAULT 0 COMMENT '状态: 0-草稿, 1-已发布',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标';

-- OID对象标识符表
CREATE TABLE IF NOT EXISTS dn_oid (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    oid_code VARCHAR(64) COMMENT 'OID编码',
    oid_name VARCHAR(128) NOT NULL COMMENT 'OID名称',
    oid_value VARCHAR(256) COMMENT 'OID值',
    parent_oid VARCHAR(256) COMMENT '父OID',
    description VARCHAR(512) COMMENT 'OID描述',
    source VARCHAR(256) COMMENT '标准来源',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OID对象标识符';

-- 4. 主数据模块表
-- ----------------------------

-- 组织机构表
CREATE TABLE IF NOT EXISTS md_organization (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    org_code VARCHAR(64) COMMENT '机构编码',
    org_name VARCHAR(128) NOT NULL COMMENT '机构名称',
    parent_id VARCHAR(64) COMMENT '上级机构ID',
    org_level INT COMMENT '机构层级',
    org_type VARCHAR(64) COMMENT '机构类型',
    address VARCHAR(256) COMMENT '机构地址',
    phone VARCHAR(64) COMMENT '联系电话',
    sort_no INT DEFAULT 0 COMMENT '排序号',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织机构';

-- 科室表
CREATE TABLE IF NOT EXISTS md_department (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    dept_code VARCHAR(64) COMMENT '科室编码',
    dept_name VARCHAR(128) NOT NULL COMMENT '科室名称',
    parent_id VARCHAR(64) COMMENT '上级科室ID',
    org_id VARCHAR(64) COMMENT '所属机构ID',
    dept_type VARCHAR(64) COMMENT '科室类型',
    category VARCHAR(64) COMMENT '科室分类',
    leader_id VARCHAR(64) COMMENT '负责人ID',
    sort_no INT DEFAULT 0 COMMENT '排序号',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室';

-- 人员信息表
CREATE TABLE IF NOT EXISTS md_person (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    person_code VARCHAR(64) COMMENT '人员编码',
    name VARCHAR(64) NOT NULL COMMENT '姓名',
    gender VARCHAR(16) COMMENT '性别',
    birth_date DATE COMMENT '出生日期',
    id_card VARCHAR(64) COMMENT '身份证号',
    mobile VARCHAR(64) COMMENT '手机号',
    email VARCHAR(128) COMMENT '邮箱',
    org_id VARCHAR(64) COMMENT '机构ID',
    dept_id VARCHAR(64) COMMENT '部门ID',
    position VARCHAR(128) COMMENT '职务',
    job_title VARCHAR(128) COMMENT '职称',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员信息';

-- 标准字典表
CREATE TABLE IF NOT EXISTS md_dictionary (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    dict_code VARCHAR(64) NOT NULL COMMENT '字典编码',
    dict_name VARCHAR(128) COMMENT '字典名称',
    dict_type VARCHAR(64) COMMENT '字典类型',
    description VARCHAR(512) COMMENT '描述',
    status INT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标准字典';

-- 5. 元数据模块表
-- ----------------------------

-- 元模型表
CREATE TABLE IF NOT EXISTS meta_model (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    model_code VARCHAR(64) COMMENT '模型编码',
    model_name VARCHAR(128) NOT NULL COMMENT '模型名称',
    model_type VARCHAR(32) COMMENT '模型类型: TABLE/COLUMN/OTHER',
    parent_id VARCHAR(64) COMMENT '父模型ID',
    description VARCHAR(512) COMMENT '描述',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='元模型';

-- 元数据项表
CREATE TABLE IF NOT EXISTS meta_item (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    item_code VARCHAR(64) COMMENT '元数据编码',
    item_name VARCHAR(128) NOT NULL COMMENT '元数据名称',
    model_id VARCHAR(64) COMMENT '模型ID',
    datasource_id VARCHAR(64) COMMENT '数据源ID',
    database_name VARCHAR(128) COMMENT '数据库名',
    table_name VARCHAR(128) COMMENT '表名',
    column_name VARCHAR(128) COMMENT '列名',
    data_type VARCHAR(64) COMMENT '数据类型',
    description VARCHAR(512) COMMENT '描述',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='元数据项';

-- 6. 数据质量模块表
-- ----------------------------

-- 质量规则表
CREATE TABLE IF NOT EXISTS dq_rule (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    rule_code VARCHAR(64) COMMENT '规则编码',
    rule_name VARCHAR(128) NOT NULL COMMENT '规则名称',
    rule_type VARCHAR(64) COMMENT '规则类型: COMPLETENESS/UNIQUENESS/FORMAT/VALUE_RANGE/CONSISTENCY/ACCURACY/CUSTOM',
    datasource_id VARCHAR(64) COMMENT '数据源ID',
    table_name VARCHAR(128) COMMENT '表名',
    column_name VARCHAR(128) COMMENT '字段名',
    rule_expression TEXT COMMENT '规则表达式/SQL',
    error_message VARCHAR(512) COMMENT '错误提示',
    description VARCHAR(512) COMMENT '规则描述',
    status INT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质量规则';

-- 质量检测任务表
CREATE TABLE IF NOT EXISTS dq_task (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    task_name VARCHAR(128) COMMENT '任务名称',
    rule_id VARCHAR(64) COMMENT '规则ID',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    total_rows BIGINT DEFAULT 0 COMMENT '检测总行数',
    error_rows BIGINT DEFAULT 0 COMMENT '错误行数',
    status INT DEFAULT 0 COMMENT '状态: 0-运行中, 1-成功, 2-失败',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质量检测任务';

-- 7. 数据资产模块表
-- ----------------------------

-- 数据资产表
CREATE TABLE IF NOT EXISTS da_asset (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    asset_code VARCHAR(64) COMMENT '资产编码',
    asset_name VARCHAR(128) NOT NULL COMMENT '资产名称',
    asset_type VARCHAR(32) COMMENT '资产类型: TABLE/API/REPORT',
    datasource_id VARCHAR(64) COMMENT '数据源ID',
    database_name VARCHAR(128) COMMENT '数据库名',
    table_name VARCHAR(128) COMMENT '表名',
    description VARCHAR(512) COMMENT '资产描述',
    owner VARCHAR(64) COMMENT '负责人',
    access_count BIGINT DEFAULT 0 COMMENT '访问次数',
    status INT DEFAULT 0 COMMENT '状态: 0-草稿, 1-已发布, 2-已归档',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据资产';

-- 8. 数据安全模块表
-- ----------------------------

-- 脱敏规则表
CREATE TABLE IF NOT EXISTS ds_mask_rule (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    rule_name VARCHAR(128) NOT NULL COMMENT '规则名称',
    datasource_id VARCHAR(64) COMMENT '数据源ID',
    table_name VARCHAR(128) COMMENT '表名',
    column_name VARCHAR(128) COMMENT '字段名',
    algorithm VARCHAR(64) COMMENT '脱敏算法: MASK_ALL/MASK_PARTIAL/HASH/REPLACE/RANDOM/NULLIFY',
    algorithm_params VARCHAR(512) COMMENT '算法参数(JSON)',
    description VARCHAR(512) COMMENT '规则描述',
    status INT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='脱敏规则';
