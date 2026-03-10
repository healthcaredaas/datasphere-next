# DataSphere Next - 医疗数据中台

下一代医疗数据平台，提供数据集成、数据标准、主数据管理、元数据管理等核心能力。

## 项目结构

```
datasphere-next/
├── datasphere-api/                    # Dubbo/Feign RPC接口定义
│   ├── datasphere-api-common/         # 公共DTO
│   ├── datasphere-api-datasource/     # 数据源服务接口
│   ├── datasphere-api-integration/    # 数据集成服务接口
│   └── datasphere-api-master/         # 主数据服务接口
│
├── datasphere-core/                   # 核心能力层
│   └── datasphere-core-common/        # 通用工具 (DbType, RdbmsUtils等)
│
├── datasphere-engine/                 # 集成引擎层
│   └── datasphere-engine-seatunnel/   # SeaTunnel引擎
│
├── datasphere-service/                # 业务服务层
│   ├── datasphere-svc-datasource/     # 数据源服务 (端口: 8081)
│   ├── datasphere-svc-integration/    # 数据集成服务 (端口: 8082)
│   ├── datasphere-svc-standard/       # 数据标准服务 (端口: 8083)
│   ├── datasphere-svc-master/         # 主数据服务 (端口: 8084)
│   ├── datasphere-svc-metadata/       # 元数据服务 (端口: 8085)
│   ├── datasphere-svc-quality/        # 数据质量服务 (端口: 8086)
│   ├── datasphere-svc-asset/          # 数据资产服务 (端口: 8087)
│   ├── datasphere-svc-security/       # 数据安全服务 (端口: 8088)
│   └── datasphere-svc-agent/          # AI Agent服务 (端口: 8089)
│
├── datasphere-hie/                    # 医疗集成引擎(HIE)
│   └── datasphere-hie-gateway/        # HIE网关服务 (端口: 8091)
│
├── datasphere-db-migration/           # 数据库迁移 (Flyway)
│   └── src/main/resources/db/migration
│
├── docker-compose.yml                 # Docker Compose配置
├── build.sh                           # 构建脚本
├── start.sh                           # 启动脚本
└── README.md                          # 项目说明
```

## 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.11 | 基础框架 |
| Spring Cloud | 2025.0.0 | 微服务框架 |
| Spring Cloud Alibaba | 2025.0.0 | 微服务治理 |
| Nacos | 3.0.x | 服务注册/配置中心 |
| MyBatis-Plus | 3.5.x | ORM框架 |
| MySQL | 8.0+ | 数据库 |
| Flyway | 10.x | 数据库迁移 |
| Redis | 7.x | 缓存 |
| Docker | 24.x | 容器化 |
| Swagger/OpenAPI | 2.x | API文档 |

## 模块说明

### API模块 (datasphere-api)

**Dubbo/Feign RPC接口定义**，用于服务间远程调用。

- `datasphere-api-datasource` - 数据源服务接口
- `datasphere-api-integration` - 数据集成服务接口
- `datasphere-api-master` - 主数据服务接口

### Core模块 (datasphere-core)

**核心工具类**

- `DbType` - 数据库类型枚举
- `RdbmsUtils` - JDBC连接工具
- `RdbmsMetaUtils` - 元数据获取工具
- `ColumnMeta` - 列元数据DTO
- `IdUtils` - ID生成工具
- `JsonUtils` - JSON工具

### Engine模块 (datasphere-engine)

**数据集成引擎**

- `datasphere-engine-seatunnel` - SeaTunnel批量同步引擎
- `datasphere-engine-cdc` - CDC实时同步引擎 (待实现)

### Service模块 (datasphere-service)

| 服务 | 端口 | 功能 |
|------|------|------|
| datasphere-svc-datasource | 8081 | 数据源管理、元数据获取 |
| datasphere-svc-integration | 8082 | 数据管道、作业调度 |
| datasphere-svc-standard | 8083 | 数据集、数据元、指标 |
| datasphere-svc-master | 8084 | 组织机构、人员、字典 |
| datasphere-svc-metadata | 8085 | 元模型、元数据项 |
| datasphere-svc-quality | 8086 | 质量规则、检测任务 |
| datasphere-svc-asset | 8087 | 数据资产管理 |
| datasphere-svc-security | 8088 | 脱敏规则、数据安全 |
| datasphere-svc-agent | 8089 | AI Agent智能助手 |

### HIE模块 (datasphere-hie)

**医疗集成引擎**

- `datasphere-hie-gateway` - HIE网关服务 (端口: 8091)

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Nacos 3.0+
- Docker (可选)

### 方式一：本地启动

#### 1. 初始化数据库

```bash
# 创建数据库
mysql -u root -p -e "create database datasphere character set utf8mb4;"

# Flyway会自动执行迁移脚本
```

#### 2. 编译项目

```bash
./build.sh
```

#### 3. 启动服务

```bash
# 启动数据源服务
cd datasphere-service/datasphere-svc-datasource
mvn spring-boot:run

# 或使用启动脚本
./start.sh
```

#### 4. 访问API文档

启动后访问 Swagger UI：
- 数据源服务: http://localhost:8081/swagger-ui.html
- 数据集成服务: http://localhost:8082/swagger-ui.html
- ...其他服务类似

### 方式二：Docker Compose启动

```bash
# 构建所有镜像
./build.sh

# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止所有服务
docker-compose down
```

