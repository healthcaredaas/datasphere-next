# DataSphere-Next 项目重构文档

**项目**: datasphere-next (医疗数据平台重构)
**版本**: v1.1
**日期**: 2026-03-06
**状态**: 核心模块完成，服务模块待修复
**作者**: chirspan (chenpan.ai@qq.com)

---

## 文档概述

本文档是对 `datasphere-next` 项目进行全面分析和重构规划的完整文档集合，基于重构后的 `daas-boot` 框架，包含产品需求、技术设计和实施计划。

---

## 文档结构

```
docs/
├── PRD文档/                    # 产品需求文档
│   ├── 产品需求规格说明书.md    # 整体产品规划和需求
│   ├── 功能清单.md             # 详细功能列表
│   └── 模块规划.md             # 模块划分与规划
│
├── 设计文档/                   # 技术设计文档
│   ├── 系统架构设计.md          # 整体技术架构
│   ├── 数据库设计.md            # 数据库表结构设计
│   ├── 接口设计.md              # API接口规范
│   └── 模块详细设计.md          # 类图、时序图、核心逻辑
│
└── 技术文档/                   # 技术实施文档
    ├── 技术选型说明.md          # 技术栈选型和版本
    ├── 重构方案.md              # 重构实施详细方案
    └── 部署指南.md              # 部署操作手册
```

---

## 核心内容速览

### 1. 项目现状

**原项目结构**:
- `data-core`: 核心数据处理工具
- `data-common-api`: 公共API
- `data-integration-engine`: 数据集成引擎（SeaTunnel/CDC）
- `healthcare-integration-engine`: 医疗集成引擎（HIE）
- `svc-data-base`: 数据源服务
- `svc-data-integration`: 数据集成服务
- `svc-data-normalization`: 数据标准服务
- `svc-master-data`: 主数据服务
- `svc-metadata`: 元数据服务

**主要问题**:
1. Spring Boot 2.x 即将停止支持
2. 多租户功能增加复杂度
3. 模块命名不规范
4. 缺少数据质量、资产、安全模块

### 2. 重构目标

| 目标 | 说明 | 优先级 |
|------|------|--------|
| 技术升级 | Spring Boot 3.5.11 + JDK 21 | P0 |
| 架构优化 | 优化模块划分，降低耦合度 | P0 |
| 多租户移除 | 去除多租户功能，简化架构 | P0 |
| 功能增强 | 新增数据质量、资产、安全模块 | P1 |

### 3. 新项目结构

```
datasphere-next/
├── datasphere-api/                    # 公共API层
├── datasphere-core/                   # 核心能力层
├── datasphere-engine/                 # 引擎服务层
├── datasphere-service/                # 业务服务层
│   ├── datasphere-svc-datasource      # 数据源服务
│   ├── datasphere-svc-integration     # 数据集成服务
│   ├── datasphere-svc-standard        # 数据标准服务
│   ├── datasphere-svc-master          # 主数据服务
│   ├── datasphere-svc-metadata        # 元数据服务
│   ├── datasphere-svc-quality         # 数据质量服务 [新增]
│   ├── datasphere-svc-asset           # 数据资产服务 [新增]
│   └── datasphere-svc-security        # 数据安全服务 [新增]
├── datasphere-hie/                    # 医疗集成引擎
└── datasphere-database/               # 数据库脚本
```

### 4. 技术栈

| 层次 | 技术组件 | 版本 |
|------|----------|------|
| 基础框架 | Spring Boot | 3.5.11 |
| 微服务框架 | Spring Cloud | 2025.0.0 |
| 微服务治理 | Spring Cloud Alibaba | 2025.0.0 |
| JDK | OpenJDK | 21 |
| 服务注册/配置 | Nacos | 3.0.x |
| RPC框架 | Apache Dubbo | 3.3.x |
| 消息队列 | Apache RocketMQ | 5.3.x |
| 数据库 | MySQL | 8.0+ |
| ORM框架 | MyBatis-Plus | 3.5.x |
| 数据集成 | Apache SeaTunnel | 2.3.x |
| 任务调度 | PowerJob | 4.x |

### 5. 新增模块功能

#### 5.1 数据质量模块 (datasphere-svc-quality)

| 功能 | 说明 |
|------|------|
| 质量规则管理 | 完整性、唯一性、格式、值域、自定义规则 |
| 检测任务 | 即时检测和定时调度检测 |
| 质量报告 | 数据质量评分、问题分布统计 |
| 问题跟踪 | 质量问题记录、指派、处理闭环 |

