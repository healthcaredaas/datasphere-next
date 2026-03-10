-- ============================================
-- DataSphere Next 数据库迁移脚本 V1.1.0
-- 增加数据集成作业相关表
-- ============================================

-- 1. Connector类型表
CREATE TABLE IF NOT EXISTS di_connector_type (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    connector_code VARCHAR(64) NOT NULL COMMENT 'Connector编码',
    connector_name VARCHAR(128) COMMENT 'Connector名称',
    connector_type VARCHAR(32) COMMENT 'Connector类型: SOURCE/SINK/TRANSFORM',
    support_engine VARCHAR(128) COMMENT '支持的引擎: Zeta/Flink/Spark',
    driver_class VARCHAR(256) COMMENT 'Driver类名',
    description VARCHAR(512) COMMENT '描述',
    icon VARCHAR(256) COMMENT '图标',
    status INT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间',
    UNIQUE KEY uk_connector_code (connector_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SeaTunnel Connector类型';

-- 2. Connector配置项定义表
CREATE TABLE IF NOT EXISTS di_connector_option (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    connector_type_id VARCHAR(64) COMMENT 'Connector类型ID',
    option_code VARCHAR(64) COMMENT '配置项编码',
    option_name VARCHAR(128) COMMENT '配置项名称',
    data_type VARCHAR(32) COMMENT '数据类型: STRING/INTEGER/BOOLEAN/ARRAY/OBJECT',
    required TINYINT DEFAULT 0 COMMENT '是否必填: 0-否, 1-是',
    default_value VARCHAR(512) COMMENT '默认值',
    description VARCHAR(512) COMMENT '配置描述',
    sort_no INT DEFAULT 0 COMMENT '排序号',
    status INT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Connector配置项定义';

-- 3. 数据作业表
CREATE TABLE IF NOT EXISTS di_data_job (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    job_code VARCHAR(64) COMMENT '作业编码',
    job_name VARCHAR(128) NOT NULL COMMENT '作业名称',
    pipeline_id VARCHAR(64) COMMENT '管道ID',
    engine_type VARCHAR(64) COMMENT '引擎类型: SeaTunnel/DataConnect',
    execute_mode VARCHAR(32) COMMENT '执行模式: BATCH/STREAMING',
    cron_expression VARCHAR(128) COMMENT 'Cron表达式(定时调度)',
    is_schedule INT DEFAULT 0 COMMENT '是否定时调度: 0-否, 1-是',
    config_content TEXT COMMENT 'SeaTunnel配置文件内容',
    runtime_params VARCHAR(1024) COMMENT '运行参数(JSON)',
    status INT DEFAULT 0 COMMENT '状态: 0-草稿, 1-已发布, 2-运行中, 3-已停止',
    last_run_time DATETIME COMMENT '最后运行时间',
    last_run_status INT COMMENT '最后运行状态: 0-失败, 1-成功',
    description VARCHAR(512) COMMENT '描述',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据作业';

-- 4. 数据作业执行记录表
CREATE TABLE IF NOT EXISTS di_data_job_execute (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    job_id VARCHAR(64) COMMENT '作业ID',
    pipeline_id VARCHAR(64) COMMENT '管道ID',
    execute_id VARCHAR(128) COMMENT '执行ID(引擎返回)',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    read_rows BIGINT DEFAULT 0 COMMENT '读取行数',
    write_rows BIGINT DEFAULT 0 COMMENT '写入行数',
    error_rows BIGINT DEFAULT 0 COMMENT '错误行数',
    duration BIGINT DEFAULT 0 COMMENT '执行耗时(ms)',
    status INT DEFAULT 0 COMMENT '状态: 0-运行中, 1-成功, 2-失败, 3-取消',
    error_msg TEXT COMMENT '错误信息',
    execute_log TEXT COMMENT '执行日志',
    trigger_type INT DEFAULT 0 COMMENT '触发类型: 0-手动, 1-定时',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据作业执行记录';

-- 5. 初始化SeaTunnel Connector类型数据
INSERT INTO di_connector_type (id, connector_code, connector_name, connector_type, support_engine, description, status, create_time) VALUES
-- Source Connectors
('101', 'Jdbc-MySQL-Source', 'MySQL Source', 'SOURCE', 'Zeta,Flink,Spark', '从MySQL数据库读取数据', 1, NOW()),
('102', 'Jdbc-Oracle-Source', 'Oracle Source', 'SOURCE', 'Zeta,Flink,Spark', '从Oracle数据库读取数据', 1, NOW()),
('103', 'Jdbc-PostgreSQL-Source', 'PostgreSQL Source', 'SOURCE', 'Zeta,Flink,Spark', '从PostgreSQL数据库读取数据', 1, NOW()),
('104', 'Kafka-Source', 'Kafka Source', 'SOURCE', 'Zeta,Flink,Spark', '从Kafka读取数据', 1, NOW()),
('105', 'File-Source', 'File Source', 'SOURCE', 'Zeta,Flink,Spark', '从文件读取数据', 1, NOW()),

-- Sink Connectors
('201', 'Jdbc-MySQL-Sink', 'MySQL Sink', 'SINK', 'Zeta,Flink,Spark', '写入数据到MySQL数据库', 1, NOW()),
('202', 'Jdbc-Oracle-Sink', 'Oracle Sink', 'SINK', 'Zeta,Flink,Spark', '写入数据到Oracle数据库', 1, NOW()),
('203', 'Jdbc-PostgreSQL-Sink', 'PostgreSQL Sink', 'SINK', 'Zeta,Flink,Spark', '写入数据到PostgreSQL数据库', 1, NOW()),
('204', 'Kafka-Sink', 'Kafka Sink', 'SINK', 'Zeta,Flink,Spark', '写入数据到Kafka', 1, NOW()),
('205', 'File-Sink', 'File Sink', 'SINK', 'Zeta,Flink,Spark', '写入数据到文件', 1, NOW()),
('206', 'Console-Sink', 'Console Sink', 'SINK', 'Zeta,Flink,Spark', '输出到控制台', 1, NOW()),

-- Transform Connectors
('301', 'Sql-Transform', 'SQL Transform', 'TRANSFORM', 'Zeta,Flink,Spark', 'SQL转换', 1, NOW()),
('302', 'FieldMapper-Transform', 'Field Mapper Transform', 'TRANSFORM', 'Zeta,Flink,Spark', '字段映射转换', 1, NOW()),
('303', 'Filter-Transform', 'Filter Transform', 'TRANSFORM', 'Zeta,Flink,Spark', '过滤转换', 1, NOW()),
('304', 'Replace-Transform', 'Replace Transform', 'TRANSFORM', 'Zeta,Flink,Spark', '替换转换', 1, NOW());

-- 6. 初始化Connector配置项
-- MySQL Source配置项
INSERT INTO di_connector_option (id, connector_type_id, option_code, option_name, data_type, required, description, sort_no, status, create_time) VALUES
('1001', '101', 'url', 'JDBC URL', 'STRING', 1, 'JDBC连接URL', 1, 1, NOW()),
('1002', '101', 'driver', 'Driver Class', 'STRING', 1, 'JDBC驱动类名', 2, 1, NOW()),
('1003', '101', 'user', 'Username', 'STRING', 1, '数据库用户名', 3, 1, NOW()),
('1004', '101', 'password', 'Password', 'STRING', 1, '数据库密码', 4, 1, NOW()),
('1005', '101', 'query', 'SQL Query', 'STRING', 1, 'SQL查询语句', 5, 1, NOW()),
('1006', '101', 'connection_check_timeout_sec', 'Connection Check Timeout', 'INTEGER', 0, '连接检查超时时间(秒)', 6, 1, NOW());

-- Kafka Source配置项
INSERT INTO di_connector_option (id, connector_type_id, option_code, option_name, data_type, required, description, sort_no, status, create_time) VALUES
('2001', '104', 'bootstrap.servers', 'Bootstrap Servers', 'STRING', 1, 'Kafka服务器地址', 1, 1, NOW()),
('2002', '104', 'topic', 'Topic', 'STRING', 1, 'Kafka主题', 2, 1, NOW()),
('2003', '104', 'consumer.group', 'Consumer Group', 'STRING', 0, '消费者组ID', 3, 1, NOW()),
('2004', '104', 'format', 'Message Format', 'STRING', 1, '消息格式: json/csv/text', 4, 1, NOW());

-- MySQL Sink配置项
INSERT INTO di_connector_option (id, connector_type_id, option_code, option_name, data_type, required, description, sort_no, status, create_time) VALUES
('3001', '201', 'url', 'JDBC URL', 'STRING', 1, 'JDBC连接URL', 1, 1, NOW()),
('3002', '201', 'driver', 'Driver Class', 'STRING', 1, 'JDBC驱动类名', 2, 1, NOW()),
('3003', '201', 'user', 'Username', 'STRING', 1, '数据库用户名', 3, 1, NOW()),
('3004', '201', 'password', 'Password', 'STRING', 1, '数据库密码', 4, 1, NOW()),
('3005', '201', 'query', 'SQL Query', 'STRING', 1, 'SQL插入语句', 5, 1, NOW()),
('3006', '201', 'batch_size', 'Batch Size', 'INTEGER', 0, '批量写入大小', 6, 1, NOW());

-- SQL Transform配置项
INSERT INTO di_connector_option (id, connector_type_id, option_code, option_name, data_type, required, description, sort_no, status, create_time) VALUES
('4001', '301', 'source_table_name', 'Source Table Name', 'STRING', 1, '源表名称', 1, 1, NOW()),
('4002', '301', 'result_table_name', 'Result Table Name', 'STRING', 1, '结果表名称', 2, 1, NOW()),
('4003', '301', 'query', 'SQL Query', 'STRING', 1, 'SQL转换语句', 3, 1, NOW());
