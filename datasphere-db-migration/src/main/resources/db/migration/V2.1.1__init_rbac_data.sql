-- ========================================
-- DataSphere RBAC 系统初始化脚本
-- 版本: V2.1.0
-- 日期: 2026-03-09
-- 包含: 应用管理、资源菜单、角色权限初始化
-- ========================================

-- ========================================
-- 1. 系统应用数据 (mgmt_application)
-- ========================================

-- 清空现有应用数据
-- TRUNCATE TABLE mgmt_application;

-- 根应用(平台入口)
INSERT INTO mgmt_application (id, parent_id, app_code, app_name, app_name_en, app_abbr, app_icon, app_path, is_micro_app, app_entry, api_prefix, status, app_version, memo, order_no, tenant_id, create_by, create_time, delete_flag) VALUES
('1', '0', 'datasphere', 'DataSphere数据中台', 'DataSphere', 'DS', 'datasphere', '/', 0, '/', '', 1, '2.1.0', '医疗数据中台平台', 0, NULL, 'system', NOW(), '0');

-- 主门户应用
INSERT INTO mgmt_application (id, parent_id, app_code, app_name, app_name_en, app_abbr, app_icon, app_path, is_micro_app, app_entry, api_prefix, status, app_version, memo, order_no, tenant_id, create_by, create_time, delete_flag) VALUES
('10', '1', 'portal', '工作台', 'Portal', 'Portal', 'portal', '/portal', 1, 'http://localhost:1800', '/api/portal', 1, '2.1.0', '系统门户工作台', 1, NULL, 'system', NOW(), '0');

-- 基础管理应用
INSERT INTO mgmt_application (id, parent_id, app_code, app_name, app_name_en, app_abbr, app_icon, app_path, is_micro_app, app_entry, api_prefix, status, app_version, memo, order_no, tenant_id, create_by, create_time, delete_flag) VALUES
('11', '1', 'base', '系统管理', 'Base', 'Base', 'setting', '/base', 1, 'http://localhost:1801', '/api/foundation', 1, '2.1.0', '系统基础管理模块', 2, NULL, 'system', NOW(), '0');

-- 数据源管理应用
INSERT INTO mgmt_application (id, parent_id, app_code, app_name, app_name_en, app_abbr, app_icon, app_path, is_micro_app, app_entry, api_prefix, status, app_version, memo, order_no, tenant_id, create_by, create_time, delete_flag) VALUES
('12', '1', 'database', '数据源管理', 'Database', 'DB', 'database', '/database', 1, 'http://localhost:1802', '/api/data', 1, '2.1.0', '数据源注册与管理', 3, NULL, 'system', NOW(), '0');

-- 数据集成应用
INSERT INTO mgmt_application (id, parent_id, app_code, app_name, app_name_en, app_abbr, app_icon, app_path, is_micro_app, app_entry, api_prefix, status, app_version, memo, order_no, tenant_id, create_by, create_time, delete_flag) VALUES
('13', '1', 'integration', '数据集成', 'Integration', 'DI', 'integration', '/integration', 1, 'http://localhost:1803', '/api/integration', 1, '2.1.0', '数据集成与同步', 4, NULL, 'system', NOW(), '0');

-- 主数据管理应用
INSERT INTO mgmt_application (id, parent_id, app_code, app_name, app_name_en, app_abbr, app_icon, app_path, is_micro_app, app_entry, api_prefix, status, app_version, memo, order_no, tenant_id, create_by, create_time, delete_flag) VALUES
('14', '1', 'masterdata', '主数据管理', 'MasterData', 'MD', 'master-data', '/masterdata', 1, 'http://localhost:1804', '/api/md', 1, '2.1.0', '主数据管理模块', 5, NULL, 'system', NOW(), '0');

-- 数据标准应用
INSERT INTO mgmt_application (id, parent_id, app_code, app_name, app_name_en, app_abbr, app_icon, app_path, is_micro_app, app_entry, api_prefix, status, app_version, memo, order_no, tenant_id, create_by, create_time, delete_flag) VALUES
('15', '1', 'normalize', '数据标准', 'Normalize', 'DN', 'normalize', '/normalize', 1, 'http://localhost:1805', '/api/normalize', 1, '2.1.0', '数据标准管理模块', 6, NULL, 'system', NOW(), '0');

-- 数据质量应用
INSERT INTO mgmt_application (id, parent_id, app_code, app_name, app_name_en, app_abbr, app_icon, app_path, is_micro_app, app_entry, api_prefix, status, app_version, memo, order_no, tenant_id, create_by, create_time, delete_flag) VALUES
('16', '1', 'quality', '数据质量', 'Quality', 'DQ', 'quality', '/quality', 1, 'http://localhost:1806', '/api/quality', 1, '2.1.0', '数据质量管理模块', 7, NULL, 'system', NOW(), '0');

-- HIE医疗信息交换应用
INSERT INTO mgmt_application (id, parent_id, app_code, app_name, app_name_en, app_abbr, app_icon, app_path, is_micro_app, app_entry, api_prefix, status, app_version, memo, order_no, tenant_id, create_by, create_time, delete_flag) VALUES
('17', '1', 'hie', '医疗信息交换', 'HIE', 'HIE', 'hie', '/hie', 1, 'http://localhost:1807', '/api/hie', 1, '2.1.0', '医疗信息交换模块', 8, NULL, 'system', NOW(), '0');

