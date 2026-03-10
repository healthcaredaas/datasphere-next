# DataSphere API 模块说明

## 概述

`datasphere-api` 模块用于定义 **Dubbo/Feign RPC 远程接口**，供服务间调用。这些接口**不是** Web API，而是分布式服务间通信的接口定义。

## 模块结构

```
datasphere-api/
├── datasphere-api-common         # 公共DTO、异常定义
├── datasphere-api-datasource     # 数据源服务RPC接口
├── datasphere-api-integration    # 数据集成服务RPC接口
├── datasphere-api-master         # 主数据服务RPC接口
└── pom.xml
```

## 与 Web API 的区别

| 特性 | API模块 (RPC) | Controller模块 (REST) |
|------|--------------|---------------------|
| 用途 | 服务间内部调用 | 对外提供HTTP接口 |
| 协议 | Dubbo/Feign | HTTP/REST |
| 位置 | datasphere-api | datasphere-service/*/controller |
| 调用方式 | 直接注入接口 | HTTP请求 |

## 使用方式

### 1. 定义RPC接口 (API模块)

```java
// datasphere-api-datasource 模块
public interface DatasourceInfoApi {
    DatasourceInfoDTO getById(String id);
    List<DatasourceInfoDTO> listAll();
}
```

### 2. 实现RPC接口 (Service模块)

```java
// datasphere-svc-datasource 模块
@DubboService
public class DatasourceInfoApiImpl implements DatasourceInfoApi {

    @Autowired
    private DatasourceInfoService datasourceInfoService;

    @Override
    public DatasourceInfoDTO getById(String id) {
        // 实现逻辑
    }
}
```

### 3. 消费RPC接口 (其他Service模块)

```java
// datasphere-svc-integration 模块
@Service
public class DataPipelineServiceImpl implements DataPipelineService {

    @DubboReference
    private DatasourceInfoApi datasourceInfoApi;

    public void someMethod() {
        // 远程调用数据源服务
        DatasourceInfoDTO dsInfo = datasourceInfoApi.getById("xxx");
    }
}
```

## 接口列表

### datasphere-api-datasource
- `DatasourceInfoApi` - 数据源信息查询

### datasphere-api-integration
- `DataPipelineApi` - 数据管道管理

### datasphere-api-master
- `OrganizationApi` - 组织机构查询

## 添加新RPC接口步骤

1. 在对应api模块创建接口定义
2. 创建DTO对象 (必须实现Serializable)
3. 在对应service模块创建实现类
4. 在实现类上添加 `@DubboService` 注解
5. 在其他服务中通过 `@DubboReference` 注入使用

## 注意事项

1. **DTO必须实现Serializable** - RPC传输需要序列化
2. **接口要保持向后兼容** - 避免删除或修改已有方法
3. **DTO不要包含敏感信息** - 接口可能被多个服务消费
4. **接口粒度要合理** - 避免过大或过小的接口设计
