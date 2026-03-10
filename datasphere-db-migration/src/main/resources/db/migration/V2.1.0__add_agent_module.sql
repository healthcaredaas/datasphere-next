-- ========================================
-- AI Agent 模块数据库表
-- 版本: V2.1.0
-- 日期: 2026-03-09
-- ========================================

-- 1. 模型配置表
CREATE TABLE IF NOT EXISTS ai_model_config (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    model_name VARCHAR(100) NOT NULL COMMENT '模型名称',
    model_type VARCHAR(20) NOT NULL COMMENT '模型类型: CLAUDE/GPT/QWEN/LLAMA/LOCAL',
    api_endpoint VARCHAR(500) COMMENT 'API端点',
    api_key TEXT COMMENT 'API密钥(加密)',
    model_params JSON COMMENT '模型参数',
    capabilities VARCHAR(100) COMMENT '支持能力: CHAT,SQL,CODE,EMBEDDING',
    priority INT DEFAULT 0 COMMENT '优先级',
    status INT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    tenant_id VARCHAR(32) COMMENT '租户ID',
    create_by VARCHAR(32) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(32) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(1) DEFAULT '0' COMMENT '删除标志',
    delete_time DATETIME COMMENT '删除时间',
    INDEX idx_model_type (model_type),
    INDEX idx_status (status),
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='模型配置表';

-- 2. Agent会话表
CREATE TABLE IF NOT EXISTS ai_agent_session (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(200) COMMENT '会话标题',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    tenant_id VARCHAR(32) NOT NULL COMMENT '租户ID',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/ARCHIVED/DELETED',
    model_id VARCHAR(32) COMMENT '使用的模型ID',
    context JSON COMMENT '会话上下文',
    message_count INT DEFAULT 0 COMMENT '消息数量',
    last_active_time DATETIME COMMENT '最后活跃时间',
    create_by VARCHAR(32) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(32) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(1) DEFAULT '0' COMMENT '删除标志',
    delete_time DATETIME COMMENT '删除时间',
    INDEX idx_user_id (user_id),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_status (status),
    INDEX idx_last_active_time (last_active_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Agent会话表';

-- 3. Agent消息表
CREATE TABLE IF NOT EXISTS ai_agent_message (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    session_id VARCHAR(32) NOT NULL COMMENT '会话ID',
    role VARCHAR(20) NOT NULL COMMENT '角色: USER/ASSISTANT/SYSTEM/TOOL',
    content TEXT COMMENT '消息内容',
    content_type VARCHAR(20) DEFAULT 'TEXT' COMMENT '类型: TEXT/SQL/TABLE/CHART/ERROR',
    tool_calls JSON COMMENT '工具调用记录',
    token_usage JSON COMMENT 'Token消耗',
    knowledge_refs JSON COMMENT '引用的知识ID',
    parent_message_id VARCHAR(32) COMMENT '父消息ID',
    create_by VARCHAR(32) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(32) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(1) DEFAULT '0' COMMENT '删除标志',
    delete_time DATETIME COMMENT '删除时间',
    INDEX idx_session_id (session_id),
    INDEX idx_role (role),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Agent消息表';

-- 4. 知识库表
CREATE TABLE IF NOT EXISTS ai_knowledge (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    knowledge_type VARCHAR(20) NOT NULL COMMENT '类型: STANDARD/TEMPLATE/FAQ/METADATA',
    title VARCHAR(500) COMMENT '知识标题',
    content TEXT COMMENT '知识内容',
    vector_id VARCHAR(100) COMMENT '向量ID',
    tags JSON COMMENT '标签列表',
    source VARCHAR(100) COMMENT '来源',
    valid_from DATETIME COMMENT '有效期开始',
    valid_to DATETIME COMMENT '有效期结束',
    tenant_id VARCHAR(32) COMMENT '租户ID(空为公共知识)',
    create_by VARCHAR(32) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(32) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(1) DEFAULT '0' COMMENT '删除标志',
    delete_time DATETIME COMMENT '删除时间',
    INDEX idx_type (knowledge_type),
    INDEX idx_tenant_id (tenant_id),
    FULLTEXT INDEX ft_title_content (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库表';

-- 5. 工具定义表
CREATE TABLE IF NOT EXISTS ai_tool_definition (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    tool_name VARCHAR(100) NOT NULL COMMENT '工具名称',
    tool_type VARCHAR(50) NOT NULL COMMENT '工具类型',
    description VARCHAR(500) COMMENT '工具描述',
    input_schema JSON COMMENT '输入参数Schema',
    output_schema JSON COMMENT '输出参数Schema',
    executor_class VARCHAR(200) COMMENT '执行器类名',
    permission_code VARCHAR(100) COMMENT '所需权限码',
    status INT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_by VARCHAR(32) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(32) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(1) DEFAULT '0' COMMENT '删除标志',
    delete_time DATETIME COMMENT '删除时间',
    INDEX idx_tool_name (tool_name),
    INDEX idx_tool_type (tool_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工具定义表';

-- 6. API密钥表
CREATE TABLE IF NOT EXISTS ai_api_key (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    key_name VARCHAR(100) NOT NULL COMMENT '密钥名称',
    api_key VARCHAR(64) NOT NULL COMMENT 'API密钥(加密)',
    user_id VARCHAR(32) NOT NULL COMMENT '所属用户ID',
    tenant_id VARCHAR(32) NOT NULL COMMENT '租户ID',
    permissions JSON COMMENT '授权权限',
    rate_limit INT DEFAULT 100 COMMENT '速率限制(次/分钟)',
    expired_at DATETIME COMMENT '过期时间',
    last_used_at DATETIME COMMENT '最后使用时间',
    status INT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_by VARCHAR(32) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(32) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(1) DEFAULT '0' COMMENT '删除标志',
    delete_time DATETIME COMMENT '删除时间',
    UNIQUE INDEX uk_api_key (api_key),
    INDEX idx_user_id (user_id),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='API密钥表';

-- 7. Token用量统计表
CREATE TABLE IF NOT EXISTS ai_token_usage (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    session_id VARCHAR(32) COMMENT '会话ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    tenant_id VARCHAR(32) NOT NULL COMMENT '租户ID',
    model_id VARCHAR(32) NOT NULL COMMENT '模型ID',
    input_tokens INT DEFAULT 0 COMMENT '输入Token数',
    output_tokens INT DEFAULT 0 COMMENT '输出Token数',
    total_tokens INT DEFAULT 0 COMMENT '总Token数',
    cost_amount DECIMAL(10,4) DEFAULT 0 COMMENT '费用金额',
    usage_date DATE NOT NULL COMMENT '使用日期',
    create_by VARCHAR(32) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(32) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(1) DEFAULT '0' COMMENT '删除标志',
    delete_time DATETIME COMMENT '删除时间',
    INDEX idx_user_date (user_id, usage_date),
    INDEX idx_tenant_date (tenant_id, usage_date),
    INDEX idx_model_date (model_id, usage_date),
    INDEX idx_session_id (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Token用量统计表';

-- 8. Agent审计日志表
CREATE TABLE IF NOT EXISTS ai_agent_audit_log (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键ID',
    session_id VARCHAR(32) COMMENT '会话ID',
    message_id VARCHAR(32) COMMENT '消息ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    tenant_id VARCHAR(32) NOT NULL COMMENT '租户ID',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
    operation_desc VARCHAR(500) COMMENT '操作描述',
    request_content TEXT COMMENT '请求内容',
    response_content TEXT COMMENT '响应内容',
    tools_used JSON COMMENT '使用的工具',
    execution_time BIGINT COMMENT '执行时长(ms)',
    status VARCHAR(20) COMMENT '执行状态: SUCCESS/FAIL',
    error_msg TEXT COMMENT '错误信息',
    access_ip VARCHAR(50) COMMENT '访问IP',
    access_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    create_by VARCHAR(32) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(32) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_count INT DEFAULT 0 COMMENT '更新次数',
    delete_flag VARCHAR(1) DEFAULT '0' COMMENT '删除标志',
    delete_time DATETIME COMMENT '删除时间',
    INDEX idx_user_time (user_id, access_time),
    INDEX idx_tenant_time (tenant_id, access_time),
    INDEX idx_session (session_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Agent审计日志表';

-- ========================================
-- 初始化数据
-- ========================================

-- 初始化默认模型配置
INSERT INTO ai_model_config (id, model_name, model_type, api_endpoint, capabilities, priority, status, tenant_id, create_by, create_time, delete_flag) VALUES
('1', 'Claude 3.5 Sonnet', 'CLAUDE', 'https://api.anthropic.com', 'CHAT,SQL,CODE', 100, 1, NULL, 'system', NOW(), '0'),
('2', 'Claude 3 Opus', 'CLAUDE', 'https://api.anthropic.com', 'CHAT,SQL,CODE', 95, 1, NULL, 'system', NOW(), '0'),
('3', 'GPT-4o', 'GPT', 'https://api.openai.com', 'CHAT,SQL,CODE', 90, 1, NULL, 'system', NOW(), '0'),
('4', 'GPT-4 Turbo', 'GPT', 'https://api.openai.com', 'CHAT,SQL,CODE', 85, 1, NULL, 'system', NOW(), '0'),
('5', '通义千问-Max', 'QWEN', 'https://dashscope.aliyuncs.com', 'CHAT,SQL,CODE', 80, 1, NULL, 'system', NOW(), '0'),
('6', '通义千问-Plus', 'QWEN', 'https://dashscope.aliyuncs.com', 'CHAT,SQL,CODE', 75, 1, NULL, 'system', NOW(), '0'),
('7', 'DeepSeek-V3', 'QWEN', 'https://api.deepseek.com', 'CHAT,SQL,CODE', 70, 1, NULL, 'system', NOW(), '0'),
('8', 'Llama 3 70B', 'LOCAL', 'http://localhost:11434', 'CHAT,SQL', 50, 0, NULL, 'system', NOW(), '0'),
('9', 'Qwen2.5 72B', 'LOCAL', 'http://localhost:11434', 'CHAT,SQL', 45, 0, NULL, 'system', NOW(), '0');

-- 初始化工具定义
INSERT INTO ai_tool_definition (id, tool_name, tool_type, description, input_schema, output_schema, executor_class, permission_code, status, create_by, create_time, delete_flag) VALUES
-- SQL相关工具
('1', 'sql_generator', 'GENERATION', '根据自然语言描述生成SQL查询语句，支持复杂查询、多表关联、聚合统计等',
 '{"type":"object","properties":{"datasource_id":{"type":"string","description":"数据源ID"},"natural_language":{"type":"string","description":"自然语言描述的查询需求"},"table_hints":{"type":"array","items":{"type":"string"},"description":"可选的表名提示"}},"required":["datasource_id","natural_language"]}',
 '{"type":"object","properties":{"sql":{"type":"string","description":"生成的SQL语句"},"explanation":{"type":"string","description":"SQL解释"},"tables":{"type":"array","items":{"type":"string"},"description":"涉及的表名"}}}',
 'cn.healthcaredaas.datasphere.svc.agent.tools.executor.SqlGeneratorTool',
 'agent:open:sql', 1, 'system', NOW(), '0'),

('2', 'sql_executor', 'EXECUTION', '执行SQL查询并返回结果，仅支持SELECT查询，自动添加行数限制',
 '{"type":"object","properties":{"datasource_id":{"type":"string","description":"数据源ID"},"sql":{"type":"string","description":"要执行的SQL语句"},"limit":{"type":"integer","description":"返回行数限制，默认100"}},"required":["datasource_id","sql"]}',
 '{"type":"object","properties":{"columns":{"type":"array","items":{"type":"string"}},"rows":{"type":"array","items":{"type":"object"}},"total":{"type":"integer"},"executionTime":{"type":"integer"}}}',
 'cn.healthcaredaas.datasphere.svc.agent.tools.executor.SqlExecutionTool',
 'sql:execute', 1, 'system', NOW(), '0'),

-- 元数据查询工具
('3', 'metadata_query', 'QUERY', '查询数据源的元数据信息，包括数据库、表、字段等结构信息',
 '{"type":"object","properties":{"datasource_id":{"type":"string","description":"数据源ID"},"query_type":{"type":"string","enum":["DATABASES","TABLES","COLUMNS","ALL"],"description":"查询类型"},"table_name":{"type":"string","description":"表名(查询列时需要)"}},"required":["datasource_id","query_type"]}',
 '{"type":"object","properties":{"databases":{"type":"array"},"tables":{"type":"array"},"columns":{"type":"array"}}}',
 'cn.healthcaredaas.datasphere.svc.agent.tools.executor.MetadataQueryTool',
 'metadata:query', 1, 'system', NOW(), '0'),

-- 数据集成工具
('4', 'pipeline_generator', 'GENERATION', '根据需求描述生成数据集成管道配置，支持SeaTunnel引擎',
 '{"type":"object","properties":{"source_datasource_id":{"type":"string","description":"源数据源ID"},"target_datasource_id":{"type":"string","description":"目标数据源ID"},"description":{"type":"string","description":"数据同步需求描述"},"options":{"type":"object","properties":{"transform_rules":{"type":"array"},"filter_conditions":{"type":"array"}}}},"required":["source_datasource_id","target_datasource_id","description"]}',
 '{"type":"object","properties":{"pipelineConfig":{"type":"object"},"seatunnelConfig":{"type":"string"},"fieldMappings":{"type":"array"}}}',
 'cn.healthcaredaas.datasphere.svc.agent.tools.executor.PipelineGeneratorTool',
 'agent:open:pipeline', 1, 'system', NOW(), '0'),

-- 数据质量工具
('5', 'quality_rule_generator', 'GENERATION', '根据需求描述生成数据质量规则配置，支持完整性、唯一性、格式、值域等规则类型',
 '{"type":"object","properties":{"datasource_id":{"type":"string","description":"数据源ID"},"table_name":{"type":"string","description":"表名"},"description":{"type":"string","description":"质量规则需求描述"},"auto_create":{"type":"boolean","description":"是否自动创建规则"}},"required":["datasource_id","table_name","description"]}',
 '{"type":"object","properties":{"rules":{"type":"array","items":{"type":"object"}},"sql_templates":{"type":"array"}}}',
 'cn.healthcaredaas.datasphere.svc.agent.tools.executor.QualityRuleGeneratorTool',
 'agent:open:quality', 1, 'system', NOW(), '0'),

-- 数据源查询工具
('6', 'datasource_query', 'QUERY', '查询数据源列表和详情',
 '{"type":"object","properties":{"query_type":{"type":"string","enum":["LIST","DETAIL","TYPES"],"description":"查询类型"},"datasource_id":{"type":"string","description":"数据源ID(详情查询时需要)"}},"required":["query_type"]}',
 '{"type":"object","properties":{"datasources":{"type":"array"},"types":{"type":"array"}}}',
 'cn.healthcaredaas.datasphere.svc.agent.tools.executor.DatasourceQueryTool',
 'ds:view', 1, 'system', NOW(), '0'),

-- 标准映射工具
('7', 'standard_mapping', 'GENERATION', '生成数据标准映射配置，支持HL7、FHIR等医疗标准',
 '{"type":"object","properties":{"source_fields":{"type":"array","items":{"type":"object"}},"target_standard":{"type":"string","description":"目标标准: HL7/FHIR/互联互通"},"description":{"type":"string","description":"映射需求描述"}},"required":["source_fields","target_standard"]}',
 '{"type":"object","properties":{"mappings":{"type":"array"},"transformRules":{"type":"array"}}}',
 'cn.healthcaredaas.datasphere.svc.agent.tools.executor.StandardMappingTool',
 'standard:mapping', 1, 'system', NOW(), '0'),

-- 诊断分析工具
('8', 'diagnostic_analyzer', 'ANALYSIS', '分析数据集成、质量检测等问题，提供诊断报告和解决方案',
 '{"type":"object","properties":{"problem_type":{"type":"string","enum":["SYNC_FAILURE","QUALITY_ISSUE","CONNECTION_ERROR","PERFORMANCE"]},"context":{"type":"object","description":"问题上下文信息"}},"required":["problem_type"]}',
 '{"type":"object","properties":{"diagnosis":{"type":"string"},"rootCause":{"type":"string"},"solutions":{"type":"array"}}}',
 'cn.healthcaredaas.datasphere.svc.agent.tools.executor.DiagnosticTool',
 'agent:open:diagnostic', 1, 'system', NOW(), '0');

-- 初始化知识库数据
INSERT INTO ai_knowledge (id, knowledge_type, title, content, tags, source, tenant_id, create_by, create_time, delete_flag) VALUES
-- 元数据知识
('1', 'METADATA', '患者基本信息表结构', '患者基本信息表(patient_info)包含患者唯一标识、姓名、性别、出生日期、身份证号、联系电话等基本信息。主键为患者ID(patient_id)。', '["患者","基本信息","患者ID"]', '系统元数据', NULL, 'system', NOW(), '0'),
('2', 'METADATA', '门诊挂号表结构', '门诊挂号表(outp_visit)记录患者门诊挂号信息，包含挂号ID、患者ID、科室代码、医生代码、挂号时间、挂号类型等字段。', '["门诊","挂号","就诊"]', '系统元数据', NULL, 'system', NOW(), '0'),
('3', 'METADATA', '住院记录表结构', '住院记录表(inp_visit)记录患者住院信息，包含住院ID、患者ID、入院科室、入院时间、出院时间、住院天数等字段。', '["住院","病历","入院"]', '系统元数据', NULL, 'system', NOW(), '0'),
('4', 'METADATA', '检验结果表结构', '检验结果表(lab_result)记录患者检验结果，包含检验ID、患者ID、检验项目代码、检验结果、参考范围、检验时间等字段。', '["检验","LIS","结果"]', '系统元数据', NULL, 'system', NOW(), '0'),

-- 规则模板知识
('5', 'TEMPLATE', '身份证号格式校验规则', '身份证号格式校验规则：验证身份证号是否符合18位格式，前17位为数字，第18位为数字或X。规则表达式：REGEXP(id_card, ''^[1-9][0-9]{16}[0-9Xx]$\')', '["身份证","格式校验","规则模板"]', '内置规则模板', NULL, 'system', NOW(), '0'),
('6', 'TEMPLATE', '手机号格式校验规则', '手机号格式校验规则：验证手机号是否符合11位格式，以1开头。规则表达式：REGEXP(mobile, ''^1[3-9][0-9]{9}$\')', '["手机号","格式校验","规则模板"]', '内置规则模板', NULL, 'system', NOW(), '0'),
('7', 'TEMPLATE', '日期范围校验规则', '日期范围校验规则：验证日期是否在合理范围内，如出生日期不能晚于今天，不能早于1900年。', '["日期","范围校验","规则模板"]', '内置规则模板', NULL, 'system', NOW(), '0'),
('8', 'TEMPLATE', '值域校验规则', '值域校验规则：验证字段值是否在允许的值域范围内，如性别只能为"男"或"女"。', '["值域","枚举","规则模板"]', '内置规则模板', NULL, 'system', NOW(), '0'),
('9', 'TEMPLATE', '唯一性校验规则', '唯一性校验规则：验证字段值在表中是否唯一，如患者ID、身份证号等。', '["唯一性","重复校验","规则模板"]', '内置规则模板', NULL, 'system', NOW(), '0'),
('10', 'TEMPLATE', '完整性校验规则', '完整性校验规则：验证必填字段是否为空，如患者姓名、性别、出生日期等。', '["完整性","必填","规则模板"]', '内置规则模板', NULL, 'system', NOW(), '0'),

-- FAQ知识
('11', 'FAQ', '如何创建数据源连接', '创建数据源连接步骤：1.进入数据源管理模块；2.点击新增数据源；3.选择数据源类型；4.填写连接信息(主机、端口、数据库名、用户名、密码)；5.测试连接；6.保存数据源。', '["数据源","创建","连接"]', '用户手册', NULL, 'system', NOW(), '0'),
('12', 'FAQ', '如何配置数据同步管道', '配置数据同步管道步骤：1.进入数据集成模块；2.创建数据项目；3.新建数据管道；4.配置源连接器(选择源表)；5.配置转换规则(字段映射、过滤条件)；6.配置目标连接器；7.发布并启动管道。', '["数据集成","管道","同步"]', '用户手册', NULL, 'system', NOW(), '0'),
('13', 'FAQ', '如何创建质量检测规则', '创建质量检测规则步骤：1.进入数据质量模块；2.选择质量规则菜单；3.点击新增规则；4.选择数据源和表；5.选择规则类型；6.配置规则参数；7.保存并启用规则。', '["数据质量","规则","检测"]', '用户手册', NULL, 'system', NOW(), '0'),
('14', 'FAQ', '如何使用AI助手生成SQL', '使用AI助手生成SQL：1.打开AI智能助手；2.创建或选择一个会话；3.用自然语言描述查询需求，如"查询最近一个月门诊量前10的科室"；4.AI助手会自动生成对应的SQL语句；5.可以进一步编辑或直接执行SQL。', '["AI","SQL","自然语言"]', '用户手册', NULL, 'system', NOW(), '0'),

-- 标准规范知识
('15', 'STANDARD', 'HL7 V2消息结构说明', 'HL7 V2是一种医疗信息交换标准，常见消息类型包括：ADT(患者入院/出院/转科)、ORU(检验结果)、ORM(医嘱)、SIU(预约)等。每条消息由多个段(Segment)组成，每个段由多个字段(Field)组成。', '["HL7","标准","消息"]', '医疗标准', NULL, 'system', NOW(), '0'),
('16', 'STANDARD', 'FHIR资源类型说明', 'FHIR(Fast Healthcare Interoperability Resources)是新一代医疗信息交换标准。核心资源包括：Patient(患者)、Encounter(就诊)、Observation(检验检查)、Medication(药品)、Condition(诊断)等。', '["FHIR","资源","标准"]', '医疗标准', NULL, 'system', NOW(), '0'),
('17', 'STANDARD', '互联互通标准数据元', '互联互通标准定义了医疗数据交换的数据元规范，包括：患者标识、就诊信息、检验报告、处方医嘱等数据元的编码规则和数据格式要求。', '["互联互通","数据元","标准"]', '医疗标准', NULL, 'system', NOW(), '0');