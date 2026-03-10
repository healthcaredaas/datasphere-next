# DataSphere-Next 下一代数据平台

<p align="center">
  <img src="docs/images/logo.png" alt="DataSphere Logo" width="200">
</p>

<p align="center">
  <a href="#">
    <img src="https://img.shields.io/badge/Spring%20Boot-3.5.11-brightgreen" alt="Spring Boot">
  </a>
  <a href="#">
    <img src="https://img.shields.io/badge/JDK-21-blue" alt="JDK">
  </a>
  <a href="#">
    <img src="https://img.shields.io/badge/MySQL-8.0-orange" alt="MySQL">
  </a>
  <a href="#">
    <img src="https://img.shields.io/badge/license-Apache%202.0-green" alt="License">
  </a>
</p>

## 🚀 项目简介

DataSphere-Next 是一个面向医疗健康领域的下一代数据平台，提供数据集成、数据标准、主数据管理、元数据管理、数据质量、数据资产、数据安全等核心能力。

## 📋 核心功能模块

| 模块 | 功能 | 状态 |
|------|------|------|
| **数据源管理** | 数据源的统一注册、配置和连接管理 | ✅ 已完成 |
| **数据集成** | 多引擎数据集成，支持批量/实时数据同步 | ✅ 已完成 |
| **数据标准** | 数据集、数据元、指标管理 | ✅ 已完成 |
| **主数据管理** | 组织机构、人员、术语主数据管理 | ✅ 已完成 |
| **元数据管理** | 元模型驱动的元数据管理 | ✅ 已完成 |
| **数据质量** | 质量规则、检测任务、报告生成、问题跟踪 | ✅ 已完成 |
| **数据资产** | 资产目录、数据血缘、生命周期管理 | ✅ 已完成 |
| **数据安全** | 脱敏规则、访问审计、敏感数据识别 | ✅ 已完成 |

## 🛠️ 技术栈

### 基础框架
- **Spring Boot**: 3.5.11
- **Spring Cloud**: 2025.0.0
- **Spring Cloud Alibaba**: 2025.0.0
- **JDK**: 21 (LTS)

### 数据存储
- **MySQL**: 8.0+
- **Redis**: 7.x
- **Elasticsearch**: 8.x (可选)

### 微服务组件
- **Nacos**: 3.0.x (服务注册/配置中心)
- **Dubbo**: 3.3.x (RPC框架)
- **RocketMQ**: 5.3.x (消息队列)

### 数据集成
- **SeaTunnel**: 2.3.x
- **Debezium**: 2.3.x (CDC)

### 任务调度
- **PowerJob**: 4.x

## 📁 项目结构

```
datasphere-next/
├── datasphere-api/                    # 公共API层
│   ├── datasphere-api-common
│   ├── datasphere-api-dataset
│   └── datasphere-api-master
│
├── datasphere-core/                   # 核心能力层
│   ├── datasphere-core-common
│   ├── datasphere-core-datasource
│   └── datasphere-core-engine
│
├── datasphere-engine/                 # 引擎服务层
│   ├── datasphere-engine-seatunnel
│   └── datasphere-engine-cdc
│
├── datasphere-service/                # 业务服务层
│   ├── datasphere-svc-datasource      # 数据源服务 (port: 8081)
│   ├── datasphere-svc-integration     # 数据集成服务 (port: 8082)
│   ├── datasphere-svc-standard        # 数据标准服务 (port: 8083)
│   ├── datasphere-svc-master          # 主数据服务 (port: 8084)
│   ├── datasphere-svc-metadata        # 元数据服务 (port: 8085)
│   ├── datasphere-svc-quality         # 数据质量服务 (port: 8086)
│   ├── datasphere-svc-asset           # 数据资产服务 (port: 8087)
│   └── datasphere-svc-security        # 数据安全服务 (port: 8088)
│
└── datasphere-database/               # 数据库脚本
    └── src/main/resources/db/migration/
```

## 🚀 快速开始

