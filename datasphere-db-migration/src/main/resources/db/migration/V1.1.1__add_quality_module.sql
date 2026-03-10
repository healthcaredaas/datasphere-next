-- ============================================
-- DataSphere Next 数据库迁移脚本 V1.1.1
-- 增加数据质量管理扩展表
-- ============================================

-- 1. 质量规则模板表
CREATE TABLE IF NOT EXISTS dq_rule_template (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    template_code VARCHAR(64) COMMENT '模板编码',
    template_name VARCHAR(128) NOT NULL COMMENT '模板名称',
    rule_type VARCHAR(64) COMMENT '规则类型: COMPLETENESS/UNIQUENESS/FORMAT/VALUE_RANGE/CONSISTENCY/ACCURACY/CUSTOM',
    db_type VARCHAR(64) COMMENT '数据库类型: MySQL/Oracle/PostgreSQL/SQLServer/DM/通用',
    expression_template TEXT COMMENT '规则表达式模板',
    param_definition TEXT COMMENT '参数定义(JSON): [{name, type, required, description}]',
    error_message_template VARCHAR(512) COMMENT '错误提示模板',
    description VARCHAR(512) COMMENT '描述',
    sort_no INT DEFAULT 0 COMMENT '排序号',
    status INT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间',
    UNIQUE KEY uk_template_code (template_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质量规则模板';

-- 2. 质量问题记录表
CREATE TABLE IF NOT EXISTS dq_issue (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    task_id VARCHAR(64) COMMENT '任务ID',
    rule_id VARCHAR(64) COMMENT '规则ID',
    datasource_id VARCHAR(64) COMMENT '数据源ID',
    table_name VARCHAR(128) COMMENT '表名',
    column_name VARCHAR(128) COMMENT '字段名',
    rule_type VARCHAR(64) COMMENT '规则类型',
    primary_key_value TEXT COMMENT '主键值(JSON格式，用于定位错误数据)',
    error_value VARCHAR(1024) COMMENT '错误数据值',
    expected_value VARCHAR(1024) COMMENT '期望的值',
    error_message VARCHAR(512) COMMENT '错误描述',
    handle_status INT DEFAULT 0 COMMENT '处理状态: 0-未处理, 1-已忽略, 2-已修复',
    handler VARCHAR(64) COMMENT '处理人',
    handle_time DATETIME COMMENT '处理时间',
    handle_remark VARCHAR(512) COMMENT '处理备注',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间',
    INDEX idx_task_id (task_id),
    INDEX idx_rule_id (rule_id),
    INDEX idx_handle_status (handle_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质量问题记录';

-- 3. 质量检测报告表
CREATE TABLE IF NOT EXISTS dq_report (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    task_id VARCHAR(64) COMMENT '任务ID',
    rule_id VARCHAR(64) COMMENT '规则ID',
    report_name VARCHAR(256) COMMENT '报告名称',
    report_date DATETIME COMMENT '报告日期',
    total_rows BIGINT DEFAULT 0 COMMENT '检测总行数',
    error_rows BIGINT DEFAULT 0 COMMENT '错误行数',
    score DECIMAL(5,2) DEFAULT 100.00 COMMENT '质量评分(0-100)',
    grade VARCHAR(2) COMMENT '质量等级: A/B/C/D/F',
    report_content TEXT COMMENT '报告内容(Markdown)',
    status INT DEFAULT 1 COMMENT '状态: 0-失败, 1-成功',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(2) DEFAULT '0' COMMENT '删除标识',
    delete_time DATETIME COMMENT '删除时间',
    INDEX idx_task_id (task_id),
    INDEX idx_rule_id (rule_id),
    INDEX idx_report_date (report_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质量检测报告';

-- 4. 初始化质量规则模板数据
INSERT INTO dq_rule_template (id, template_code, template_name, rule_type, db_type, expression_template, param_definition, error_message_template, description, sort_no, status, create_time) VALUES
-- ============================================
-- 一、完整性检查模板 (COMPLETENESS)
-- ============================================

-- 1.1 基础完整性检查 - MySQL
('1001', 'COMPLETENESS_MYSQL', '基础完整性检查(MySQL)', 'COMPLETENESS', 'MySQL',
'SELECT * FROM ${table} WHERE ${column} IS NULL OR ${column} = \"\"',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "检查字段"}]',
'字段${column}不能为空',
'检查指定字段是否为NULL或空字符串(MySQL)', 1, 1, NOW()),

-- 1.2 基础完整性检查 - Oracle
('1002', 'COMPLETENESS_ORACLE', '基础完整性检查(Oracle)', 'COMPLETENESS', 'Oracle',
'SELECT * FROM ${table} WHERE ${column} IS NULL',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "检查字段"}]',
'字段${column}不能为空',
'检查指定字段是否为NULL(Oracle)', 2, 1, NOW()),

-- 1.3 基础完整性检查 - PostgreSQL
('1003', 'COMPLETENESS_PG', '基础完整性检查(PostgreSQL)', 'COMPLETENESS', 'PostgreSQL',
'SELECT * FROM ${table} WHERE ${column} IS NULL OR ${column} = \"\"',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "检查字段"}]',
'字段${column}不能为空',
'检查指定字段是否为NULL或空字符串(PostgreSQL)', 3, 1, NOW()),

