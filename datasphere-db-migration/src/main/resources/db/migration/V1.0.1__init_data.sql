-- ============================================
-- DataSphere Next 数据初始化脚本 V1.0.1
-- 初始化基础数据
-- ============================================

-- 1. 初始化数据源分类
-- ----------------------------
INSERT INTO datasource_classify (id, classify_code, classify_name, order_no, create_time) VALUES
('1', 'RDBMS', '关系型数据库', 1, NOW()),
('2', 'NOSQL', 'NoSQL数据库', 2, NOW()),
('3', 'BIGDATA', '大数据存储', 3, NOW()),
('4', 'FILE', '文件存储', 4, NOW()),
('5', 'API', 'API接口', 5, NOW());

-- 2. 初始化数据源类型
-- ----------------------------
INSERT INTO datasource_type (id, data_type, data_type_name, classify_code, classify_id, driver, url, order_no, status, create_time) VALUES
-- 关系型数据库
('101', 'MySQL', 'MySQL', 'RDBMS', '1', 'com.mysql.cj.jdbc.Driver', 'jdbc:mysql://{host}:{port}/{database}', 1, 'ENABLE', NOW()),
('102', 'Oracle', 'Oracle', 'RDBMS', '1', 'oracle.jdbc.OracleDriver', 'jdbc:oracle:thin:@{host}:{port}:{database}', 2, 'ENABLE', NOW()),
('103', 'PostgreSQL', 'PostgreSQL', 'RDBMS', '1', 'org.postgresql.Driver', 'jdbc:postgresql://{host}:{port}/{database}', 3, 'ENABLE', NOW()),
('104', 'SQLServer', 'SQL Server', 'RDBMS', '1', 'com.microsoft.sqlserver.jdbc.SQLServerDriver', 'jdbc:sqlserver://{host}:{port};databaseName={database}', 4, 'ENABLE', NOW()),
('105', 'DM', '达梦数据库', 'RDBMS', '1', 'dm.jdbc.driver.DmDriver', 'jdbc:dm://{host}:{port}/{database}', 5, 'ENABLE', NOW()),

-- NoSQL数据库
('201', 'MongoDB', 'MongoDB', 'NOSQL', '2', '', 'mongodb://{host}:{port}/{database}', 1, 'ENABLE', NOW()),
('202', 'Redis', 'Redis', 'NOSQL', '2', '', 'redis://{host}:{port}', 2, 'ENABLE', NOW()),

-- 大数据存储
('301', 'Hive', 'Apache Hive', 'BIGDATA', '3', 'org.apache.hive.jdbc.HiveDriver', 'jdbc:hive2://{host}:{port}/{database}', 1, 'ENABLE', NOW()),
('302', 'ClickHouse', 'ClickHouse', 'BIGDATA', '3', 'com.clickhouse.jdbc.ClickHouseDriver', 'jdbc:clickhouse://{host}:{port}/{database}', 2, 'ENABLE', NOW()),

-- 文件存储
('401', 'FTP', 'FTP服务器', 'FILE', '4', '', 'ftp://{host}:{port}', 1, 'ENABLE', NOW()),
('402', 'SFTP', 'SFTP服务器', 'FILE', '4', '', 'sftp://{host}:{port}', 2, 'ENABLE', NOW()),

-- API接口
('501', 'REST_API', 'REST API', 'API', '5', '', 'http://{host}:{port}', 1, 'ENABLE', NOW());

-- 3. 初始化标准字典
-- ----------------------------
INSERT INTO md_dictionary (id, dict_code, dict_name, dict_type, description, status, create_time) VALUES
('1', 'GENDER', '性别', 'SYSTEM', '性别字典', 1, NOW()),
('2', 'ORG_TYPE', '机构类型', 'SYSTEM', '机构类型字典', 1, NOW()),
('3', 'DEPT_TYPE', '科室类型', 'SYSTEM', '科室类型字典', 1, NOW()),
('4', 'ID_CARD_TYPE', '证件类型', 'SYSTEM', '证件类型字典', 1, NOW()),
('5', 'DATA_TYPE', '数据类型', 'SYSTEM', '数据类型字典', 1, NOW());

-- 4. 初始化元模型
-- ----------------------------
INSERT INTO meta_model (id, model_code, model_name, model_type, description, create_time) VALUES
('1', 'DATABASE', '数据库', 'TABLE', '数据库元模型', NOW()),
('2', 'TABLE', '数据表', 'TABLE', '数据表元模型', NOW()),
('3', 'COLUMN', '数据列', 'COLUMN', '数据列元模型', NOW()),
('4', 'API', 'API接口', 'OTHER', 'API接口元模型', NOW());