### 环境要求
- JDK 21+
- Maven 3.9+
- MySQL 8.0+
- Redis 7.x
- Nacos 3.0+

### 1. 克隆项目
```bash
git clone https://github.com/healthcaredaas/datasphere-next.git
cd datasphere-next
```

### 2. 初始化数据库
```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE datasphere CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# Flyway自动执行迁移脚本
```

### 3. 启动基础设施
```bash
docker-compose up -d mysql redis nacos
```

### 4. 编译项目
```bash
mvn clean install -DskipTests
```

### 5. 启动服务
```bash
# 启动质量服务（示例）
cd datasphere-service/datasphere-svc-quality
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 6. 访问服务
| 服务 | 地址 | 说明 |
|------|------|------|
| 数据源服务 | http://localhost:8081 | 数据源管理 |
| 质量服务 | http://localhost:8086 | 质量规则/检测 |
| 资产服务 | http://localhost:8087 | 资产/血缘管理 |
| Nacos控制台 | http://localhost:8848/nacos | 服务注册/配置 |
| Swagger文档 | http://localhost:8086/doc.html | API文档 |

## 📊 数据质量模块亮点

### 7大规则类型，40+预置模板

| 规则类型 | 数量 | 核心功能 |
|----------|------|----------|
| 完整性检查 | 5个 | NULL/空字符串检查 |
| 唯一性检查 | 3个 | 单字段/组合字段唯一性 |
| 格式检查 | 9个 | 手机号/身份证/邮箱/URL/日期等 |
| 值域检查 | 7个 | 数值范围/枚举/长度/外键引用 |
| **一致性检查** | **6个** | **多表关联验证（重点）** |
| 准确性检查 | 5个 | 金额/库存/退款/逻辑校验 |
| 自定义检查 | 2个 | 完全自定义SQL |

### 多表校验能力
- ✅ **正向检查**：订单表 → 用户表（检查外键引用有效性）
- ✅ **反向检查**：用户表 → 会员表（检查必要关联完整性）
- ✅ **汇总一致性**：订单金额 = SUM(子项金额)
- ✅ **记录数匹配**：用户订单数字段 = COUNT(订单记录)

**使用示例**：
```json
{
  "ruleName": "订单用户存在性检查",
  "ruleType": "CONSISTENCY",
  "ruleExpression": "SELECT o.* FROM order_info o LEFT JOIN user_info u ON o.user_id = u.user_id WHERE u.user_id IS NULL",
  "errorMessage": "订单关联的用户在用户表中不存在"
}
```

## 🧪 测试

### 运行单元测试
```bash
mvn test
```

### 运行集成测试
```bash
mvn verify -P integration-test
```

### 测试覆盖率报告
```bash
mvn jacoco:report
```

## 🐳 Docker部署

### 构建镜像
```bash
# 构建所有服务
mvn clean package -DskipTests
docker-compose build