-- 1.4 基础完整性检查 - SQLServer
('1004', 'COMPLETENESS_SQLSERVER', '基础完整性检查(SQLServer)', 'COMPLETENESS', 'SQLServer',
'SELECT * FROM ${table} WHERE ${column} IS NULL OR ${column} = \"\"',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "检查字段"}]',
'字段${column}不能为空',
'检查指定字段是否为NULL或空字符串(SQLServer)', 4, 1, NOW()),

-- 1.5 多字段完整性检查
('1005', 'COMPLETENESS_MULTI', '多字段完整性检查', 'COMPLETENESS', '通用',
'SELECT * FROM ${table} WHERE ${column1} IS NULL OR ${column2} IS NULL',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column1", "type": "string", "required": true, "description": "字段1"}, {"name": "column2", "type": "string", "required": true, "description": "字段2"}]',
'字段${column1}或${column2}不能为空',
'检查多个字段是否同时为空', 5, 1, NOW()),

-- ============================================
-- 二、唯一性检查模板 (UNIQUENESS)
-- ============================================

-- 2.1 单字段唯一性检查
('1010', 'UNIQUENESS_SINGLE', '单字段唯一性检查', 'UNIQUENESS', '通用',
'SELECT ${column}, COUNT(*) as duplicate_count FROM ${table} GROUP BY ${column} HAVING COUNT(*) > 1',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "检查字段"}]',
'字段${column}存在重复值',
'检查单字段是否有重复值', 10, 1, NOW()),

-- 2.2 组合字段唯一性检查
('1011', 'UNIQUENESS_COMBINED', '组合字段唯一性检查', 'UNIQUENESS', '通用',
'SELECT ${column1}, ${column2}, COUNT(*) as duplicate_count FROM ${table} GROUP BY ${column1}, ${column2} HAVING COUNT(*) > 1',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column1", "type": "string", "required": true, "description": "字段1"}, {"name": "column2", "type": "string", "required": true, "description": "字段2"}]',
'字段组合(${column1}, ${column2})存在重复值',
'检查组合字段是否有重复值', 11, 1, NOW()),

-- 2.3 带业务条件的唯一性检查
('1012', 'UNIQUENESS_CONDITIONAL', '条件唯一性检查', 'UNIQUENESS', '通用',
'SELECT ${column}, ${conditionColumn}, COUNT(*) as duplicate_count FROM ${table} WHERE ${conditionColumn} = ${conditionValue} GROUP BY ${column}, ${conditionColumn} HAVING COUNT(*) > 1',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "检查字段"}, {"name": "conditionColumn", "type": "string", "required": true, "description": "条件字段"}, {"name": "conditionValue", "type": "string", "required": true, "description": "条件值"}]',
'在指定条件下字段${column}存在重复值',
'检查特定条件下的字段唯一性', 12, 1, NOW()),

-- ============================================
-- 三、格式检查模板 (FORMAT)
-- ============================================

-- 3.1 中国大陆手机号格式
('1020', 'FORMAT_PHONE_CN', '中国大陆手机号格式', 'FORMAT', 'MySQL',
'SELECT * FROM ${table} WHERE ${column} NOT REGEXP \"^[1][3-9][0-9]{9}$\"',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "手机号字段"}]',
'手机号${column}格式不正确，应为11位数字',
'验证中国大陆手机号格式(11位，1开头)', 20, 1, NOW()),

-- 3.2 邮箱格式检查
('1021', 'FORMAT_EMAIL', '邮箱格式检查', 'FORMAT', 'MySQL',
'SELECT * FROM ${table} WHERE ${column} NOT REGEXP \"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$\"',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "邮箱字段"}]',
'邮箱${column}格式不正确',
'验证标准邮箱格式', 21, 1, NOW()),

