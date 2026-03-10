# DataSphere 数据库迁移服务

## 概述

`datasphere-db-migration` 是 DataSphere 数据中台的独立数据库迁移服务，基于 **Flyway** 实现数据库版本化管理。

## 目的

- **版本管理**：对数据库表结构进行版本控制，支持增量升级
- **自动化迁移**：服务启动时自动执行数据库迁移脚本
- **多环境支持**：支持开发、测试、生产等多环境部署
- **回滚追溯**：完整的迁移历史记录，便于问题排查

## 架构设计

```
┌─────────────────────────────────────────────────────────────┐
│                 DataSphere 部署架构                          │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────────┐      ┌───────────────────────┐       │
│  │  MySQL Database  │◄─────│ datasphere-db-migration │       │
│  │                  │      │  (独立迁移服务)          │       │
│  │  - datasphere    │      │  - Flyway              │       │
│  │                  │      │  - V1.0.0 ~ V2.1.1     │       │
│  └──────────────────┘      └───────────────────────┘       │
│         ▲                                                   │
│         │                                                   │
│         ▼                                                   │
│  ┌──────────────────┐      ┌──────────────────┐            │
│  │  业务微服务        │      │  其他微服务        │            │
│  │  (不执行迁移)      │      │  (不执行迁移)      │            │
│  └──────────────────┘      └──────────────────┘            │
│                                                              │
└─────────────────────────────────────────────────────────────┘

说明：数据库迁移服务独立部署，先于业务服务启动，确保数据库结构就绪。
```

## 快速开始

### 1. 环境准备

确保已安装：
- JDK 17+
- Maven 3.8+
- MySQL 8.0+

### 2. 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS datasphere
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

### 3. 配置数据库连接

通过环境变量配置：

```bash
export MYSQL_HOST=localhost
export MYSQL_PORT=3306
export MYSQL_USERNAME=root
export MYSQL_PASSWORD=your_password
```

或修改 `application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/datasphere
    username: root
    password: your_password
```

### 4. 本地启动

```bash
# 编译打包
cd datasphere-next/datasphere-db-migration
mvn clean package -DskipTests

# 运行
java -jar target/datasphere-db-migration-2.0.0-SNAPSHOT.jar
```

### 5. Docker 部署

```bash
# 构建镜像
docker build -t datasphere-db-migration:2.0.0 .

# 运行容器
docker run -d \
  --name datasphere-db-migration \
  -e MYSQL_HOST=mysql \
  -e MYSQL_PORT=3306 \
  -e MYSQL_USERNAME=root \
  -e MYSQL_PASSWORD=root123 \
  --network datasphere-network \
  datasphere-db-migration:2.0.0
```

## 迁移脚本说明

### 版本命名规范

| 版本 | 文件名 | 描述 |
|------|--------|------|
| V1.0.0 | `V1.0.0__init_schema.sql` | 核心表结构 |
| V1.0.1 | `V1.0.1__init_data.sql` | 基础数据 |
| V1.1.0 | `V1.1.0__add_integration_job_tables.sql` | 集成作业表 |
| V1.1.1 | `V1.1.1__add_quality_module.sql` | 质量模块表 + 40+规则模板 |
| V1.1.2 | `V1.1.2__add_asset_module.sql` | 数据资产表 |
| V1.1.3 | `V1.1.3__add_security_module.sql` | 数据安全表 |
| V2.1.0 | `V2.1.0__add_agent_module.sql` | AI Agent模块表 |
| V2.1.1 | `V2.1.1__init_rbac_data.sql` | RBAC资源菜单数据 |

### 文件命名规则

```
V{版本号}__{描述}.sql

例如：
V2.1.0__add_agent_module.sql
V2.1.1__init_rbac_data.sql
```

**注意**：
- 版本号之间使用双下划线 `__` 分隔
- 版本号必须递增
- 文件一旦执行不可修改

### 新增迁移脚本

1. 在 `src/main/resources/db/migration/` 目录下创建新文件
2. 按版本命名规范命名文件
3. 编写 SQL 脚本
4. 测试验证后提交

```sql
-- 示例：V2.2.0__add_new_feature.sql

-- 创建新表
CREATE TABLE IF NOT EXISTS new_table (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(128),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 初始化数据
INSERT INTO new_table (id, name) VALUES ('1', 'test');
```

## 运维指南

### 查看迁移状态

通过 Actuator 端点查看：

```bash
curl http://localhost:8099/actuator/flyway
```

或查询数据库：

```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

### 常见问题

#### 1. 迁移失败如何处理？

```bash
# 查看错误日志
docker logs datasphere-db-migration

# 手动修复后重新执行
# 方案1：修复数据后，标记为成功
UPDATE flyway_schema_history SET success = 1 WHERE version = 'X.X.X';