-- 数据服务应用
INSERT INTO mgmt_application (id, parent_id, app_code, app_name, app_name_en, app_abbr, app_icon, app_path, is_micro_app, app_entry, api_prefix, status, app_version, memo, order_no, tenant_id, create_by, create_time, delete_flag) VALUES
('18', '1', 'data-service', '数据服务', 'DataService', 'DS', 'api', '/data-service', 1, 'http://localhost:1808', '/api/data-service', 1, '2.1.0', '数据服务管理模块', 9, NULL, 'system', NOW(), '0');

-- 可视化应用
INSERT INTO mgmt_application (id, parent_id, app_code, app_name, app_name_en, app_abbr, app_icon, app_path, is_micro_app, app_entry, api_prefix, status, app_version, memo, order_no, tenant_id, create_by, create_time, delete_flag) VALUES
('19', '1', 'visualization', '数据可视化', 'Visualization', 'VIS', 'chart', '/visualization', 1, 'http://localhost:1809', '/api/visualization', 1, '2.1.0', '数据可视化模块', 10, NULL, 'system', NOW(), '0');

-- 模板管理应用
INSERT INTO mgmt_application (id, parent_id, app_code, app_name, app_name_en, app_abbr, app_icon, app_path, is_micro_app, app_entry, api_prefix, status, app_version, memo, order_no, tenant_id, create_by, create_time, delete_flag) VALUES
('20', '1', 'template', '模板管理', 'Template', 'TPL', 'template', '/template', 1, 'http://localhost:1810', '/api/template', 1, '2.1.0', '模板管理模块', 11, NULL, 'system', NOW(), '0');

-- AI Agent应用
INSERT INTO mgmt_application (id, parent_id, app_code, app_name, app_name_en, app_abbr, app_icon, app_path, is_micro_app, app_entry, api_prefix, status, app_version, memo, order_no, tenant_id, create_by, create_time, delete_flag) VALUES
('21', '1', 'agent', 'AI智能助手', 'AIAgent', 'AI', 'robot', '/agent', 1, 'http://localhost:1811', '/api/agent', 1, '2.1.0', 'AI Agent智能助手模块', 12, NULL, 'system', NOW(), '0');


-- ========================================
-- 2. RBAC资源数据 (rbac_resource)
-- ========================================

-- 清空现有资源数据
-- TRUNCATE TABLE rbac_resource;

-- ====================
-- 2.1 系统管理模块资源 (base)
-- ====================

-- 系统管理菜单
INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
-- 一级菜单
('base_1', '11', '0', 'system', '系统管理', 'MENU', NULL, '/system', 'setting', 1, 0, 'Layout', '/system', NULL, 'system', NOW(), '0'),

-- 二级菜单
('base_1_1', '11', 'base_1', 'user_manage', '用户管理', 'MENU', NULL, '/system/user', 'user', 1, 0, 'views/rbac/user/index', '/system/user', NULL, 'system', NOW(), '0'),
('base_1_2', '11', 'base_1', 'role_manage', '角色管理', 'MENU', NULL, '/system/role', 'peoples', 2, 0, 'views/rbac/role/index', '/system/role', NULL, 'system', NOW(), '0'),
('base_1_3', '11', 'base_1', 'resource_manage', '资源管理', 'MENU', NULL, '/system/resource', 'tree-table', 3, 0, 'views/rbac/resource/index', '/system/resource', NULL, 'system', NOW(), '0'),
('base_1_4', '11', 'base_1', 'client_manage', '客户端管理', 'MENU', NULL, '/system/client', 'client', 4, 0, 'views/rbac/client/index', '/system/client', NULL, 'system', NOW(), '0'),
('base_1_5', '11', 'base_1', 'application_manage', '应用管理', 'MENU', NULL, '/system/application', 'application', 5, 0, 'views/mgmt/application/index', '/system/application', NULL, 'system', NOW(), '0'),
('base_1_6', '11', 'base_1', 'dict_manage', '字典管理', 'MENU', NULL, '/system/dict', 'dict', 6, 0, 'views/mgmt/dict/index', '/system/dict', NULL, 'system', NOW(), '0'),
('base_1_7', '11', 'base_1', 'property_manage', '属性配置', 'MENU', NULL, '/system/property', 'edit', 7, 0, 'views/mgmt/property/index', '/system/property', NULL, 'system', NOW(), '0'),
('base_1_8', '11', 'base_1', 'audit_log', '审计日志', 'MENU', NULL, '/system/audit', 'log', 8, 0, 'views/audit/log/index', '/system/audit', NULL, 'system', NOW(), '0');