-- 3.3 中国大陆身份证号
('1022', 'FORMAT_IDCARD_CN', '中国大陆身份证号', 'FORMAT', 'MySQL',
'SELECT * FROM ${table} WHERE ${column} NOT REGEXP \"^[0-9]{17}[0-9Xx]$\"',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "身份证字段"}]',
'身份证号${column}格式不正确，应为18位',
'验证18位身份证号格式', 22, 1, NOW()),

-- 3.4 固定电话格式
('1023', 'FORMAT_TELPHONE', '固定电话格式', 'FORMAT', 'MySQL',
'SELECT * FROM ${table} WHERE ${column} NOT REGEXP \"^(0[0-9]{2,3}-)?[2-9][0-9]{6,7}$\"',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "电话字段"}]',
'固定电话${column}格式不正确',
'验证固定电话格式', 23, 1, NOW()),

-- 3.5 邮政编码格式
('1024', 'FORMAT_ZIPCODE', '邮政编码格式', 'FORMAT', 'MySQL',
'SELECT * FROM ${table} WHERE ${column} NOT REGEXP \"^[0-9]{6}$\"',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "邮编字段"}]',
'邮政编码${column}格式不正确，应为6位数字',
'验证6位邮政编码格式', 24, 1, NOW()),

-- 3.6 URL格式检查
('1025', 'FORMAT_URL', 'URL格式检查', 'FORMAT', 'MySQL',
'SELECT * FROM ${table} WHERE ${column} NOT REGEXP \"^(https?|ftp)://[^\\s/$.?#].[^\\s]*$\"',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "URL字段"}]',
'URL${column}格式不正确',
'验证HTTP/HTTPS/FTP URL格式', 25, 1, NOW()),

-- 3.7 IPv4地址格式
('1026', 'FORMAT_IPV4', 'IPv4地址格式', 'FORMAT', 'MySQL',
'SELECT * FROM ${table} WHERE ${column} NOT REGEXP \"^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$\"',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "IP字段"}]',
'IPv4地址${column}格式不正确',
'验证IPv4地址格式', 26, 1, NOW()),

-- 3.8 日期格式检查(YYYY-MM-DD)
('1027', 'FORMAT_DATE_ISO', 'ISO日期格式', 'FORMAT', 'MySQL',
'SELECT * FROM ${table} WHERE ${column} NOT REGEXP \"^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$\"',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "日期字段"}]',
'日期${column}格式不正确，应为YYYY-MM-DD格式',
'验证ISO日期格式', 27, 1, NOW()),

-- 3.9 自定义正则格式
('1028', 'FORMAT_CUSTOM_REGEX', '自定义正则格式', 'FORMAT', 'MySQL',
'SELECT * FROM ${table} WHERE ${column} NOT REGEXP \"${pattern}\"',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "检查字段"}, {"name": "pattern", "type": "string", "required": true, "description": "正则表达式"}]',
'字段${column}不符合指定格式',
'使用自定义正则表达式验证格式', 28, 1, NOW()),

-- ============================================
-- 四、值域检查模板 (VALUE_RANGE)
-- ============================================

-- 4.1 数值范围检查
('1030', 'VALUE_RANGE_NUMBER', '数值范围检查', 'VALUE_RANGE', '通用',
'SELECT * FROM ${table} WHERE ${column} < ${min} OR ${column} > ${max}',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "字段名"}, {"name": "min", "type": "number", "required": true, "description": "最小值"}, {"name": "max", "type": "number", "required": true, "description": "最大值"}]',
'字段${column}的值必须在${min}和${max}之间',
'检查数值是否在指定范围内', 30, 1, NOW()),

-- 4.2 枚举值检查
('1031', 'VALUE_ENUM', '枚举值检查', 'VALUE_RANGE', '通用',
'SELECT * FROM ${table} WHERE ${column} NOT IN (${values})',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "字段名"}, {"name": "values", "type": "string", "required": true, "description": "枚举值列表(逗号分隔，字符串需加引号)"}]',
'字段${column}的值不在允许的列表中',
'检查字段值是否在指定枚举范围内', 31, 1, NOW()),

