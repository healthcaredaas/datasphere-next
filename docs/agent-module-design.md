# DataSphere AI Agent 模块设计文档

## 版本信息

| 项目 | 内容 |
|------|------|
| 文档版本 | V1.0 |
| 编写日期 | 2026-03-09 |
| 项目名称 | DataSphere AI Agent 智能助手模块 |
| 目标版本 | datasphere-next 2.1.0 |

---

## 目录

1. [概述](#1-概述)
2. [技术架构](#2-技术架构)
3. [业务流程](#3-业务流程)
4. [接口设计](#4-接口设计)
5. [数据模型](#5-数据模型)
6. [LLM集成](#6-llm集成)
7. [工具系统](#7-工具系统)
8. [前端设计](#8-前端设计)
9. [安全设计](#9-安全设计)
10. [部署方案](#10-部署方案)
11. [测试方案](#11-测试方案)

---

## 1. 概述

### 1.1 项目背景

DataSphere AI Agent 是面向医疗数据中台的智能助手模块，通过 LLM 技术赋能，实现自然语言交互、智能辅助配置、数据知识增强等能力。

### 1.2 核心能力

| 能力 | 说明 |
|------|------|
| 自然语言交互 | 用户通过对话完成数据治理任务 |
| SQL智能生成 | 自然语言转换为SQL查询语句 |
| 数据集成配置 | 自动生成数据管道配置 |
| 质量规则生成 | 智能生成数据质量检测规则 |
| 知识库增强 | 基于元数据和规则模板的知识检索 |

### 1.3 技术选型

| 层次 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.2.3 |
| ORM | MyBatis-Plus | 3.5.5 |
| LLM框架 | LangChain4j | 0.35.0 |
| 前端框架 | Vue 3 | 3.4+ |
| UI框架 | Element Plus | 2.6+ |
| CRD框架 | Avue | 3.7+ |

---

## 2. 技术架构

### 2.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              前端层 (Frontend)                              │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │  datasphere/apps/agent (端口: 1811)                                    │  │
│  │  ├── 对话界面 (ChatView.vue)      - 多轮对话、Markdown渲染            │  │
│  │  ├── 会话管理 (SessionList.vue)   - 会话列表、归档删除                │  │
│  │  ├── 知识库管理 (KnowledgeView.vue) - 知识增删改查                    │  │
│  │  ├── 模型配置 (ModelConfig.vue)   - 模型管理、连接测试                │  │
│  │  ├── API密钥管理 (ApiKeyView.vue) - 密钥生成、权限控制                │  │
│  │  └── 用量统计 (UsageView.vue)     - Token统计、费用分析               │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
│  依赖: @daas-fe/core (CrudApi, CrudService, hasPermission)                  │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                            API网关层 (Gateway)                              │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │  cloud-svc-gateway (端口: 9000)                                        │  │
│  │  ├── 路由转发      - /api/agent/** → datasphere-svc-agent:8089       │  │
│  │  ├── 认证鉴权      - JWT Token验证                                    │  │
│  │  ├── 限流熔断      - Sentinel限流                                     │  │
│  │  └── 日志记录      - 请求日志、响应日志                                │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           后端服务层 (Backend)                              │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │  datasphere-svc-agent (端口: 8089)                                     │  │
│  │  ├── AgentSessionController    - 会话管理 API                         │  │
│  │  ├── ModelConfigController     - 模型配置 API                         │  │
│  │  ├── KnowledgeController       - 知识库 API                           │  │
│  │  ├── ApiKeyController          - API密钥 API                          │  │
│  │  ├── TokenUsageController      - 用量统计 API                         │  │
│  │  ├── ToolDefinitionController  - 工具定义 API                         │  │
│  │  ├── AuditLogController        - 审计日志 API                         │  │
│  │  └── OpenApiController         - 开放 API                             │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          Agent核心引擎层 (Engine)                           │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │  AgentEngine                                                           │  │
│  │  ├── processMessage()           - 同步处理消息                         │  │
│  │  ├── processMessageStream()     - 流式处理消息                         │  │
│  │  ├── executeTool()              - 执行工具调用                         │  │
│  │  └── buildPrompt()              - 构建提示词                           │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                    ┌─────────────────┴─────────────────┐
                    ▼                                   ▼
┌────────────────────────────────┐    ┌────────────────────────────────────┐
│      LLM服务层 (LLM)           │    │       工具执行层 (Tools)           │
│  ┌──────────────────────────┐  │    │  ┌──────────────────────────────┐  │
│  │ LlmAdapterFactory        │  │    │  │ ToolRegistry                 │  │
│  │ ├── ClaudeAdapter        │  │    │  │ ├── SqlExecutionTool         │  │
│  │ ├── OpenAiAdapter        │  │    │  │ ├── SqlGeneratorTool         │  │
│  │ ├── QwenAdapter          │  │    │  │ ├── MetadataQueryTool        │  │
│  │ └── LocalModelAdapter    │  │    │  │ ├── PipelineGeneratorTool    │  │
│  └──────────────────────────┘  │    │  │ └── QualityRuleGeneratorTool │  │
│  使用: LangChain4j SDK         │    │  └──────────────────────────────┘  │
└────────────────────────────────┘    └────────────────────────────────────┘
                    │                                   │
                    └─────────────────┬─────────────────┘
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          数据存储层 (Storage)                               │
│  ┌─────────────────────┐  ┌─────────────────┐  ┌─────────────────────────┐  │
│  │ MySQL 8.0+          │  │ Redis           │  │ Dify (可选)             │  │
│  │ ├── ai_model_config │  │ ├── 会话状态    │  │ ├── 知识库管理          │  │
│  │ ├── ai_agent_session│  │ ├── 响应缓存    │  │ ├── 向量存储            │  │
│  │ ├── ai_agent_message│  │ └── 限流计数    │  │ └── RAG检索             │  │
│  │ ├── ai_knowledge    │  └─────────────────┘  └─────────────────────────┘  │
│  │ ├── ai_tool_def     │                                                    │
│  │ ├── ai_api_key      │                                                    │
│  │ ├── ai_token_usage  │                                                    │
│  │ └── ai_audit_log    │                                                    │
│  └─────────────────────┘                                                    │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 模块依赖关系

```
datasphere-svc-agent
    │
    ├── datasphere-core-common (基础工具)
    │       ├── BaseEntity
    │       ├── BaseController
    │       └── RestResult
    │
    ├── datasphere-api-common (公共API)
    │
    ├── LangChain4j (LLM框架)
    │       ├── langchain4j
    │       ├── langchain4j-anthropic
    │       ├── langchain4j-open-ai
    │       └── langchain4j-dashscope
    │
    └── 内部模块
            ├── entity (实体层)
            ├── mapper (数据访问层)
            ├── service (服务层)
            ├── controller (控制层)
            ├── llm (LLM适配层)
            ├── tools (工具层)
            └── engine (引擎层)
```

### 2.3 服务调用关系

```
┌─────────────────────────────────────────────────────────────────┐
│                     服务间调用关系                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  datasphere-svc-agent                                           │
│       │                                                         │
│       ├──► datasphere-svc-datasource (RPC)                      │
│       │         └── SQL执行、数据源查询                          │
│       │                                                         │
│       ├──► datasphere-svc-metadata (RPC)                        │
│       │         └── 元数据查询、表结构获取                        │
│       │                                                         │
│       ├──► datasphere-svc-integration (RPC)                     │
│       │         └── 管道配置生成、任务执行                        │
│       │                                                         │
│       ├──► datasphere-svc-quality (RPC)                         │
│       │         └── 质量规则创建、任务执行                        │
│       │                                                         │
│       └──► datasphere-hie-gateway (RPC)                         │
│                 └── HIE服务配置                                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 3. 业务流程

### 3.1 对话处理流程

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          对话处理流程                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────┐    ┌─────────┐    ┌─────────────┐    ┌─────────────────┐  │
│  │ 用户    │───►│ 前端    │───►│ API Gateway │───►│ AgentController │  │
│  │ 输入    │    │ 发送    │    │ 路由/认证   │    │ 接收请求        │  │
│  └─────────┘    └─────────┘    └─────────────┘    └────────┬────────┘  │
│                                                                   │       │
│                                                                   ▼       │
│  ┌─────────────────────────────────────────────────────────────────────┐│
│  │                        AgentEngine.processMessage()                 ││
│  │                                                                     ││
│  │  ┌───────────────┐                                                  ││
│  │  │ 1. 获取会话   │ ───► 查询AgentSession，验证状态                  ││
│  │  └───────┬───────┘                                                  ││
│  │          │                                                          ││
│  │          ▼                                                          ││
│  │  ┌───────────────┐                                                  ││
│  │  │ 2. 获取模型   │ ───► 查询ModelConfig，获取LLM配置               ││
│  │  └───────┬───────┘                                                  ││
│  │          │                                                          ││
│  │          ▼                                                          ││
│  │  ┌───────────────┐                                                  ││
│  │  │ 3. 保存用户   │ ───► AgentMessageService.addUserMessage()        ││
│  │  │    消息       │                                                   ││
│  │  └───────┬───────┘                                                  ││
│  │          │                                                          ││
│  │          ▼                                                          ││
│  │  ┌───────────────┐                                                  ││
│  │  │ 4. 构建提示词 │ ───► 系统提示词 + 历史消息 + 工具描述            ││
│  │  └───────┬───────┘                                                  ││
│  │          │                                                          ││
│  │          ▼                                                          ││
│  │  ┌───────────────┐                                                  ││
│  │  │ 5. 调用LLM    │ ───► LlmAdapter.chat() / chatStream()            ││
│  │  └───────┬───────┘                                                  ││
│  │          │                                                          ││
│  │          ├─────────────────────────────────────┐                    ││
│  │          │                                     │                    ││
│  │          ▼                                     ▼                    ││
│  │  ┌───────────────┐                    ┌───────────────┐             ││
│  │  │ 6a. 直接响应  │                    │ 6b. 工具调用  │             ││
│  │  │    返回文本   │                    │    执行工具   │             ││
│  │  └───────┬───────┘                    └───────┬───────┘             ││
│  │          │                                     │                    ││
│  │          │                                     ▼                    ││
│  │          │                            ┌───────────────┐             ││
│  │          │                            │ 7. 工具结果   │             ││
│  │          │                            │    再次调用LLM│             ││
│  │          │                            └───────┬───────┘             ││
│  │          │                                     │                    ││
│  │          └─────────────────┬───────────────────┘                    ││
│  │                            │                                        ││
│  │                            ▼                                        ││
│  │  ┌───────────────────────────────────────────────────────────────┐  ││
│  │  │ 8. 保存助手消息 │ AgentMessageService.addAssistantMessage()   │  ││
│  │  └───────────────────────────────────────────────────────────────┘  ││
│  │                            │                                        ││
│  └────────────────────────────┼────────────────────────────────────────┘│
│                               │                                         │
│                               ▼                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐│
│  │ 9. 返回响应给前端                                                    ││
│  │    ├── 同步模式: 返回完整消息                                       ││
│  │    └── 流式模式: SSE推送Token                                       ││
│  └─────────────────────────────────────────────────────────────────────┘│
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 3.2 SQL生成流程

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          SQL生成流程                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  用户输入: "查询最近一个月门诊量前10的科室"                              │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Step 1: 意图识别                                                 │   │
│  │         LLM识别用户意图为"SQL查询"                               │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              │                                          │
│                              ▼                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Step 2: 元数据检索                                               │   │
│  │         MetadataQueryTool 查询相关表和字段                       │   │
│  │         ├── 门诊挂号表: outp_visit                               │   │
│  │         ├── 科室表: dept_info                                    │   │
│  │         └── 字段: dept_id, dept_name, visit_date                 │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              │                                          │
│                              ▼                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Step 3: 构建提示词                                               │   │
│  │                                                                 │   │
│  │  "根据以下表结构生成SQL查询:                                     │   │
│  │   表: outp_visit (门诊挂号表)                                    │   │
│  │     - dept_id: 科室ID                                           │   │
│  │     - visit_date: 就诊日期                                      │   │
│  │   表: dept_info (科室表)                                        │   │
│  │     - dept_id: 科室ID                                           │   │
│  │     - dept_name: 科室名称                                       │   │
│  │                                                                 │   │
│  │   用户需求: 查询最近一个月门诊量前10的科室                       │   │
│  │   要求: 返回科室名称和门诊人次"                                  │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              │                                          │
│                              ▼                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Step 4: LLM生成SQL                                               │   │
│  │                                                                 │   │
│  │  SELECT d.dept_name,                                            │   │
│  │         COUNT(*) as visit_count                                 │   │
│  │  FROM outp_visit v                                              │   │
│  │  JOIN dept_info d ON v.dept_id = d.dept_id                      │   │
│  │  WHERE v.visit_date >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)    │   │
│  │  GROUP BY d.dept_name                                           │   │
│  │  ORDER BY visit_count DESC                                      │   │
│  │  LIMIT 10                                                       │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              │                                          │
│                              ▼                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Step 5: SQL验证                                                  │   │
│  │         ├── 语法检查                                            │   │
│  │         ├── 安全检查(禁止DELETE/UPDATE等)                       │   │
│  │         └── 性能检查(添加LIMIT)                                  │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              │                                          │
│                              ▼                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Step 6: 返回结果                                                 │   │
│  │         ├── sql: 生成的SQL语句                                   │   │
│  │         ├── explanation: SQL解释                                 │   │
│  │         ├── tables: 使用的表                                     │   │
│  │         └── canExecute: 是否可直接执行                           │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 3.3 工具调用流程

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          工具调用流程                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  LLM响应包含工具调用:                                                   │
│  {                                                                      │
│    "tool_calls": [                                                      │
│      {                                                                  │
│        "name": "sql_executor",                                          │
│        "parameters": {                                                  │
│          "datasource_id": "ds_001",                                     │
│          "sql": "SELECT * FROM patient LIMIT 10"                        │
│        }                                                                │
│      }                                                                  │
│    ]                                                                    │
│  }                                                                      │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Step 1: 解析工具调用                                             │   │
│  │         从LLM响应中提取tool_calls                                │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              │                                          │
│                              ▼                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Step 2: 查找工具                                                 │   │
│  │         ToolRegistry.getTool("sql_executor")                     │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              │                                          │
│                              ▼                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Step 3: 权限检查                                                 │   │
│  │         检查用户是否有执行该工具的权限                           │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              │                                          │
│                              ▼                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Step 4: 参数验证                                                 │   │
│  │         根据inputSchema验证参数格式和必填项                      │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              │                                          │
│                              ▼                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Step 5: 执行工具                                                 │   │
│  │         Tool.execute(params, context)                            │   │
│  │         ├── 记录开始时间                                         │   │
│  │         ├── 执行业务逻辑                                         │   │
│  │         ├── 记录结束时间                                         │   │
│  │         └── 返回ToolResult                                       │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              │                                          │
│                              ▼                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Step 6: 记录审计日志                                             │   │
│  │         AuditLogService.logOperation()                           │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              │                                          │
│                              ▼                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Step 7: 构建结果消息                                             │   │
│  │         将工具执行结果格式化为自然语言                           │   │
│  │         "已执行SQL查询，返回10条记录..."                         │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 4. 接口设计

### 4.1 RESTful API规范

#### 基础路径

```
/api/v1/agent/{resource}
```

#### 通用响应格式

```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": "2026-03-09T12:00:00"
}
```

#### 分页响应格式

```json
{
  "success": true,
  "code": 200,
  "data": {
    "current": 1,
    "size": 10,
    "total": 100,
    "pages": 10,
    "records": []
  }
}
```

### 4.2 API接口清单

#### 4.2.1 会话管理API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/v1/agent/sessions | 分页查询会话列表 |
| GET | /api/v1/agent/sessions/user/{userId} | 获取用户会话列表 |
| GET | /api/v1/agent/sessions/{id} | 获取会话详情 |
| POST | /api/v1/agent/sessions | 创建会话 |
| PUT | /api/v1/agent/sessions/{id}/title | 更新会话标题 |
| PUT | /api/v1/agent/sessions/{id}/archive | 归档会话 |
| DELETE | /api/v1/agent/sessions/{id} | 删除会话 |
| GET | /api/v1/agent/sessions/{sessionId}/messages | 获取会话消息 |
| POST | /api/v1/agent/sessions/{sessionId}/messages | 发送消息 |
| POST | /api/v1/agent/sessions/{sessionId}/messages/stream | 发送消息(SSE流式) |

#### 4.2.2 模型配置API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/v1/agent/models | 分页查询模型配置 |
| GET | /api/v1/agent/models/enabled | 获取启用的模型列表 |
| GET | /api/v1/agent/models/{id} | 获取模型详情 |
| POST | /api/v1/agent/models | 新增模型配置 |
| PUT | /api/v1/agent/models/{id} | 更新模型配置 |
| DELETE | /api/v1/agent/models/{id} | 删除模型配置 |
| POST | /api/v1/agent/models/{id}/test | 测试模型连接 |
| PUT | /api/v1/agent/models/{id}/status | 启用/禁用模型 |

#### 4.2.3 知识库API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/v1/agent/knowledge | 分页查询知识 |
| GET | /api/v1/agent/knowledge/type/{type} | 按类型查询 |
| GET | /api/v1/agent/knowledge/search | 语义检索 |
| GET | /api/v1/agent/knowledge/{id} | 获取知识详情 |
| POST | /api/v1/agent/knowledge | 新增知识 |
| PUT | /api/v1/agent/knowledge/{id} | 更新知识 |
| DELETE | /api/v1/agent/knowledge/{id} | 删除知识 |
| POST | /api/v1/agent/knowledge/batch | 批量导入 |

#### 4.2.4 开放API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/v1/agent/open/sql/generate | SQL生成API |
| POST | /api/v1/agent/open/sql/execute | SQL执行API |
| POST | /api/v1/agent/open/pipeline/generate | 管道配置生成API |
| POST | /api/v1/agent/open/quality-rule/generate | 质量规则生成API |
| POST | /api/v1/agent/open/metadata/query | 元数据查询API |
| POST | /api/v1/agent/open/chat | 对话API |

### 4.3 SSE流式响应

```
POST /api/v1/agent/sessions/{sessionId}/messages/stream
Content-Type: application/json

{
  "content": "帮我分析患者信息表的数据质量",
  "userId": "user_001",
  "tenantId": "tenant_001"
}

Response (text/event-stream):

event: start
data: {}

event: message
data: {"token": "根据"}

event: message
data: {"token": "分析"}

event: message
data: {"token": "，"}

event: tool_call
data: {"tool": "metadata_query", "params": {...}}

event: tool_result
data: {"result": {...}}

event: message
data: {"token": "患者信息表存在以下质量问题..."}

event: done
data: {"messageId": "msg_xxx", "tokenUsage": {"input": 156, "output": 423}}
```

---

## 5. 数据模型

### 5.1 E-R图

```
┌─────────────────┐       ┌─────────────────┐
│  ModelConfig    │       │  AgentSession   │
├─────────────────┤       ├─────────────────┤
│ id (PK)         │◄──────│ model_id (FK)   │
│ model_name      │       │ id (PK)         │
│ model_type      │       │ title           │
│ api_endpoint    │       │ user_id         │
│ api_key         │       │ tenant_id       │
│ capabilities    │       │ status          │
│ priority        │       │ message_count   │
│ status          │       └────────┬────────┘
└─────────────────┘                │
                                   │ 1:N
                                   ▼
                         ┌─────────────────┐
                         │  AgentMessage   │
                         ├─────────────────┤
                         │ id (PK)         │
                         │ session_id (FK) │
                         │ role            │
                         │ content         │
                         │ content_type    │
                         │ tool_calls      │
                         │ token_usage     │
                         └─────────────────┘

┌─────────────────┐       ┌─────────────────┐
│   Knowledge     │       │ ToolDefinition  │
├─────────────────┤       ├─────────────────┤
│ id (PK)         │       │ id (PK)         │
│ knowledge_type  │       │ tool_name       │
│ title           │       │ tool_type       │
│ content         │       │ description     │
│ vector_id       │       │ input_schema    │
│ tenant_id       │       │ output_schema   │
└─────────────────┘       │ executor_class  │
                          └─────────────────┘

┌─────────────────┐       ┌─────────────────┐
│     ApiKey      │       │   TokenUsage    │
├─────────────────┤       ├─────────────────┤
│ id (PK)         │       │ id (PK)         │
│ key_name        │       │ session_id      │
│ api_key         │       │ user_id         │
│ user_id         │       │ model_id        │
│ tenant_id       │       │ input_tokens    │
│ permissions     │       │ output_tokens   │
│ rate_limit      │       │ total_tokens    │
│ status          │       │ cost_amount     │
└─────────────────┘       └─────────────────┘
```

### 5.2 表结构详细设计

#### ai_model_config (模型配置表)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(32) | 主键ID |
| model_name | VARCHAR(100) | 模型名称 |
| model_type | VARCHAR(20) | 模型类型: CLAUDE/GPT/QWEN/LLAMA/LOCAL |
| api_endpoint | VARCHAR(500) | API端点 |
| api_key | TEXT | API密钥(加密) |
| model_params | JSON | 模型参数配置 |
| capabilities | VARCHAR(100) | 支持能力: CHAT,SQL,CODE,EMBEDDING |
| priority | INT | 优先级(用于模型路由) |
| status | INT | 状态: 0-禁用, 1-启用 |
| tenant_id | VARCHAR(32) | 租户ID |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

#### ai_agent_session (会话表)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(32) | 主键ID |
| title | VARCHAR(200) | 会话标题 |
| user_id | VARCHAR(32) | 用户ID |
| tenant_id | VARCHAR(32) | 租户ID |
| status | VARCHAR(20) | 状态: ACTIVE/ARCHIVED/DELETED |
| model_id | VARCHAR(32) | 使用的模型ID |
| context | JSON | 会话上下文 |
| message_count | INT | 消息数量 |
| last_active_time | DATETIME | 最后活跃时间 |

#### ai_agent_message (消息表)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(32) | 主键ID |
| session_id | VARCHAR(32) | 会话ID |
| role | VARCHAR(20) | 角色: USER/ASSISTANT/SYSTEM/TOOL |
| content | TEXT | 消息内容 |
| content_type | VARCHAR(20) | 类型: TEXT/SQL/TABLE/CHART/ERROR |
| tool_calls | JSON | 工具调用记录 |
| token_usage | JSON | Token消耗 |
| knowledge_refs | JSON | 引用的知识ID |

---

## 6. LLM集成

### 6.1 LangChain4j集成方案

#### 依赖配置

```xml
<properties>
    <langchain4j.version>0.35.0</langchain4j.version>
</properties>

<dependencies>
    <!-- LangChain4j Core -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j</artifactId>
        <version>${langchain4j.version}</version>
    </dependency>

    <!-- Anthropic Claude -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-anthropic</artifactId>
        <version>${langchain4j.version}</version>
    </dependency>

    <!-- OpenAI -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-open-ai</artifactId>
        <version>${langchain4j.version}</version>
    </dependency>

    <!-- 阿里云通义千问 -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-dashscope</artifactId>
        <version>${langchain4j.version}</version>
    </dependency>
</dependencies>
```

### 6.2 适配器实现示例

#### Claude适配器

```java
@Component
public class ClaudeAdapter implements LlmAdapter {

    @Override
    public String chat(String systemPrompt, String userMessage, ModelConfig config) {
        ChatLanguageModel model = AnthropicAiChatModel.builder()
                .apiKey(config.getApiKey())
                .modelName(AnthropicAiChatModelName.CLAUDE_3_SONNET_20240229)
                .temperature(0.7)
                .maxTokens(4096)
                .build();

        ChatRequest request = ChatRequest.builder()
                .messages(
                        SystemMessage.from(systemPrompt),
                        UserMessage.from(userMessage)
                )
                .build();

        ChatResponse response = model.generate(request);
        return response.content().text();
    }

    @Override
    public void chatStream(String systemPrompt, String userMessage,
                           ModelConfig config, StreamCallback callback) {
        StreamingChatLanguageModel model = AnthropicAiStreamingChatModel.builder()
                .apiKey(config.getApiKey())
                .modelName(AnthropicAiChatModelName.CLAUDE_3_SONNET_20240229)
                .temperature(0.7)
                .build();

        StringBuilder fullResponse = new StringBuilder();

        model.generate(
                List.of(
                        SystemMessage.from(systemPrompt),
                        UserMessage.from(userMessage)
                ),
                new StreamingResponseHandler<>() {
                    @Override
                    public void onNext(String token) {
                        fullResponse.append(token);
                        callback.onToken(token);
                    }

                    @Override
                    public void onComplete(Response<AiMessage> response) {
                        callback.onComplete(fullResponse.toString());
                    }

                    @Override
                    public void onError(Throwable error) {
                        callback.onError(error);
                    }
                }
        );
    }
}
```

### 6.3 Function Calling

```java
// 工具定义
@Tool("执行SQL查询并返回结果")
public String executeSql(
        @P("数据源ID") String datasourceId,
        @P("SQL语句") String sql
) {
    // 调用SqlExecutionTool
    JSONObject params = new JSONObject();
    params.put("datasource_id", datasourceId);
    params.put("sql", sql);

    ToolResult result = sqlExecutionTool.execute(params, context);
    return JSON.toJSONString(result);
}

// 构建带工具的模型
ChatLanguageModel model = OpenAiChatModel.builder()
        .apiKey(config.getApiKey())
        .modelName("gpt-4o")
        .tools(new SqlExecutionToolSpecification())
        .build();
```

---

## 7. 工具系统

### 7.1 工具架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         Tool System                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────┐     ┌─────────────────┐                   │
│  │ Tool Interface  │◄────│ ToolRegistry    │                   │
│  ├─────────────────┤     ├─────────────────┤                   │
│  │ getName()       │     │ getTool()       │                   │
│  │ getDescription()│     │ hasTool()       │                   │
│  │ getInputSchema()│     │ getAllSchemas() │                   │
│  │ execute()       │     └─────────────────┘                   │
│  └─────────────────┘                                           │
│           △                                                     │
│           │ 实现                                                │
│     ┌─────┴─────┬─────────────┬─────────────┬─────────────┐    │
│     │           │             │             │             │    │
│  ┌──┴───┐  ┌────┴────┐  ┌─────┴────┐  ┌─────┴────┐  ┌─────┴──┐ │
│  │SQL   │  │SQL      │  │Metadata  │  │Pipeline  │  │Quality │ │
│  │Exec  │  │Generator│  │Query     │  │Generator │  │RuleGen │ │
│  └──────┘  └─────────┘  └──────────┘  └──────────┘  └────────┘ │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 7.2 工具开发规范

```java
@Component
public class CustomTool implements Tool {

    @Override
    public String getName() {
        return "custom_tool";
    }

    @Override
    public String getDescription() {
        return "工具描述，用于LLM理解工具用途";
    }

    @Override
    public JSONObject getInputSchema() {
        // JSON Schema格式定义输入参数
        JSONObject schema = new JSONObject();
        schema.put("type", "object");

        JSONObject properties = new JSONObject();
        properties.put("param1", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "参数1描述"));
        properties.put("param2", new JSONObject()
                .fluentPut("type", "integer")
                .fluentPut("description", "参数2描述"));

        schema.put("properties", properties);
        schema.put("required", List.of("param1"));
        return schema;
    }

    @Override
    public ToolResult execute(JSONObject params, ToolContext context) {
        long startTime = System.currentTimeMillis();

        try {
            // 1. 参数验证
            String param1 = params.getString("param1");
            if (StringUtils.isBlank(param1)) {
                return ToolResult.error("参数param1不能为空");
            }

            // 2. 权限检查
            if (!checkPermission(context)) {
                return ToolResult.error("无执行权限");
            }

            // 3. 执行业务逻辑
            Object result = doExecute(param1, params);

            // 4. 返回结果
            ToolResult toolResult = ToolResult.success(result);
            toolResult.setExecutionTime(System.currentTimeMillis() - startTime);
            return toolResult;

        } catch (Exception e) {
            log.error("工具执行失败: {}", e.getMessage(), e);
            return ToolResult.error("执行失败: " + e.getMessage());
        }
    }
}
```

---

## 8. 前端设计

### 8.1 页面结构

```
datasphere/apps/agent/
├── src/
│   ├── main.ts              # 入口
│   ├── pages.ts             # 页面加载器
│   ├── App.vue              # 根组件
│   ├── api/                 # API封装
│   │   ├── index.ts         # API类定义
│   │   └── types.ts         # TypeScript类型
│   ├── views/               # 页面组件
│   │   ├── chat/            # 对话界面
│   │   ├── session/         # 会话管理
│   │   ├── model/           # 模型配置
│   │   ├── knowledge/       # 知识库
│   │   ├── api-key/         # API密钥
│   │   └── usage/           # 用量统计
│   ├── components/          # 公共组件
│   │   ├── ChatMessage.vue  # 消息组件
│   │   ├── ChatInput.vue    # 输入组件
│   │   ├── CodeBlock.vue    # 代码块
│   │   └── SqlResult.vue    # SQL结果
│   └── dict/                # 常量定义
```

### 8.2 状态管理

```typescript
// stores/agent.ts
import { defineStore } from 'pinia'

export const useAgentStore = defineStore('agent', {
  state: () => ({
    currentSession: null as AgentSession | null,
    sessions: [] as AgentSession[],
    messages: [] as AgentMessage[],
    models: [] as ModelConfig[],
    selectedModelId: '',
    loading: false,
    streaming: false
  }),

  actions: {
    async createSession(title: string) {
      const res = await sessionApi.add({
        title,
        modelId: this.selectedModelId
      })
      this.sessions.unshift(res.data)
      this.currentSession = res.data
      return res.data
    },

    async sendMessage(content: string) {
      if (!this.currentSession) return

      this.loading = true
      try {
        const res = await sessionApi.sendMessage(
          this.currentSession.id,
          content
        )
        this.messages.push(res.data)
      } finally {
        this.loading = false
      }
    },

    async sendMessageStream(content: string, onToken: (token: string) => void) {
      if (!this.currentSession) return

      this.streaming = true
      try {
        const eventSource = new EventSource(
          `/api/v1/agent/sessions/${this.currentSession.id}/messages/stream`,
          { content }
        )

        eventSource.onmessage = (event) => {
          if (event.event === 'message') {
            onToken(event.data.token)
          } else if (event.event === 'done') {
            eventSource.close()
          }
        }
      } finally {
        this.streaming = false
      }
    }
  }
})
```

---

## 9. 安全设计

### 9.1 安全机制

| 安全措施 | 说明 |
|----------|------|
| API密钥加密 | AES-256加密存储 |
| SQL注入防护 | 只允许SELECT语句，参数化查询 |
| 敏感数据脱敏 | 手机号、身份证等字段脱敏处理 |
| 速率限制 | 基于Redis的令牌桶限流 |
| 审计日志 | 记录所有操作，保留180天 |
| 多租户隔离 | tenant_id强制隔离 |

### 9.2 SQL安全规则

```java
public class SqlSecurityChecker {

    private static final List<String> FORBIDDEN_KEYWORDS = List.of(
            "DELETE", "DROP", "TRUNCATE", "ALTER", "CREATE",
            "INSERT", "UPDATE", "GRANT", "REVOKE"
    );

    public static void check(String sql) {
        String upperSql = sql.toUpperCase().trim();

        // 1. 只允许SELECT
        if (!upperSql.startsWith("SELECT")) {
            throw new SecurityException("只允许执行SELECT查询");
        }

        // 2. 检查禁止关键字
        for (String keyword : FORBIDDEN_KEYWORDS) {
            if (upperSql.contains(keyword)) {
                throw new SecurityException("SQL包含禁止关键字: " + keyword);
            }
        }

        // 3. 检查注释注入
        if (sql.contains("--") || sql.contains("/*")) {
            throw new SecurityException("SQL包含可疑注释");
        }

        // 4. 强制添加LIMIT
        if (!upperSql.contains("LIMIT")) {
            sql += " LIMIT 10000";
        }
    }
}
```

---

## 10. 部署方案

### 10.1 Docker部署

```dockerfile
# Dockerfile
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/datasphere-svc-agent-*.jar app.jar

ENV JAVA_OPTS="-Xms512m -Xmx1024m"

EXPOSE 8089

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### 10.2 Kubernetes部署

```yaml
# k8s-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: datasphere-svc-agent
spec:
  replicas: 2
  selector:
    matchLabels:
      app: datasphere-svc-agent
  template:
    metadata:
      labels:
        app: datasphere-svc-agent
    spec:
      containers:
      - name: agent
        image: datasphere/datasphere-svc-agent:2.1.0
        ports:
        - containerPort: 8089
        env:
        - name: MYSQL_HOST
          valueFrom:
            configMapKeyRef:
              name: datasphere-config
              key: mysql-host
        - name: NACOS_SERVER
          valueFrom:
            configMapKeyRef:
              name: datasphere-config
              key: nacos-server
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8089
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8089
          initialDelaySeconds: 5
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: datasphere-svc-agent
spec:
  selector:
    app: datasphere-svc-agent
  ports:
  - port: 8089
    targetPort: 8089
```

### 10.3 配置管理

```yaml
# application-prod.yml
server:
  port: 8089

spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST}:3306/datasphere
    username: ${MYSQL_USERNAME}
    password: ${MYSQL_PASSWORD}

datasphere:
  agent:
    default-model: CLAUDE
    max-history-messages: 20
    stream-timeout: 60
    tool-timeout: 30

# LLM配置
llm:
  claude:
    api-key: ${CLAUDE_API_KEY}
    model: claude-3-sonnet-20240229
  openai:
    api-key: ${OPENAI_API_KEY}
    model: gpt-4o
  qwen:
    api-key: ${QWEN_API_KEY}
    model: qwen-max
```

---

## 11. 测试方案

### 11.1 单元测试

```java
@SpringBootTest
class AgentEngineTest {

    @Autowired
    private AgentEngine agentEngine;

    @Autowired
    private AgentSessionService sessionService;

    @Test
    void testProcessMessage() {
        // 1. 创建会话
        AgentSession session = sessionService.createSession(
            "测试会话", null, "test_user", "test_tenant"
        );

        // 2. 发送消息
        AgentMessage message = agentEngine.processMessage(
            session.getId(),
            "你好",
            "test_user",
            "test_tenant"
        );

        // 3. 验证结果
        assertNotNull(message);
        assertEquals("ASSISTANT", message.getRole());
        assertNotNull(message.getContent());
    }
}
```

### 11.2 集成测试

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AgentControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testCreateSession() {
        webTestClient.post()
                .uri("/api/v1/agent/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                    "title", "测试会话",
                    "userId", "test_user"
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.id").exists()
                .jsonPath("$.data.title").isEqualTo("测试会话");
    }
}
```

---

## 附录

### A. 错误码定义

| 错误码 | 说明 |
|--------|------|
| 10001 | 会话不存在 |
| 10002 | 模型配置不存在 |
| 10003 | API密钥无效 |
| 10004 | Token额度不足 |
| 10005 | 工具执行失败 |
| 10006 | LLM调用失败 |

### B. 配置项说明

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| datasphere.agent.default-model | CLAUDE | 默认模型类型 |
| datasphere.agent.max-history-messages | 20 | 最大历史消息数 |
| datasphere.agent.stream-timeout | 60 | 流式响应超时(秒) |
| datasphere.agent.tool-timeout | 30 | 工具执行超时(秒) |

---

**文档版本历史**

| 版本 | 日期 | 修改内容 |
|------|------|----------|
| V1.0 | 2026-03-09 | 初始版本 |