## 数据库表结构

共 **26张表**，分为8个模块：

| 模块 | 表名 |
|------|------|
| 数据源 | datasource_classify, datasource_type, datasource_info |
| 数据集成 | di_data_project, di_data_pipeline, di_data_pipeline_connector, di_data_job_log |
| 数据标准 | dn_dataset, dn_data_element, dn_indicator, dn_oid |
| 主数据 | md_organization, md_department, md_person, md_dictionary |
| 元数据 | meta_model, meta_item |
| 数据质量 | dq_rule, dq_task |
| 数据资产 | da_asset |
| 数据安全 | ds_mask_rule |
| HIE | hie_service |

## API列表

### 数据源服务 (8081)
- `GET/POST/PUT/DELETE /api/v1/datasources` - 数据源管理
- `GET/POST/PUT/DELETE /api/v1/datasource-types` - 数据源类型管理
- `GET/POST/PUT/DELETE /api/v1/datasource-classifies` - 数据源分类管理
- `POST /api/v1/datasources/test` - 连接测试
- `GET /api/v1/datasources/{id}/tables` - 获取表列表
- `GET /api/v1/datasources/{id}/tables/{tableName}/columns` - 获取字段列表

### 数据集成服务 (8082)
- `GET/POST/PUT/DELETE /api/v1/integration/projects` - 数据项目管理
- `GET/POST/PUT/DELETE /api/v1/integration/pipelines` - 数据管道管理

### 数据标准服务 (8083)
- `GET/POST/PUT/DELETE /api/v1/standard/datasets` - 数据集管理
- `GET/POST/PUT/DELETE /api/v1/standard/indicators` - 指标管理

### 主数据服务 (8084)
- `GET/POST/PUT/DELETE /api/v1/master/organizations` - 组织机构管理
- `GET/POST/PUT/DELETE /api/v1/master/departments` - 科室管理
- `GET/POST/PUT/DELETE /api/v1/master/persons` - 人员管理

### 元数据服务 (8085)
- `GET/POST/PUT/DELETE /api/v1/metadata/items` - 元数据项管理

### 数据质量服务 (8086)
- `GET/POST/PUT/DELETE /api/v1/quality/rules` - 质量规则管理

### 数据资产服务 (8087)
- `GET/POST/PUT/DELETE /api/v1/asset/assets` - 数据资产管理

### 数据安全服务 (8088)
- `GET/POST/PUT/DELETE /api/v1/security/mask-rules` - 脱敏规则管理

### AI Agent服务 (8089)
- `GET/POST/PUT/DELETE /api/v1/agent/sessions` - 会话管理
- `POST /api/v1/agent/sessions/{id}/messages` - 发送消息
- `POST /api/v1/agent/sessions/{id}/messages/stream` - 流式消息(SSE)
- `GET/POST/PUT/DELETE /api/v1/agent/models` - 模型配置管理
- `GET/POST/PUT/DELETE /api/v1/agent/knowledge` - 知识库管理
- `GET/POST/PUT/DELETE /api/v1/agent/api-keys` - API密钥管理
- `POST /api/v1/agent/open/chat` - 开放对话API
- `POST /api/v1/agent/open/sql/generate` - SQL生成API
- `POST /api/v1/agent/open/pipeline/generate` - 数据管道生成API

### HIE网关服务 (8091)
- `GET/POST/PUT/DELETE /api/v1/hie/services` - 交互服务管理

## 开发指南

### 添加新实体

1. 在对应模块的 `entity` 包下创建实体类
2. 继承 `BaseEntity` 类（来自daas-boot）
3. 添加 `@TableName` 注解指定表名
4. 使用 `@TableField` 注解映射字段

### 添加新接口

1. 创建Mapper接口继承 `BaseMapper`
2. 创建Service接口继承 `IService`
3. 创建ServiceImpl实现类继承 `ServiceImpl`
4. 创建Controller类使用 `@RestController`

### 服务间调用

使用Dubbo RPC调用其他服务：

```java
@Service
public class SomeService {
    @DubboReference
    private DatasourceInfoApi datasourceInfoApi;

    public void someMethod() {
        DatasourceInfoDTO dsInfo = datasourceInfoApi.getById("xxx");
    }
}
```

## 脚本说明

| 脚本 | 用途 |
|------|------|
| `build.sh` | 编译项目并构建Docker镜像 |
| `start.sh` | 快速启动服务 |
| `stop.sh` | 停止所有服务 |

## 端口分配

| 服务 | 端口 | 说明 |
|------|------|------|
| datasphere-svc-datasource | 8081 | 数据源服务 |
| datasphere-svc-integration | 8082 | 数据集成服务 |
| datasphere-svc-standard | 8083 | 数据标准服务 |
| datasphere-svc-master | 8084 | 主数据服务 |
| datasphere-svc-metadata | 8085 | 元数据服务 |
| datasphere-svc-quality | 8086 | 数据质量服务 |
| datasphere-svc-asset | 8087 | 数据资产服务 |
| datasphere-svc-security | 8088 | 数据安全服务 |
| datasphere-svc-agent | 8089 | AI Agent服务 |
| datasphere-hie-gateway | 8091 | HIE网关服务 |

## 作者

- 陈攀 (chenpan.ai@qq.com)

## 版本

2.0.0-SNAPSHOT

## 许可证

GNU General Public License v3.0 (GPL-3.0)
