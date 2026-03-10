# DataSphere 数据集成服务单元测试文档

## 测试模块概览

| 模块 | 测试类 | 说明 |
|------|--------|------|
| datasphere-svc-integration | SeaTunnelConfigGeneratorTest | 配置生成器测试 |
| datasphere-svc-integration | DataJobServiceTest | 作业服务测试 |
| datasphere-svc-integration | DataJobControllerTest | 作业控制器测试 |
| datasphere-svc-integration-job-runner | IntegrationJobManagerTest | Job Manager测试 |
| datasphere-svc-integration-job-runner | SeaTunnelBatchProcessorTest | 批处理器测试 |

## 测试覆盖率

### 1. SeaTunnelConfigGeneratorTest

测试配置生成器的各种场景：

- ✅ `testGenerateJdbcToJdbcConfig` - 基本JDBC同步配置生成
- ✅ `testGenerateConfigWithSqlTransform` - 带SQL转换的配置生成
- ✅ `testGenerateConsoleSinkConfig` - Console输出配置生成
- ✅ `testGenerateWithEmptyConnectors` - 空连接器列表处理
- ✅ `testGenerateWithMultipleSourcesAndSinks` - 多Source/Sink配置
- ✅ `testGenerateZetaConfig` - Zeta配置生成（与generateConfig结果对比）

### 2. DataJobServiceTest

测试作业服务的业务逻辑：

- ✅ `testPublishJobSuccess` - 发布作业成功
- ✅ `testPublishJobNotFound` - 发布作业-作业不存在
- ✅ `testPublishPipelineNotFound` - 发布作业-管道不存在
- ✅ `testPublishEmptyConnectors` - 发布作业-连接器为空
- ✅ `testStartJobSuccess` - 启动作业成功
- ✅ `testStartJobNotFound` - 启动作业-作业不存在
- ✅ `testStartJobNotPublished` - 启动作业-未发布
- ✅ `testStopJob` - 停止作业
- ✅ `testStopJobNotFound` - 停止作业-作业不存在
- ✅ `testGetJobConfig` - 获取作业配置
- ✅ `testUpdateStatus` - 更新作业状态

### 3. DataJobControllerTest

测试控制器的REST接口：

- ✅ `testPage` - 分页查询
- ✅ `testGetById` - 根据ID获取作业
- ✅ `testSave` - 新增作业
- ✅ `testUpdate` - 更新作业
- ✅ `testDelete` - 删除作业
- ✅ `testPublish` - 发布作业
- ✅ `testStart` - 启动作业
- ✅ `testStop` - 停止作业
- ✅ `testGetConfig` - 获取作业配置
- ✅ `testCreateExecuteRecord` - 创建执行记录
- ✅ `testUpdateJobStatus` - 更新作业状态

### 4. IntegrationJobManagerTest

测试Job Manager的远程调用：

- ✅ `testGetJobConfigSuccess` - 获取作业配置成功
- ✅ `testGetJobConfigFailure` - 获取作业配置失败
- ✅ `testCreateExecuteRecordSuccess` - 创建执行记录成功
- ✅ `testCreateExecuteRecordNull` - 创建执行记录返回空
- ✅ `testUpdateExecuteSuccess` - 更新执行成功状态
- ✅ `testUpdateExecuteSuccessException` - 更新成功状态-异常不抛出
- ✅ `testUpdateExecuteFailed` - 更新执行失败状态
- ✅ `testUpdateJobStatus` - 更新作业状态
- ✅ `testUpdateJobStatusException` - 更新作业状态-异常不抛出

### 5. SeaTunnelBatchProcessorTest

测试PowerJob处理器：

- ✅ `testProcessSuccess` - 处理作业成功
- ✅ `testProcessFailure` - 处理作业失败
- ✅ `testProcessEmptyParams` - 空参数处理
- ✅ `testProcessBlankParams` - 空白参数处理
- ✅ `testProcessInvalidJson` - 无效JSON参数
- ✅ `testProcessMissingJobId` - 缺少jobId
- ✅ `testProcessWithoutExecuteId` - 无executeId执行
- ✅ `testProcessException` - 执行抛出异常

## 运行测试

### 运行所有测试

```bash
cd /Users/chenpan/dev/source_code/chirspan/daas/new/datasphere-next
mvn test
```

### 运行指定模块测试

```bash
# 主服务测试
mvn test -pl datasphere-service/datasphere-svc-integration

# Job Runner测试
mvn test -pl datasphere-service/datasphere-svc-integration-job-runner
```

### 运行指定测试类

```bash
mvn test -pl datasphere-service/datasphere-svc-integration -Dtest=SeaTunnelConfigGeneratorTest
```

## 测试技术

- **JUnit 5**: 测试框架
- **Mockito**: 模拟对象
- **AssertJ**: 流式断言（可选）

## 最佳实践

1. **单元测试隔离**: 每个测试方法独立运行，互不干扰
2. **Mock外部依赖**: 使用Mockito模拟Mapper、Service等依赖
3. **边界条件测试**: 测试空值、异常、边界条件
4. **命名规范**: 测试方法名使用`testXxx`格式，配合`@DisplayName`说明
5. **Given-When-Then**: 测试代码结构遵循准备-执行-验证模式