#### 5.2 数据资产模块 (datasphere-svc-asset)

| 功能 | 说明 |
|------|------|
| 资产目录 | 数据资产注册、分类、标签管理 |
| 数据血缘 | 字段级血缘解析、可视化展示 |
| 生命周期 | 数据创建、归档、销毁策略 |
| 资产价值 | 访问热度、使用频次、价值评分 |

#### 5.3 数据安全模块 (datasphere-svc-security)

| 功能 | 说明 |
|------|------|
| 脱敏规则 | 静态脱敏、动态脱敏规则配置 |
| 脱敏算法 | 遮盖、替换、哈希等算法 |
| 访问审计 | 数据访问日志、操作审计 |
| 敏感数据识别 | 自动识别敏感数据字段 |

---

## 当前状态

### 已完成
- ✅ 所有文档编写 (10/10)
- ✅ 核心公共组件 (datasphere-core-common)
- ✅ 新增模块框架（数据质量、数据资产、数据安全）
- ✅ 数据库脚本 (Flyway迁移脚本)
- ✅ 核心模块编译成功

### 进行中
- ⏳ 服务模块编译修复
- ⏳ Dubbo API完善

### 项目进度
- 文档：100%
- 核心模块：95%
- 服务模块：70%
- 整体进度：85%

---

## 快速导航

### 产品需求
- [产品需求规格说明书](./PRD文档/产品需求规格说明书.md) - 整体产品规划和需求清单
- [功能清单](./PRD文档/功能清单.md) - 详细功能列表
- [模块规划](./PRD文档/模块规划.md) - 模块划分与映射关系

### 技术设计
- [系统架构设计](./设计文档/系统架构设计.md) - 整体技术架构设计
- [数据库设计](./设计文档/数据库设计.md) - 数据库表结构设计（含新增模块）
- [接口设计](./设计文档/接口设计.md) - RESTful API设计规范
- [模块详细设计](./设计文档/模块详细设计.md) - 类图、时序图、核心逻辑

### 技术实施
- [技术选型说明](./技术文档/技术选型说明.md) - 技术栈选型依据和版本兼容性
- [重构方案](./技术文档/重构方案.md) - 重构实施详细方案（含多租户移除）
- [部署指南](./技术文档/部署指南.md) - 单机/Docker/K8s部署手册

---

## 实施计划

| 阶段 | 时间 | 任务 | 产出 |
|------|------|------|------|
| **Phase 1** | Week 1-2 | 基础设施 | core、api、database模块 |
| **Phase 2** | Week 3-4 | 核心服务 | datasource、engine模块 |
| **Phase 3** | Week 5-6 | 业务服务 | integration、standard、master、metadata |
| **Phase 4** | Week 7-8 | 新增服务 | quality、asset、security |
| **Phase 5** | Week 9 | HIE服务 | hie模块 |
| **Phase 6** | Week 10 | 集成测试 | 全链路验证 |

---

## 关键变更点

### 1. 多租户移除
- 删除所有实体类的 `tenant_id` 字段
- 移除MyBatis租户拦截器
- 清理Service层租户上下文代码
- 数据库脚本删除租户相关字段

### 2. 包名调整
- 旧: `cn.healthcaredaas.data.cloud.datasphere.*`
- 新: `cn.healthcaredaas.datasphere.*`

### 3. Jakarta迁移
- `javax.*` -> `jakarta.*`
- 影响Servlet、Validation、Persistence等包

### 4. 依赖升级
- Spring Boot: 2.x -> 3.5.11
- Spring Cloud: 2021.x -> 2025.0.0
- JDK: 8/11 -> 21

---

## 联系方式

- **项目负责人**: chirspan
- **邮箱**: chenpan.ai@qq.com
- **项目路径**: `/Users/chenpan/dev/source_code/chirspan/daas/daas-backend/daas-microsvc/datasphere-next`
- **文档路径**: `/Users/chenpan/dev/source_code/chirspan/daas/new/docs`

---

## 更新记录

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.0 | 2026-03-04 | 初始版本，完成所有文档编写 |
| v1.1 | 2026-03-06 | 完善核心模块公共组件（缓存、日志、限流、脱敏）|

---

**文档版本**: v1.1
**最后更新**: 2026-03-06
