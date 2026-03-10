package cn.healthcaredaas.datasphere.svc.agent.rpc;

import com.alibaba.fastjson2.JSONObject;

import java.util.List;

/**
 * 数据集成服务接口（RPC调用）
 *
 * @author chenpan
 */
public interface IntegrationRpcService {

    /**
     * 获取管道配置
     */
    JSONObject getPipeline(String pipelineId);

    /**
     * 创建管道
     */
    String createPipeline(JSONObject config);

    /**
     * 更新管道
     */
    void updatePipeline(String pipelineId, JSONObject config);

    /**
     * 执行管道
     */
    String executePipeline(String pipelineId);

    /**
     * 获取执行日志
     */
    List<JSONObject> getExecutionLogs(String jobId);

    /**
     * 获取连接器类型列表
     */
    List<JSONObject> listConnectorTypes();

    /**
     * 验证管道配置
     */
    ValidationResult validatePipeline(JSONObject config);

    /**
     * 生成SeaTunnel配置
     */
    String generateSeaTunnelConfig(JSONObject pipelineConfig);

    /**
     * 验证结果
     */
    record ValidationResult(
            boolean valid,
            List<String> errors,
            List<String> warnings
    ) {}
}