-- 用户管理权限
INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
('base_1_1_1', '11', 'base_1_1', 'user:view', '查看用户', 'BUTTON', 'GET', '/api/foundation/rbac/user/**', NULL, 1, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('base_1_1_2', '11', 'base_1_1', 'user:add', '新增用户', 'BUTTON', 'POST', '/api/foundation/rbac/user', NULL, 2, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('base_1_1_3', '11', 'base_1_1', 'user:edit', '编辑用户', 'BUTTON', 'PUT', '/api/foundation/rbac/user/**', NULL, 3, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('base_1_1_4', '11', 'base_1_1', 'user:delete', '删除用户', 'BUTTON', 'DELETE', '/api/foundation/rbac/user/**', NULL, 4, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('base_1_1_5', '11', 'base_1_1', 'user:resetPwd', '重置密码', 'BUTTON', 'PUT', '/api/foundation/rbac/user/resetPwd/**', NULL, 5, 0, NULL, NULL, NULL, 'system', NOW(), '0');

-- 角色管理权限
INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
('base_1_2_1', '11', 'base_1_2', 'role:view', '查看角色', 'BUTTON', 'GET', '/api/foundation/rbac/role/**', NULL, 1, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('base_1_2_2', '11', 'base_1_2', 'role:add', '新增角色', 'BUTTON', 'POST', '/api/foundation/rbac/role', NULL, 2, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('base_1_2_3', '11', 'base_1_2', 'role:edit', '编辑角色', 'BUTTON', 'PUT', '/api/foundation/rbac/role/**', NULL, 3, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('base_1_2_4', '11', 'base_1_2', 'role:delete', '删除角色', 'BUTTON', 'DELETE', '/api/foundation/rbac/role/**', NULL, 4, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('base_1_2_5', '11', 'base_1_2', 'role:assignResource', '分配权限', 'BUTTON', 'POST', '/api/foundation/rbac/role/authority/**', NULL, 5, 0, NULL, NULL, NULL, 'system', NOW(), '0');

-- 资源管理权限
INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
('base_1_3_1', '11', 'base_1_3', 'resource:view', '查看资源', 'BUTTON', 'GET', '/api/foundation/rbac/resource/**', NULL, 1, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('base_1_3_2', '11', 'base_1_3', 'resource:add', '新增资源', 'BUTTON', 'POST', '/api/foundation/rbac/resource', NULL, 2, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('base_1_3_3', '11', 'base_1_3', 'resource:edit', '编辑资源', 'BUTTON', 'PUT', '/api/foundation/rbac/resource/**', NULL, 3, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('base_1_3_4', '11', 'base_1_3', 'resource:delete', '删除资源', 'BUTTON', 'DELETE', '/api/foundation/rbac/resource/**', NULL, 4, 0, NULL, NULL, NULL, 'system', NOW(), '0');


-- ====================
-- 2.2 数据源管理模块资源 (database)
-- ====================

INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
-- 一级菜单
('db_1', '12', '0', 'datasource', '数据源', 'MENU', NULL, '/datasource', 'database', 2, 0, 'Layout', '/datasource', NULL, 'system', NOW(), '0'),

-- 二级菜单
('db_1_1', '12', 'db_1', 'ds_classify', '数据源分类', 'MENU', NULL, '/datasource/classify', 'classify', 1, 0, 'views/datasource/classify/index', '/datasource/classify', NULL, 'system', NOW(), '0'),
('db_1_2', '12', 'db_1', 'ds_type', '数据源类型', 'MENU', NULL, '/datasource/type', 'type', 2, 0, 'views/datasource/type/index', '/datasource/type', NULL, 'system', NOW(), '0'),
('db_1_3', '12', 'db_1', 'ds_info', '数据源信息', 'MENU', NULL, '/datasource/info', 'info', 3, 0, 'views/datasource/info/index', '/datasource/info', NULL, 'system', NOW(), '0');

-- 数据源信息权限
INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
('db_1_3_1', '12', 'db_1_3', 'ds:view', '查看数据源', 'BUTTON', 'GET', '/api/data/dsInfo/**', NULL, 1, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('db_1_3_2', '12', 'db_1_3', 'ds:add', '新增数据源', 'BUTTON', 'POST', '/api/data/dsInfo', NULL, 2, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('db_1_3_3', '12', 'db_1_3', 'ds:edit', '编辑数据源', 'BUTTON', 'PUT', '/api/data/dsInfo/**', NULL, 3, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('db_1_3_4', '12', 'db_1_3', 'ds:delete', '删除数据源', 'BUTTON', 'DELETE', '/api/data/dsInfo/**', NULL, 4, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('db_1_3_5', '12', 'db_1_3', 'ds:testConnection', '测试连接', 'BUTTON', 'POST', '/api/data/dsInfo/testConnection/**', NULL, 5, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('db_1_3_6', '12', 'db_1_3', 'ds:syncMetadata', '同步元数据', 'BUTTON', 'POST', '/api/data/dsInfo/syncMetadata/**', NULL, 6, 0, NULL, NULL, NULL, 'system', NOW(), '0');


-- ====================
-- 2.3 数据集成模块资源 (integration)
-- ====================

INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
-- 一级菜单
('di_1', '13', '0', 'integration', '数据集成', 'MENU', NULL, '/integration', 'integration', 3, 0, 'Layout', '/integration', NULL, 'system', NOW(), '0'),

-- 二级菜单
('di_1_1', '13', 'di_1', 'di_project', '数据项目', 'MENU', NULL, '/integration/project', 'project', 1, 0, 'views/integration/project/index', '/integration/project', NULL, 'system', NOW(), '0'),
('di_1_2', '13', 'di_1', 'di_pipeline', '数据管道', 'MENU', NULL, '/integration/pipeline', 'pipeline', 2, 0, 'views/integration/pipeline/index', '/integration/pipeline', NULL, 'system', NOW(), '0'),
('di_1_3', '13', 'di_1', 'di_job', '作业管理', 'MENU', NULL, '/integration/job', 'job', 3, 0, 'views/integration/job/index', '/integration/job', NULL, 'system', NOW(), '0'),
('di_1_4', '13', 'di_1', 'di_job_log', '作业日志', 'MENU', NULL, '/integration/log', 'log', 4, 0, 'views/integration/log/index', '/integration/log', NULL, 'system', NOW(), '0'),
('di_1_5', '13', 'di_1', 'di_connector', '连接器类型', 'MENU', NULL, '/integration/connector', 'connector', 5, 0, 'views/integration/connector/index', '/integration/connector', NULL, 'system', NOW(), '0');

-- 数据管道权限
INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
('di_1_2_1', '13', 'di_1_2', 'pipeline:view', '查看管道', 'BUTTON', 'GET', '/api/integration/pipeline/**', NULL, 1, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('di_1_2_2', '13', 'di_1_2', 'pipeline:add', '新增管道', 'BUTTON', 'POST', '/api/integration/pipeline', NULL, 2, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('di_1_2_3', '13', 'di_1_2', 'pipeline:edit', '编辑管道', 'BUTTON', 'PUT', '/api/integration/pipeline/**', NULL, 3, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('di_1_2_4', '13', 'di_1_2', 'pipeline:delete', '删除管道', 'BUTTON', 'DELETE', '/api/integration/pipeline/**', NULL, 4, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('di_1_2_5', '13', 'di_1_2', 'pipeline:deploy', '发布管道', 'BUTTON', 'POST', '/api/integration/pipeline/deploy/**', NULL, 5, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('di_1_2_6', '13', 'di_1_2', 'pipeline:start', '启动管道', 'BUTTON', 'POST', '/api/integration/pipeline/start/**', NULL, 6, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('di_1_2_7', '13', 'di_1_2', 'pipeline:stop', '停止管道', 'BUTTON', 'POST', '/api/integration/pipeline/stop/**', NULL, 7, 0, NULL, NULL, NULL, 'system', NOW(), '0');


-- ====================
-- 2.4 主数据管理模块资源 (masterdata)
-- ====================

INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
-- 一级菜单
('md_1', '14', '0', 'masterdata', '主数据', 'MENU', NULL, '/masterdata', 'master-data', 4, 0, 'Layout', '/masterdata', NULL, 'system', NOW(), '0'),

-- 二级菜单
('md_1_1', '14', 'md_1', 'md_organization', '组织机构', 'MENU', NULL, '/masterdata/organization', 'organization', 1, 0, 'views/md/organization/index', '/masterdata/organization', NULL, 'system', NOW(), '0'),
('md_1_2', '14', 'md_1', 'md_department', '科室管理', 'MENU', NULL, '/masterdata/department', 'department', 2, 0, 'views/md/department/index', '/masterdata/department', NULL, 'system', NOW(), '0'),
('md_1_3', '14', 'md_1', 'md_practitioner', '从业人员', 'MENU', NULL, '/masterdata/practitioner', 'peoples', 3, 0, 'views/md/practitioner/index', '/masterdata/practitioner', NULL, 'system', NOW(), '0'),
('md_1_4', '14', 'md_1', 'md_code_system', '代码体系', 'MENU', NULL, '/masterdata/codeSystem', 'code', 4, 0, 'views/md/codeSystem/index', '/masterdata/codeSystem', NULL, 'system', NOW(), '0'),
('md_1_5', '14', 'md_1', 'md_affiliation', '隶属关系', 'MENU', NULL, '/masterdata/affiliation', 'affiliation', 5, 0, 'views/md/affiliation/index', '/masterdata/affiliation', NULL, 'system', NOW(), '0');


-- ====================
-- 2.5 数据标准模块资源 (normalize)
-- ====================

INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
-- 一级菜单
('dn_1', '15', '0', 'normalize', '数据标准', 'MENU', NULL, '/normalize', 'normalize', 5, 0, 'Layout', '/normalize', NULL, 'system', NOW(), '0'),

-- 二级菜单
('dn_1_1', '15', 'dn_1', 'dn_dataset', '数据集', 'MENU', NULL, '/normalize/dataset', 'dataset', 1, 0, 'views/normalize/dataset/index', '/normalize/dataset', NULL, 'system', NOW(), '0'),
('dn_1_2', '15', 'dn_1', 'dn_element', '数据元', 'MENU', NULL, '/normalize/element', 'element', 2, 0, 'views/normalize/element/index', '/normalize/element', NULL, 'system', NOW(), '0'),
('dn_1_3', '15', 'dn_1', 'dn_indicator', '指标管理', 'MENU', NULL, '/normalize/indicator', 'indicator', 3, 0, 'views/normalize/indicator/index', '/normalize/indicator', NULL, 'system', NOW(), '0'),
('dn_1_4', '15', 'dn_1', 'dn_oid', 'OID管理', 'MENU', NULL, '/normalize/oid', 'oid', 4, 0, 'views/normalize/oid/index', '/normalize/oid', NULL, 'system', NOW(), '0'),
('dn_1_5', '15', 'dn_1', 'dn_mapping', '标准映射', 'MENU', NULL, '/normalize/mapping', 'mapping', 5, 0, 'views/normalize/mapping/index', '/normalize/mapping', NULL, 'system', NOW(), '0');


-- ====================
-- 2.6 数据质量模块资源 (quality)
-- ====================

INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
-- 一级菜单
('dq_1', '16', '0', 'quality', '数据质量', 'MENU', NULL, '/quality', 'quality', 6, 0, 'Layout', '/quality', NULL, 'system', NOW(), '0'),

-- 二级菜单
('dq_1_1', '16', 'dq_1', 'dq_rule', '质量规则', 'MENU', NULL, '/quality/rule', 'rule', 1, 0, 'views/quality/rule/index', '/quality/rule', NULL, 'system', NOW(), '0'),
('dq_1_2', '16', 'dq_1', 'dq_task', '检测任务', 'MENU', NULL, '/quality/task', 'task', 2, 0, 'views/quality/task/index', '/quality/task', NULL, 'system', NOW(), '0'),
('dq_1_3', '16', 'dq_1', 'dq_report', '质量报告', 'MENU', NULL, '/quality/report', 'report', 3, 0, 'views/quality/report/index', '/quality/report', NULL, 'system', NOW(), '0'),
('dq_1_4', '16', 'dq_1', 'dq_template', '规则模板', 'MENU', NULL, '/quality/template', 'template', 4, 0, 'views/quality/template/index', '/quality/template', NULL, 'system', NOW(), '0');

-- 质量规则权限
INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
('dq_1_1_1', '16', 'dq_1_1', 'dq:rule:view', '查看规则', 'BUTTON', 'GET', '/api/quality/rule/**', NULL, 1, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('dq_1_1_2', '16', 'dq_1_1', 'dq:rule:add', '新增规则', 'BUTTON', 'POST', '/api/quality/rule', NULL, 2, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('dq_1_1_3', '16', 'dq_1_1', 'dq:rule:edit', '编辑规则', 'BUTTON', 'PUT', '/api/quality/rule/**', NULL, 3, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('dq_1_1_4', '16', 'dq_1_1', 'dq:rule:delete', '删除规则', 'BUTTON', 'DELETE', '/api/quality/rule/**', NULL, 4, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('dq_1_1_5', '16', 'dq_1_1', 'dq:rule:execute', '执行检测', 'BUTTON', 'POST', '/api/quality/rule/execute/**', NULL, 5, 0, NULL, NULL, NULL, 'system', NOW(), '0');


-- ====================
-- 2.7 医疗信息交换模块资源 (hie)
-- ====================

INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
-- 一级菜单
('hie_1', '17', '0', 'hie', '医疗信息交换', 'MENU', NULL, '/hie', 'hie', 7, 0, 'Layout', '/hie', NULL, 'system', NOW(), '0'),

-- 二级菜单
('hie_1_1', '17', 'hie_1', 'hie_patient', '患者信息', 'MENU', NULL, '/hie/patient', 'patient', 1, 0, 'views/hie/patient/index', '/hie/patient', NULL, 'system', NOW(), '0'),
('hie_1_2', '17', 'hie_1', 'hie_encounter', '就诊信息', 'MENU', NULL, '/hie/encounter', 'encounter', 2, 0, 'views/hie/encounter/index', '/hie/encounter', NULL, 'system', NOW(), '0'),
('hie_1_3', '17', 'hie_1', 'hie_observation', '检验检查', 'MENU', NULL, '/hie/observation', 'observation', 3, 0, 'views/hie/observation/index', '/hie/observation', NULL, 'system', NOW(), '0'),
('hie_1_4', '17', 'hie_1', 'hie_document', '文档交换', 'MENU', NULL, '/hie/document', 'document', 4, 0, 'views/hie/document/index', '/hie/document', NULL, 'system', NOW(), '0'),
('hie_1_5', '17', 'hie_1', 'hie_service', '交互服务', 'MENU', NULL, '/hie/service', 'service', 5, 0, 'views/hie/service/index', '/hie/service', NULL, 'system', NOW(), '0');


-- ====================
-- 2.8 数据服务模块资源 (data-service)
-- ====================

INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
-- 一级菜单
('ds_1', '18', '0', 'data-service', '数据服务', 'MENU', NULL, '/data-service', 'api', 8, 0, 'Layout', '/data-service', NULL, 'system', NOW(), '0'),

-- 二级菜单
('ds_1_1', '18', 'ds_1', 'ds_api', 'API管理', 'MENU', NULL, '/data-service/api', 'api', 1, 0, 'views/data-service/api/index', '/data-service/api', NULL, 'system', NOW(), '0'),
('ds_1_2', '18', 'ds_1', 'ds_publish', '服务发布', 'MENU', NULL, '/data-service/publish', 'publish', 2, 0, 'views/data-service/publish/index', '/data-service/publish', NULL, 'system', NOW(), '0'),
('ds_1_3', '18', 'ds_1', 'ds_statistics', '调用统计', 'MENU', NULL, '/data-service/statistics', 'statistics', 3, 0, 'views/data-service/statistics/index', '/data-service/statistics', NULL, 'system', NOW(), '0');


-- ====================
-- 2.9 数据可视化模块资源 (visualization)
-- ====================

INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
-- 一级菜单
('vis_1', '19', '0', 'visualization', '数据可视化', 'MENU', NULL, '/visualization', 'chart', 9, 0, 'Layout', '/visualization', NULL, 'system', NOW(), '0'),

-- 二级菜单
('vis_1_1', '19', 'vis_1', 'vis_dashboard', '仪表盘', 'MENU', NULL, '/visualization/dashboard', 'dashboard', 1, 0, 'views/visualization/dashboard/index', '/visualization/dashboard', NULL, 'system', NOW(), '0'),
('vis_1_2', '19', 'vis_1', 'vis_chart', '图表管理', 'MENU', NULL, '/visualization/chart', 'chart', 2, 0, 'views/visualization/chart/index', '/visualization/chart', NULL, 'system', NOW(), '0'),
('vis_1_3', '19', 'vis_1', 'vis_report', '报表管理', 'MENU', NULL, '/visualization/report', 'report', 3, 0, 'views/visualization/report/index', '/visualization/report', NULL, 'system', NOW(), '0');


-- ====================
-- 2.10 模板管理模块资源 (template)
-- ====================

INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
-- 一级菜单
('tpl_1', '20', '0', 'template', '模板管理', 'MENU', NULL, '/template', 'template', 10, 0, 'Layout', '/template', NULL, 'system', NOW(), '0'),

-- 二级菜单
('tpl_1_1', '20', 'tpl_1', 'tpl_list', '模板列表', 'MENU', NULL, '/template/list', 'list', 1, 0, 'views/template/list/index', '/template/list', NULL, 'system', NOW(), '0'),
('tpl_1_2', '20', 'tpl_1', 'tpl_category', '模板分类', 'MENU', NULL, '/template/category', 'category', 2, 0, 'views/template/category/index', '/template/category', NULL, 'system', NOW(), '0');


-- ====================
-- 2.11 AI Agent智能助手模块资源 (agent)
-- ====================

INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
-- 一级菜单
('ai_1', '21', '0', 'agent', 'AI智能助手', 'MENU', NULL, '/agent', 'robot', 11, 0, 'Layout', '/agent', NULL, 'system', NOW(), '0'),

-- 二级菜单
('ai_1_1', '21', 'ai_1', 'ai_chat', '智能对话', 'MENU', NULL, '/agent/chat', 'chat', 1, 0, 'views/agent/chat/index', '/agent/chat', NULL, 'system', NOW(), '0'),
('ai_1_2', '21', 'ai_1', 'ai_session', '会话管理', 'MENU', NULL, '/agent/session', 'session', 2, 0, 'views/agent/session/index', '/agent/session', NULL, 'system', NOW(), '0'),
('ai_1_3', '21', 'ai_1', 'ai_knowledge', '知识库管理', 'MENU', NULL, '/agent/knowledge', 'knowledge', 3, 0, 'views/agent/knowledge/index', '/agent/knowledge', NULL, 'system', NOW(), '0'),
('ai_1_4', '21', 'ai_1', 'ai_model', '模型配置', 'MENU', NULL, '/agent/model', 'model', 4, 0, 'views/agent/model/index', '/agent/model', NULL, 'system', NOW(), '0'),
('ai_1_5', '21', 'ai_1', 'ai_tool', '工具管理', 'MENU', NULL, '/agent/tool', 'tool', 5, 0, 'views/agent/tool/index', '/agent/tool', NULL, 'system', NOW(), '0'),
('ai_1_6', '21', 'ai_1', 'ai_apikey', 'API密钥', 'MENU', NULL, '/agent/apikey', 'key', 6, 0, 'views/agent/apikey/index', '/agent/apikey', NULL, 'system', NOW(), '0'),
('ai_1_7', '21', 'ai_1', 'ai_usage', '用量统计', 'MENU', NULL, '/agent/usage', 'usage', 7, 0, 'views/agent/usage/index', '/agent/usage', NULL, 'system', NOW(), '0'),
('ai_1_8', '21', 'ai_1', 'ai_audit', '审计日志', 'MENU', NULL, '/agent/audit', 'audit', 8, 0, 'views/agent/audit/index', '/agent/audit', NULL, 'system', NOW(), '0');

-- 智能对话权限
INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
('ai_1_1_1', '21', 'ai_1_1', 'agent:chat:send', '发送消息', 'BUTTON', 'POST', '/api/agent/v1/sessions/**/messages', NULL, 1, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('ai_1_1_2', '21', 'ai_1_1', 'agent:chat:stream', '流式响应', 'BUTTON', 'POST', '/api/agent/v1/sessions/**/stream', NULL, 2, 0, NULL, NULL, NULL, 'system', NOW(), '0');

-- 会话管理权限
INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
('ai_1_2_1', '21', 'ai_1_2', 'agent:session:view', '查看会话', 'BUTTON', 'GET', '/api/agent/v1/sessions/**', NULL, 1, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('ai_1_2_2', '21', 'ai_1_2', 'agent:session:create', '创建会话', 'BUTTON', 'POST', '/api/agent/v1/sessions', NULL, 2, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('ai_1_2_3', '21', 'ai_1_2', 'agent:session:delete', '删除会话', 'BUTTON', 'DELETE', '/api/agent/v1/sessions/**', NULL, 3, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('ai_1_2_4', '21', 'ai_1_2', 'agent:session:archive', '归档会话', 'BUTTON', 'PUT', '/api/agent/v1/sessions/**/archive', NULL, 4, 0, NULL, NULL, NULL, 'system', NOW(), '0');

-- 知识库权限
INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
('ai_1_3_1', '21', 'ai_1_3', 'agent:knowledge:view', '查看知识', 'BUTTON', 'GET', '/api/agent/v1/knowledge/**', NULL, 1, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('ai_1_3_2', '21', 'ai_1_3', 'agent:knowledge:add', '新增知识', 'BUTTON', 'POST', '/api/agent/v1/knowledge', NULL, 2, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('ai_1_3_3', '21', 'ai_1_3', 'agent:knowledge:edit', '编辑知识', 'BUTTON', 'PUT', '/api/agent/v1/knowledge/**', NULL, 3, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('ai_1_3_4', '21', 'ai_1_3', 'agent:knowledge:delete', '删除知识', 'BUTTON', 'DELETE', '/api/agent/v1/knowledge/**', NULL, 4, 0, NULL, NULL, NULL, 'system', NOW(), '0');

-- 模型配置权限
INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
('ai_1_4_1', '21', 'ai_1_4', 'agent:model:view', '查看模型', 'BUTTON', 'GET', '/api/agent/v1/models/**', NULL, 1, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('ai_1_4_2', '21', 'ai_1_4', 'agent:model:add', '新增模型', 'BUTTON', 'POST', '/api/agent/v1/models', NULL, 2, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('ai_1_4_3', '21', 'ai_1_4', 'agent:model:edit', '编辑模型', 'BUTTON', 'PUT', '/api/agent/v1/models/**', NULL, 3, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('ai_1_4_4', '21', 'ai_1_4', 'agent:model:delete', '删除模型', 'BUTTON', 'DELETE', '/api/agent/v1/models/**', NULL, 4, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('ai_1_4_5', '21', 'ai_1_4', 'agent:model:test', '测试连接', 'BUTTON', 'POST', '/api/agent/v1/models/**/test', NULL, 5, 0, NULL, NULL, NULL, 'system', NOW(), '0');

-- API密钥权限
INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
('ai_1_6_1', '21', 'ai_1_6', 'agent:apikey:view', '查看密钥', 'BUTTON', 'GET', '/api/agent/v1/api-keys/**', NULL, 1, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('ai_1_6_2', '21', 'ai_1_6', 'agent:apikey:create', '创建密钥', 'BUTTON', 'POST', '/api/agent/v1/api-keys', NULL, 2, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('ai_1_6_3', '21', 'ai_1_6', 'agent:apikey:revoke', '吊销密钥', 'BUTTON', 'DELETE', '/api/agent/v1/api-keys/**', NULL, 3, 0, NULL, NULL, NULL, 'system', NOW(), '0');

-- 开放API权限
INSERT INTO rbac_resource (id, application_id, parent_id, resource_code, resource_name, resource_type, request_method, url, icon, order_no, hidden_menu, view_component, view_url, tenant_id, create_by, create_time, delete_flag) VALUES
('ai_open_1', '21', 'ai_1', 'agent:open:sql', 'SQL生成API', 'API', 'POST', '/api/agent/v1/open/sql/**', NULL, 1, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('ai_open_2', '21', 'ai_1', 'agent:open:pipeline', '管道生成API', 'API', 'POST', '/api/agent/v1/open/pipeline/**', NULL, 2, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('ai_open_3', '21', 'ai_1', 'agent:open:quality', '质量规则API', 'API', 'POST', '/api/agent/v1/open/quality/**', NULL, 3, 0, NULL, NULL, NULL, 'system', NOW(), '0'),
('ai_open_4', '21', 'ai_1', 'agent:open:diagnostic', '诊断API', 'API', 'POST', '/api/agent/v1/open/diagnostic/**', NULL, 4, 0, NULL, NULL, NULL, 'system', NOW(), '0');


-- ========================================
-- 3. 角色数据 (rbac_role)
-- ========================================

-- 清空现有角色数据
-- TRUNCATE TABLE rbac_role;

INSERT INTO rbac_role (id, role_code, role_name, description, tenant_id, create_by, create_time, delete_flag) VALUES
('1', 'SUPER_ADMIN', '超级管理员', '系统超级管理员，拥有所有权限', NULL, 'system', NOW(), '0'),
('2', 'ADMIN', '系统管理员', '系统管理员，拥有大部分权限', NULL, 'system', NOW(), '0'),
('3', 'DATA_ENGINEER', '数据工程师', '数据工程师，负责数据集成与质量', NULL, 'system', NOW(), '0'),
('4', 'DATA_ANALYST', '数据分析师', '数据分析师，负责数据分析与可视化', NULL, 'system', NOW(), '0'),
('5', 'BUSINESS_USER', '业务用户', '业务用户，使用平台功能', NULL, 'system', NOW(), '0'),
('6', 'AI_USER', 'AI助手用户', 'AI助手用户，使用Agent功能', NULL, 'system', NOW(), '0'),
('7', 'API_USER', 'API调用方', 'API调用方，通过API使用平台功能', NULL, 'system', NOW(), '0');


-- ========================================
-- 4. 角色权限关联 (rbac_role_authority)
-- ========================================

-- 超级管理员 - 拥有所有资源权限
INSERT INTO rbac_role_authority (id, role_id, authority_id, authority_type, tenant_id, create_by, create_time, delete_flag)
SELECT
    CONCAT('ra_super_', r.id) as id,
    '1' as role_id,
    r.id as authority_id,
    '1' as authority_type,
    NULL as tenant_id,
    'system' as create_by,
    NOW() as create_time,
    '0' as delete_flag
FROM rbac_resource r;

-- 系统管理员 - 拥有所有模块菜单权限和大部分操作权限
INSERT INTO rbac_role_authority (id, role_id, authority_id, authority_type, tenant_id, create_by, create_time, delete_flag)
SELECT
    CONCAT('ra_admin_', r.id) as id,
    '2' as role_id,
    r.id as authority_id,
    '1' as authority_type,
    NULL as tenant_id,
    'system' as create_by,
    NOW() as create_time,
    '0' as delete_flag
FROM rbac_resource r
WHERE r.resource_type IN ('MENU', 'BUTTON');

-- 数据工程师 - 数据集成、数据质量、数据源相关权限
INSERT INTO rbac_role_authority (id, role_id, authority_id, authority_type, tenant_id, create_by, create_time, delete_flag)
SELECT
    CONCAT('ra_engineer_', r.id) as id,
    '3' as role_id,
    r.id as authority_id,
    '1' as authority_type,
    NULL as tenant_id,
    'system' as create_by,
    NOW() as create_time,
    '0' as delete_flag
FROM rbac_resource r
WHERE r.id LIKE 'db_%' OR r.id LIKE 'di_%' OR r.id LIKE 'dq_%' OR r.id LIKE 'dn_%';

-- 数据分析师 - 数据可视化、数据标准相关权限
INSERT INTO rbac_role_authority (id, role_id, authority_id, authority_type, tenant_id, create_by, create_time, delete_flag)
SELECT
    CONCAT('ra_analyst_', r.id) as id,
    '4' as role_id,
    r.id as authority_id,
    '1' as authority_type,
    NULL as tenant_id,
    'system' as create_by,
    NOW() as create_time,
    '0' as delete_flag
FROM rbac_resource r
WHERE r.id LIKE 'vis_%' OR r.id LIKE 'dn_%' OR r.id LIKE 'ds_%';

-- 业务用户 - 基本查看权限
INSERT INTO rbac_role_authority (id, role_id, authority_id, authority_type, tenant_id, create_by, create_time, delete_flag)
SELECT
    CONCAT('ra_business_', r.id) as id,
    '5' as role_id,
    r.id as authority_id,
    '1' as authority_type,
    NULL as tenant_id,
    'system' as create_by,
    NOW() as create_time,
    '0' as delete_flag
FROM rbac_resource r
WHERE r.resource_type = 'MENU';

-- AI助手用户 - Agent模块权限
INSERT INTO rbac_role_authority (id, role_id, authority_id, authority_type, tenant_id, create_by, create_time, delete_flag)
SELECT
    CONCAT('ra_aiuser_', r.id) as id,
    '6' as role_id,
    r.id as authority_id,
    '1' as authority_type,
    NULL as tenant_id,
    'system' as create_by,
    NOW() as create_time,
    '0' as delete_flag
FROM rbac_resource r
WHERE r.id LIKE 'ai_%';

-- API调用方 - 开放API权限
INSERT INTO rbac_role_authority (id, role_id, authority_id, authority_type, tenant_id, create_by, create_time, delete_flag)
SELECT
    CONCAT('ra_api_', r.id) as id,
    '7' as role_id,
    r.id as authority_id,
    '1' as authority_type,
    NULL as tenant_id,
    'system' as create_by,
    NOW() as create_time,
    '0' as delete_flag
FROM rbac_resource r
WHERE r.id LIKE 'ai_open_%' OR r.resource_type = 'API';


-- ========================================
-- 5. 默认用户数据 (rbac_user)
-- ========================================

-- 管理员用户 (密码: admin123，使用BCrypt加密)
INSERT INTO rbac_user (id, username, password, real_name, email, mobile, status, tenant_id, create_by, create_time, delete_flag) VALUES
('1', 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'admin@datasphere.com', '13800138000', 1, NULL, 'system', NOW(), '0'),
('2', 'engineer', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '数据工程师', 'engineer@datasphere.com', '13800138001', 1, NULL, 'system', NOW(), '0'),
('3', 'analyst', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '数据分析师', 'analyst@datasphere.com', '13800138002', 1, NULL, 'system', NOW(), '0');


-- ========================================
-- 6. 用户角色关联 (rbac_user_role)
-- ========================================

INSERT INTO rbac_user_role (id, user_id, role_id, tenant_id, create_by, create_time, delete_flag) VALUES
('ur_1', '1', '1', NULL, 'system', NOW(), '0'),  -- admin -> 超级管理员
('ur_2', '2', '3', NULL, 'system', NOW(), '0'),  -- engineer -> 数据工程师
('ur_3', '3', '4', NULL, 'system', NOW(), '0');  -- analyst -> 数据分析师


-- ========================================
-- 初始化完成
-- ========================================