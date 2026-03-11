# datasphere-next - 后端服务

## 项目概述

datasphere-next 是 DataSphere 项目的后端服务，采用微服务架构，提供数据集成、数据标准、数据质量、数据资产、AI智能助手等核心服务。

## 技术栈

- Spring Boot 3.5.11
- JDK 21
- MyBatis-Plus 3.5.x
- MySQL 8.0+
- Redis
- RocketMQ / Kafka
- Elasticsearch

## 项目结构

```
datasphere-next/
├── datasphere-gateway/         # API 网关
│
├── datasphere-core/            # 核心模块
│   ├── datasphere-core-common/ # 公共组件
│   └── datasphere-core-web/    # Web 组件
│
└── datasphere-service/         # 业务服务
    ├── datasphere-svc-integration/  # 数据集成
    ├── datasphere-svc-standard/     # 数据标准
    ├── datasphere-svc-master/       # 主数据
    ├── datasphere-svc-quality/      # 数据质量
    ├── datasphere-svc-asset/        # 数据资产
    ├── datasphere-svc-metadata/     # 元数据
    ├── datasphere-svc-datasource/   # 数据源
    ├── datasphere-svc-security/     # 数据安全
    ├── datasphere-svc-hie/          # HIE 服务
    └── datasphere-svc-agent/        # AI 智能助手
```

## 服务列表

| 服务 | 端口 | 说明 |
|------|------|------|
| gateway | 8080 | API 网关 |
| integration | 8081 | 数据集成服务 |
| quality | 8082 | 数据质量服务 |
| master | 8083 | 主数据服务 |
| asset | 8084 | 数据资产服务 |
| agent | 8085 | AI 智能助手 |

## 开发指南

### 环境要求

- JDK 21
- Maven 3.9+
- MySQL 8.0+
- Redis 7.0+

### 本地开发

```bash
# 编译
mvn clean compile

# 打包
mvn clean package -DskipTests

# 运行服务
java -jar datasphere-gateway/target/datasphere-gateway.jar
java -jar datasphere-service/datasphere-svc-quality/target/datasphere-svc-quality.jar
```

### 配置文件

```yaml
# application.yml
server:
  port: 8082

spring:
  application:
    name: quality-service
  datasource:
    url: jdbc:mysql://localhost:3306/datasphere
    username: root
    password: root
  redis:
    host: localhost
    port: 6379

mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
```

## 模块开发

### 创建新服务

1. 在 datasphere-service 下创建模块目录
2. 创建 pom.xml
3. 创建启动类
4. 创建配置文件
5. 开发业务代码

### Controller 开发

```java
@RestController
@RequestMapping("/api/v1/quality/rules")
@RequiredArgsConstructor
@Tag(name = "质量规则管理")
public class QualityRuleController {

    private final QualityRuleService qualityRuleService;

    @GetMapping
    @Operation(summary = "分页查询")
    public IPage<QualityRule> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            QualityRule params) {
        return qualityRuleService.pageQuery(new Page<>(current, size), params);
    }

    @PostMapping
    @Operation(summary = "新增")
    public QualityRule save(@RequestBody @Validated QualityRule rule) {
        qualityRuleService.save(rule);
        return rule;
    }
}
```

### Service 开发

```java
@Service
@RequiredArgsConstructor
public class QualityRuleServiceImpl implements QualityRuleService {

    private final QualityRuleMapper qualityRuleMapper;

    @Override
    public IPage<QualityRule> pageQuery(Page<QualityRule> page, QualityRule params) {
        LambdaQueryWrapper<QualityRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(params.getRuleName()), QualityRule::getRuleName, params.getRuleName())
               .eq(StringUtils.isNotBlank(params.getStatus()), QualityRule::getStatus, params.getStatus())
               .orderByDesc(QualityRule::getCreateTime);
        return qualityRuleMapper.selectPage(page, wrapper);
    }
}
```

### Entity 开发

```java
@Data
@TableName("qual_rule")
@Schema(description = "质量规则")
public class QualityRule extends BaseEntity {

    @Schema(description = "规则名称")
    @TableField("rule_name")
    private String ruleName;

    @Schema(description = "规则类型")
    @TableField("rule_type")
    private String ruleType;

    @Schema(description = "规则SQL")
    @TableField("rule_sql")
    private String ruleSql;

    @Schema(description = "状态")
    @TableField("status")
    private String status;
}
```

## 数据库迁移

### Flyway 配置

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

### 迁移脚本

```sql
-- V1.0.0__init_quality_schema.sql
CREATE TABLE IF NOT EXISTS `qual_rule` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `rule_name` varchar(100) NOT NULL COMMENT '规则名称',
  `rule_type` varchar(50) NOT NULL COMMENT '规则类型',
  `rule_sql` text COMMENT '规则SQL',
  `status` char(1) DEFAULT '1' COMMENT '状态',

  `create_by` varchar(32) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_count` int DEFAULT 0,
  `update_by` varchar(32) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `delete_flag` char(1) DEFAULT '0',
  `delete_time` datetime DEFAULT NULL,

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质量规则表';
```

## API 文档

### Swagger 配置

```yaml
springdoc:
  api-docs:
    enabled: true
    path: /v3/api-docs
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
```

### 访问地址

- Swagger UI: http://localhost:8082/swagger-ui.html
- API Docs: http://localhost:8082/v3/api-docs

## 常用命令

```bash
# 编译
mvn clean compile

# 打包跳过测试
mvn clean package -DskipTests

# 运行测试
mvn test

# 查看依赖树
mvn dependency:tree
```

## 代码规范

- Controller 使用 @RestController
- Service 使用 @Service
- Entity 继承 BaseEntity
- API 路径: /api/v1/{module}/{resource}
- 分页参数: current, size

## 注意事项

1. Entity 必须继承 BaseEntity
2. 接口需要 Swagger 注解
3. 事务方法添加 @Transactional
4. 异常使用 BusinessException