# 方案2：回滚后重新执行（需要手动回滚SQL）
DELETE FROM flyway_schema_history WHERE version = 'X.X.X';
```

#### 2. 如何跳过某些版本？

不建议跳过版本。如果必须，可以使用 `flywayRepair`：

```bash
mvn flyway:repair -Dflyway.url=jdbc:mysql://localhost:3306/datasphere
```

#### 3. 生产环境注意事项

- 确保 `spring.flyway.clean-disabled: true`
- 迁移前备份数据库
- 大表变更在低峰期执行
- 监控迁移执行时间

### 健康检查

```bash
# 健康状态
curl http://localhost:8099/actuator/health

# Flyway 状态
curl http://localhost:8099/actuator/flyway
```

## CI/CD 集成

### Kubernetes 部署

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: datasphere-db-migration
spec:
  replicas: 1
  selector:
    matchLabels:
      app: datasphere-db-migration
  template:
    metadata:
      labels:
        app: datasphere-db-migration
    spec:
      initContainers:
        - name: wait-for-mysql
          image: busybox
          command: ['sh', '-c', 'until nc -z mysql 3306; do sleep 1; done']
      containers:
        - name: datasphere-db-migration
          image: datasphere-db-migration:2.0.0
          env:
            - name: MYSQL_HOST
              value: "mysql"
            - name: MYSQL_PORT
              value: "3306"
            - name: MYSQL_USERNAME
              valueFrom:
                secretKeyRef:
                  name: mysql-secret
                  key: username
            - name: MYSQL_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: mysql-secret
                  key: password
```

### Helm Chart 部署顺序

```yaml
# Chart.yaml
dependencies:
  - name: mysql
    version: "9.x.x"
    repository: "https://charts.bitnami.com/bitnami"
  - name: datasphere-db-migration
    version: "1.0.0"
    repository: "file://./charts/datasphere-db-migration"
  - name: datasphere-svc-xxx
    version: "1.0.0"
    repository: "file://./charts/datasphere-svc-xxx"
```

## 日志输出示例

```
2026-03-10 10:00:00.001 [main] INFO  c.h.d.m.MigrationApplication - ==========================================================
2026-03-10 10:00:00.002 [main] INFO  c.h.d.m.MigrationApplication -   DataSphere 数据库迁移服务启动中...
2026-03-10 10:00:00.003 [main] INFO  c.h.d.m.MigrationApplication - ==========================================================
2026-03-10 10:00:01.000 [main] INFO  c.h.d.m.callback.MigrationLogCallback - ==========================================================
2026-03-10 10:00:01.001 [main] INFO  c.h.d.m.callback.MigrationLogCallback -   [Flyway] 开始执行数据库迁移...
2026-03-10 10:00:01.002 [main] INFO  c.h.d.m.callback.MigrationLogCallback -   数据库: jdbc:mysql://localhost:3306/datasphere
2026-03-10 10:00:01.003 [main] INFO  c.h.d.m.callback.MigrationLogCallback - ==========================================================
2026-03-10 10:00:01.100 [main] INFO  c.h.d.m.callback.MigrationLogCallback -   [Flyway] 执行脚本: 1.0.0 - init schema
2026-03-10 10:00:02.000 [main] INFO  c.h.d.m.callback.MigrationLogCallback -   [Flyway] 脚本执行完成: 1.0.0 (耗时: 900ms)
...
2026-03-10 10:00:10.000 [main] INFO  c.h.d.m.callback.MigrationLogCallback - ==========================================================
2026-03-10 10:00:10.001 [main] INFO  c.h.d.m.callback.MigrationLogCallback -   [Flyway] 数据库迁移执行完成！
2026-03-10 10:00:10.002 [main] INFO  c.h.d.m.callback.MigrationLogCallback -   成功执行脚本数: 8
2026-03-10 10:00:10.003 [main] INFO  c.h.d.m.callback.MigrationLogCallback - ==========================================================
2026-03-10 10:00:11.000 [main] INFO  c.h.d.m.MigrationApplication - ==========================================================
2026-03-10 10:00:11.001 [main] INFO  c.h.d.m.MigrationApplication -   DataSphere 数据库迁移服务启动完成！
2026-03-10 10:00:11.002 [main] INFO  c.h.d.m.MigrationApplication - ==========================================================
```

## 相关文档

- [Flyway 官方文档](https://flywaydb.org/documentation/)
- [DataSphere 需求文档](../../docs/requirements/)
- [部署指南](../../docs/deployment/)

## 版本历史

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| 2.0.0 | 2026-03-10 | 初始版本，支持独立部署 |
| 2.1.0 | 2026-03-10 | 新增 AI Agent 模块表结构 |
| 2.1.1 | 2026-03-10 | 新增 RBAC 资源菜单数据 |

---

**作者**: chenpan
**最后更新**: 2026-03-10