-- 4.3 大于指定值
('1032', 'VALUE_GREATER_THAN', '大于指定值', 'VALUE_RANGE', '通用',
'SELECT * FROM ${table} WHERE ${column} <= ${threshold}',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "字段名"}, {"name": "threshold", "type": "number", "required": true, "description": "阈值"}]',
'字段${column}的值必须大于${threshold}',
'检查数值是否大于指定阈值', 32, 1, NOW()),

-- 4.4 小于指定值
('1033', 'VALUE_LESS_THAN', '小于指定值', 'VALUE_RANGE', '通用',
'SELECT * FROM ${table} WHERE ${column} >= ${threshold}',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "字段名"}, {"name": "threshold", "type": "number", "required": true, "description": "阈值"}]',
'字段${column}的值必须小于${threshold}',
'检查数值是否小于指定阈值', 33, 1, NOW()),

-- 4.5 字符串长度范围
('1034', 'VALUE_LENGTH_RANGE', '字符串长度范围', 'VALUE_RANGE', '通用',
'SELECT * FROM ${table} WHERE LENGTH(${column}) < ${minLen} OR LENGTH(${column}) > ${maxLen}',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "字段名"}, {"name": "minLen", "type": "number", "required": true, "description": "最小长度"}, {"name": "maxLen", "type": "number", "required": true, "description": "最大长度"}]',
'字段${column}的长度必须在${minLen}到${maxLen}之间',
'检查字符串长度是否在指定范围内', 34, 1, NOW()),

-- 4.6 外键引用检查
('1035', 'VALUE_FOREIGN_KEY', '外键引用检查', 'VALUE_RANGE', '通用',
'SELECT t.* FROM ${table} t LEFT JOIN ${refTable} r ON t.${column} = r.${refColumn} WHERE r.${refColumn} IS NULL',
'[{"name": "table", "type": "string", "required": true, "description": "主表名"}, {"name": "column", "type": "string", "required": true, "description": "外键字段"}, {"name": "refTable", "type": "string", "required": true, "description": "参考表名"}, {"name": "refColumn", "type": "string", "required": true, "description": "参考字段"}]',
'字段${column}引用的数据在${refTable}中不存在',
'检查外键引用是否有效', 35, 1, NOW()),

-- 4.7 日期范围检查
('1036', 'VALUE_DATE_RANGE', '日期范围检查', 'VALUE_RANGE', '通用',
'SELECT * FROM ${table} WHERE ${column} < \"${startDate}\" OR ${column} > \"${endDate}\"',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "column", "type": "string", "required": true, "description": "日期字段"}, {"name": "startDate", "type": "string", "required": true, "description": "开始日期(YYYY-MM-DD)"}, {"name": "endDate", "type": "string", "required": true, "description": "结束日期(YYYY-MM-DD)"}]',
'日期${column}必须在${startDate}和${endDate}之间',
'检查日期是否在指定范围内', 36, 1, NOW()),

-- ============================================
-- 五、一致性检查模板 (CONSISTENCY)
-- ============================================

-- 5.1 外键引用一致性
('1040', 'CONSISTENCY_FOREIGN_KEY', '外键引用一致性', 'CONSISTENCY', '通用',
'SELECT m.* FROM ${mainTable} m LEFT JOIN ${refTable} r ON m.${mainColumn} = r.${refColumn} WHERE r.${refColumn} IS NULL',
'[{"name": "mainTable", "type": "string", "required": true, "description": "主表名"}, {"name": "mainColumn", "type": "string", "required": true, "description": "主表字段"}, {"name": "refTable", "type": "string", "required": true, "description": "参考表名"}, {"name": "refColumn", "type": "string", "required": true, "description": "参考字段"}]',
'${mainTable}.${mainColumn}引用的数据在${refTable}中不存在',
'检查主表字段在参考表中是否存在', 40, 1, NOW()),

-- 5.2 反向存在检查
('1041', 'CONSISTENCY_REVERSE_EXIST', '反向存在检查', 'CONSISTENCY', '通用',
'SELECT r.* FROM ${refTable} r LEFT JOIN ${mainTable} m ON r.${refColumn} = m.${mainColumn} WHERE m.${mainColumn} IS NULL',
'[{"name": "mainTable", "type": "string", "required": true, "description": "主表名"}, {"name": "mainColumn", "type": "string", "required": true, "description": "主表字段"}, {"name": "refTable", "type": "string", "required": true, "description": "参考表名"}, {"name": "refColumn", "type": "string", "required": true, "description": "参考字段"}]',
'${refTable}中存在记录但${mainTable}中不存在对应记录',
'检查参考表记录在主表中是否存在', 41, 1, NOW()),

