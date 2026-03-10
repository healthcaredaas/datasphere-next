package cn.healthcaredaas.datasphere.svc.agent.rpc.impl;

import cn.healthcaredaas.datasphere.svc.agent.rpc.IntegrationRpcService;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据集成服务本地实现
 *
 * @author chenpan
 */
@Slf4j
@Component
public class IntegrationRpcServiceImpl implements IntegrationRpcService {

    @Override
    public JSONObject getPipeline(String pipelineId) {
        log.info("RPC调用: getPipeline, id={}", pipelineId);

        JSONObject pipeline = new JSONObject();
        pipeline.put("id", pipelineId);
        pipeline.put("name", "数据同步管道");
        pipeline.put("status", "RUNNING");
        return pipeline;
    }

    @Override
    public String createPipeline(JSONObject config) {
        log.info("RPC调用: createPipeline, config={}", config);
        return "pipeline_" + System.currentTimeMillis();
    }

    @Override
    public void updatePipeline(String pipelineId, JSONObject config) {
        log.info("RPC调用: updatePipeline, id={}", pipelineId);
    }

    @Override
    public String executePipeline(String pipelineId) {
        log.info("RPC调用: executePipeline, id={}", pipelineId);
        return "job_" + System.currentTimeMillis();
    }

    @Override
    public List<JSONObject> getExecutionLogs(String jobId) {
        log.info("RPC调用: getExecutionLogs, jobId={}", jobId);
        return List.of();
    }

    @Override
    public List<JSONObject> listConnectorTypes() {
        log.info("RPC调用: listConnectorTypes");

        return List.of(
                createConnectorType("jdbc-mysql", "MySQL", "DATABASE"),
                createConnectorType("jdbc-oracle", "Oracle", "DATABASE"),
                createConnectorType("jdbc-postgresql", "PostgreSQL", "DATABASE"),
                createConnectorType("kafka", "Kafka", "MQ"),
                createConnectorType("http", "HTTP", "API")
        );
    }

    @Override
    public ValidationResult validatePipeline(JSONObject config) {
        log.info("RPC调用: validatePipeline");
        return new ValidationResult(true, List.of(), List.of());
    }

    @Override
    public String generateSeaTunnelConfig(JSONObject pipelineConfig) {
        log.info("RPC调用: generateSeaTunnelConfig");

        return """
            env {
              job.mode = "BATCH"
              parallelism = 1
            }

            source {
              Jdbc {
                url = "jdbc:mysql://localhost:3306/source_db"
                driver = "com.mysql.cj.jdbc.Driver"
                query = "SELECT * FROM source_table"
              }
            }

            sink {
              Jdbc {
                url = "jdbc:mysql://localhost:3306/target_db"
                driver = "com.mysql.cj.jdbc.Driver"
                table = "target_table"
              }
            }
            """;
    }

    private JSONObject createConnectorType(String code, String name, String category) {
        JSONObject ct = new JSONObject();
        ct.put("code", code);
        ct.put("name", name);
        ct.put("category", category);
        return ct;
    }
}