# 或构建单个服务
docker build -t datasphere-svc-quality ./datasphere-service/datasphere-svc-quality
```

### 启动所有服务
```bash
docker-compose up -d
```

### 查看日志
```bash
docker-compose logs -f svc-quality
```

## 📚 文档

| 文档 | 路径 |
|------|------|
| 产品需求规格说明书 | `docs/PRD文档/产品需求规格说明书.md` |
| 数据质量模块详细设计 | `docs/设计文档/数据质量模块详细设计.md` |
| 技术选型说明 | `docs/技术文档/技术选型说明.md` |
| API接口文档 | 启动服务后访问 `/doc.html` |

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📄 许可证

本项目采用 Apache License 2.0 开源许可证。

## 👥 团队

- **架构师**: chenpan
- **开发团队**: DataSphere Team
- **邮箱**: chenpan.ai@qq.com

---

<p align="center">
  Made with ❤️ by DataSphere Team
</p>

## 项目结构

```
daas/
├── daas-boot/                          # 基础开发框架
│   ├── daas-dependencies/              # 依赖管理
│   ├── daas-core/                      # 核心工具类
│   ├── daas-data/                      # 数据访问层
│   │   ├── daas-data-core/             # 数据核心
│   │   ├── daas-data-mybatis-plus/     # MyBatis-Plus 封装
│   │   └── daas-data-hudi/             # Hudi 大数据支持
│   ├── daas-web/                       # Web 层
│   │   ├── daas-web-core/              # Web 核心
│   │   ├── daas-web-rest/              # REST 控制器
│   │   └── daas-web-audit/             # 审计日志
│   ├── daas-security/                  # 安全模块
│   │   ├── daas-security-core/         # 安全核心
│   │   ├── daas-security-authentication/ # 认证
│   │   └── daas-security-authorization/  # 授权
│   ├── daas-scheduler/                 # 调度模块
│   │   ├── daas-scheduler-common/      # 调度通用
│   │   └── daas-scheduler-powerjob/    # PowerJob 集成
│   ├── daas-dataformat/                # 数据格式化
│   ├── daas-exchange/                  # 数据交换
│   ├── daas-id/                        # ID 生成
│   ├── daas-oss/                       # 对象存储
│   │   └── daas-oss-minio/             # MinIO 集成
│   └── daas-ops-metric/                # 运维监控
│
└── daas-cloud/                         # 微服务应用
    ├── cloud-domain-foundation/        # 基础领域模型
    ├── cloud-svc-auth/                 # 认证服务
    ├── cloud-svc-foundation/           # 系统管理服务
    └── cloud-svc-gateway/              # API 网关
```

## 技术栈

### 核心框架

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.11 | 应用框架 |
| Spring Framework | 6.2.16 | 核心框架 |
| Spring Cloud | 2025.0.0 | 微服务框架 |
| Spring Cloud Alibaba | 2025.0.0.0 | 阿里巴巴微服务组件 |
| Spring Security | 6.5.8 | 安全框架 |
| Spring Security OAuth2 Authorization Server | 1.5.6 | OAuth2 授权服务器 |

### 数据访问

| 组件 | 版本 | 说明 |
|------|------|------|
| MyBatis-Plus | 3.5.12 | ORM 框架 |
| MySQL Connector | 9.2.0 | MySQL 驱动 |
| Redisson | 3.45.0 | Redis 客户端 |

### 工具组件

| 组件 | 版本 | 说明 |
|------|------|------|
| Hutool | 5.8.37 | Java 工具集 |
| FastJSON2 | 2.0.53 | JSON 处理 |
| Guava | 33.0.0-jre | Google 工具集 |
| SpringDoc OpenAPI | 2.7.0 | API 文档生成 |

### 中间件

| 组件 | 版本 | 说明 |
|------|------|------|
| Nacos | 2.5.1 | 注册中心/配置中心 |
| Sentinel | (由 Spring Cloud Alibaba 管理) | 流量控制 |
| PowerJob | 5.1.1 | 分布式任务调度 |

## 模块说明

### daas-boot 基础框架

#### daas-core
核心工具模块，包含：
- 通用常量定义
- 异常处理
- 上下文管理
- REST 响应封装
- 工具类

#### daas-data
数据访问模块，提供：
- `BaseEntity`: 基础实体类
- `BaseTreeEntity`: 树形实体类
- MyBatis-Plus 增强功能
- 多数据源支持
- Hudi 大数据支持

#### daas-web
Web 层模块，提供：
- 全局异常处理
- 统一响应格式
- REST 基础控制器
  - `BaseCRUDController`: CRUD 控制器
  - `BaseTreeCRUDController`: 树形 CRUD 控制器
  - `BaseReadController`: 只读控制器
  - `BaseWriteController`: 写入控制器
- 审计日志注解

#### daas-security
安全模块，提供：
- OAuth2 认证授权
- JWT Token 支持
- 权限校验
- 密码加密

### daas-cloud 微服务

#### cloud-svc-auth
认证服务，提供：
- OAuth2 授权服务器
- 密码模式登录
- 客户端凭证模式
- Token 管理
- 登录失败限制

#### cloud-svc-foundation
系统管理服务，提供：
- 用户管理
- 角色管理
- 资源权限管理
- 字典管理
- 应用管理
- 配置管理

#### cloud-svc-gateway
API 网关，提供：
- 路由转发
- 负载均衡
- 限流熔断

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.5+

### 编译项目

```bash
# 编译 daas-boot
cd daas-boot
mvn clean install -DskipTests