-- 5.3 汇总一致性检查
('1042', 'CONSISTENCY_SUM_MATCH', '汇总金额一致性', 'CONSISTENCY', '通用',
'SELECT m.* FROM ${mainTable} m LEFT JOIN (SELECT ${joinColumn}, SUM(${detailColumn}) as sum_val FROM ${detailTable} GROUP BY ${joinColumn}) d ON m.${joinColumn} = d.${joinColumn} WHERE m.${mainColumn} != d.sum_val OR d.sum_val IS NULL',
'[{"name": "mainTable", "type": "string", "required": true, "description": "主表名"}, {"name": "mainColumn", "type": "string", "required": true, "description": "主表汇总字段"}, {"name": "detailTable", "type": "string", "required": true, "description": "明细表名"}, {"name": "detailColumn", "type": "string", "required": true, "description": "明细字段"}, {"name": "joinColumn", "type": "string", "required": true, "description": "关联字段"}]',
'${mainTable}.${mainColumn}与${detailTable}的汇总值不一致',
'检查主表汇总字段与明细表汇总是否一致', 42, 1, NOW()),

-- 5.4 记录数一致性
('1043', 'CONSISTENCY_COUNT_MATCH', '记录数一致性', 'CONSISTENCY', '通用',
'SELECT m.* FROM ${mainTable} m LEFT JOIN (SELECT ${joinColumn}, COUNT(*) as cnt FROM ${detailTable} GROUP BY ${joinColumn}) d ON m.${joinColumn} = d.${joinColumn} WHERE m.${countColumn} != IFNULL(d.cnt, 0)',
'[{"name": "mainTable", "type": "string", "required": true, "description": "主表名"}, {"name": "countColumn", "type": "string", "required": true, "description": "计数字段"}, {"name": "detailTable", "type": "string", "required": true, "description": "明细表名"}, {"name": "joinColumn", "type": "string", "required": true, "description": "关联字段"}]',
'${mainTable}.${countColumn}与实际记录数不一致',
'检查主表计数字段与实际记录数是否一致', 43, 1, NOW()),

-- 5.5 字段值相等检查
('1044', 'CONSISTENCY_FIELD_EQUAL', '字段值相等检查', 'CONSISTENCY', '通用',
'SELECT m.* FROM ${mainTable} m LEFT JOIN ${refTable} r ON m.${joinColumn} = r.${joinColumn} WHERE m.${mainColumn} != r.${refColumn} OR r.${refColumn} IS NULL',
'[{"name": "mainTable", "type": "string", "required": true, "description": "主表名"}, {"name": "mainColumn", "type": "string", "required": true, "description": "主表字段"}, {"name": "refTable", "type": "string", "required": true, "description": "参考表名"}, {"name": "refColumn", "type": "string", "required": true, "description": "参考字段"}, {"name": "joinColumn", "type": "string", "required": true, "description": "关联字段"}]',
'${mainTable}.${mainColumn}与${refTable}.${refColumn}不相等',
'检查关联表中对应字段值是否相等', 44, 1, NOW()),

-- 5.6 时间顺序一致性
('1045', 'CONSISTENCY_DATE_SEQUENCE', '时间顺序一致性', 'CONSISTENCY', '通用',
'SELECT * FROM ${table} WHERE ${laterTime} <= ${earlierTime} OR (${laterTime} IS NOT NULL AND ${earlierTime} IS NULL)',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "earlierTime", "type": "string", "required": true, "description": "较早时间字段"}, {"name": "laterTime", "type": "string", "required": true, "description": "较晚时间字段"}]',
'时间顺序不正确：${laterTime}应该在${earlierTime}之后',
'检查时间字段的逻辑顺序', 45, 1, NOW()),

-- ============================================
-- 六、准确性检查模板 (ACCURACY)
-- ============================================

-- 6.1 金额范围准确性
('1050', 'ACCURACY_AMOUNT_RANGE', '金额范围准确性', 'ACCURACY', '通用',
'SELECT * FROM ${table} WHERE ${amountColumn} <= 0 OR ${amountColumn} > ${maxAmount}',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "amountColumn", "type": "string", "required": true, "description": "金额字段"}, {"name": "maxAmount", "type": "number", "required": true, "description": "最大金额"}]',
'金额${amountColumn}必须大于0且不超过${maxAmount}',
'检查金额是否在合理范围内', 50, 1, NOW()),