# 编译 daas-cloud
cd ../daas-cloud
mvn clean install -DskipTests
```

### 启动服务

1. **启动 Nacos**
   ```bash
   sh startup.sh -m standalone
   ```

2. **创建数据库**
   ```sql
   CREATE DATABASE daas DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **配置 Nacos**
   - 创建命名空间
   - 导入配置文件

4. **启动服务**
   ```bash
   # 启动认证服务
   java -jar cloud-svc-auth/target/cloud-svc-auth.jar

   # 启动系统管理服务
   java -jar cloud-svc-foundation/target/cloud-svc-foundation.jar

   # 启动网关
   java -jar cloud-svc-gateway/target/cloud-svc-gateway.jar
   ```

## API 文档

项目使用 SpringDoc OpenAPI 生成 API 文档，基于 OpenAPI 3.0 规范。

### 访问地址

启动服务后，访问以下地址查看 API 文档：

| 服务 | Swagger UI | OpenAPI JSON |
|------|------------|--------------|
| 认证服务 | http://localhost:8080/swagger-ui.html | http://localhost:8080/v3/api-docs |
| 系统管理服务 | http://localhost:8081/swagger-ui.html | http://localhost:8081/v3/api-docs |
| API 网关 | http://localhost:9000/swagger-ui.html | http://localhost:9000/v3/api-docs |

### 配置说明

SpringDoc 配置位于 `application-rest.yaml`:

```yaml
springdoc:
  group-configs:
    - group: 'default'
      paths-to-match: '/**'
      packages-to-scan: healthcaredaas.hscp
  default-flat-param-object: true
```

### 注解使用

项目使用 OpenAPI 3.x 注解标注 API 信息：

```java
@Tag(name = "用户管理", description = "用户相关接口")
@RestController
public class UserController {

    @Operation(summary = "获取用户列表", description = "分页查询用户")
    @GetMapping("/users")
    public RestResult<Page<User>> list() {
        // ...
    }
}
```

常用注解：
- `@Tag`: 控制器级别的 API 分组
- `@Operation`: 接口操作说明
- `@Schema`: 实体类字段描述
- `@Parameter`: 参数描述

## 开发指南

### 新增业务模块

1. 在 `daas-boot` 或 `daas-cloud` 下创建新模块
2. 添加模块到父 POM
3. 继承相应的基类：
   - 实体类继承 `BaseEntity` 或 `BaseTreeEntity`
   - 服务类继承 `BaseServiceImpl`
   - 控制器继承 `BaseCRUDController`

### 代码规范

- 使用 Lombok 简化代码
- 使用 Jakarta EE 注解 (Spring Boot 3.x)
- 统一使用 `RestResult` 返回响应
- 异常使用 `BizException` 抛出

## 版本更新日志

### v0.0.1-SNAPSHOT (2026-03-03)

#### 版本升级
- Spring Boot 升级到 3.5.11
- Spring Cloud 升级到 2025.0.0
- Spring Cloud Alibaba 升级到 2025.0.0.0
- Spring Security OAuth2 Authorization Server 升级到 1.5.6
- MySQL Connector 升级到 9.2.0
- Redisson 升级到 3.45.0
- Hutool 升级到 5.8.37
- FastJSON2 升级到 2.0.53

#### 功能变更
- 移除多租户功能模块
- 合并 web-protect 到 web-core
- 模块数量从 31 个优化到 22 个
- API 文档从 Knife4j 迁移到 SpringDoc OpenAPI 2.7.0

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 许可证

本项目采用 Apache 2.0 许可证。详见 [LICENSE](LICENSE) 文件。

## 联系方式

- 作者: chirspan
- 邮箱: chenpan.ai@qq.com