-- 6.2 优惠金额准确性
('1051', 'ACCURACY_DISCOUNT', '优惠金额准确性', 'ACCURACY', '通用',
'SELECT * FROM ${table} WHERE ${discountColumn} > ${totalColumn}',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "discountColumn", "type": "string", "required": true, "description": "优惠金额字段"}, {"name": "totalColumn", "type": "string", "required": true, "description": "总金额字段"}]',
'优惠金额${discountColumn}不能大于总金额${totalColumn}',
'检查优惠金额是否超过总金额', 51, 1, NOW()),

-- 6.3 库存数量准确性
('1052', 'ACCURACY_STOCK', '库存数量准确性', 'ACCURACY', '通用',
'SELECT * FROM ${table} WHERE ${stockColumn} < 0',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "stockColumn", "type": "string", "required": true, "description": "库存字段"}]',
'库存数量${stockColumn}不能为负数',
'检查库存数量是否为非负数', 52, 1, NOW()),

-- 6.4 退款金额准确性
('1053', 'ACCURACY_REFUND', '退款金额准确性', 'ACCURACY', '通用',
'SELECT * FROM ${table} WHERE ${refundColumn} > ${payColumn}',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "refundColumn", "type": "string", "required": true, "description": "退款金额字段"}, {"name": "payColumn", "type": "string", "required": true, "description": "支付金额字段"}]',
'退款金额${refundColumn}不能大于支付金额${payColumn}',
'检查退款金额是否超过支付金额', 53, 1, NOW()),

-- 6.5 多字段逻辑准确性
('1054', 'ACCURACY_LOGIC', '多字段逻辑准确性', 'ACCURACY', '通用',
'SELECT * FROM ${table} WHERE (${statusColumn} = \"${completedStatus}\" AND ${completeTime} IS NULL) OR (${statusColumn} != \"${completedStatus}\" AND ${completeTime} IS NOT NULL)',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "statusColumn", "type": "string", "required": true, "description": "状态字段"}, {"name": "completedStatus", "type": "string", "required": true, "description": "完成状态值"}, {"name": "completeTime", "type": "string", "required": true, "description": "完成时间字段"}]',
'状态与时间字段逻辑不匹配',
'检查状态与时间字段的逻辑一致性', 54, 1, NOW()),

-- ============================================
-- 七、自定义检查模板 (CUSTOM)
-- ============================================

-- 7.1 自定义SQL检查
('1060', 'CUSTOM_SQL', '自定义SQL检查', 'CUSTOM', '通用',
'${customSql}',
'[{"name": "customSql", "type": "string", "required": true, "description": "完整自定义SQL语句，用于查询不符合规则的数据"}]',
'数据不符合自定义规则',
'使用完全自定义的SQL进行数据检查', 60, 1, NOW()),

-- 7.2 自定义WHERE条件
('1061', 'CUSTOM_WHERE', '自定义WHERE条件', 'CUSTOM', '通用',
'SELECT * FROM ${table} WHERE ${whereCondition}',
'[{"name": "table", "type": "string", "required": true, "description": "表名"}, {"name": "whereCondition", "type": "string", "required": true, "description": "WHERE条件表达式"}]',
'数据不符合指定条件',
'使用自定义WHERE条件进行数据检查', 61, 1, NOW());

-- ============================================
-- 初始化说明
-- ============================================
-- 本脚本初始化了40个常用的质量规则模板，涵盖7大规则类型：
-- 1. 完整性检查: 5个模板 (1001-1005)
-- 2. 唯一性检查: 3个模板 (1010-1012)
-- 3. 格式检查: 9个模板 (1020-1028)
-- 4. 值域检查: 7个模板 (1030-1036)
-- 5. 一致性检查: 6个模板 (1040-1045)
-- 6. 准确性检查: 5个模板 (1050-1054)
-- 7. 自定义检查: 2个模板 (1060-1061)
--
-- 使用示例:
-- 1. 通过模板快速创建规则
--    POST /api/v1/quality/rules/create-from-template
--    {
--      "templateId": "1020",
--      "params": {
--        "table": "user_info",
--        "column": "phone",
--        "datasourceId": "ds_001",
--        "ruleName": "用户手机号格式检查"
--      }
--    }
--
-- 2. 查询所有模板
--    GET /api/v1/quality/templates
--
-- 3. 查询特定类型的模板
--    GET /api/v1/quality/templates?ruleType=FORMAT
--
