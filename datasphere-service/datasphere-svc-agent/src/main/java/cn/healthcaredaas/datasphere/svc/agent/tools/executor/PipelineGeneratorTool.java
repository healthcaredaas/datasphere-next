package cn.healthcaredaas.datasphere.svc.agent.tools.executor;

import cn.healthcaredaas.datasphere.svc.agent.entity.ModelConfig;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapter;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapterFactory;
import cn.healthcaredaas.datasphere.svc.agent.rpc.DatasourceRpcService;
import cn.healthcaredaas.datasphere.svc.agent.rpc.IntegrationRpcService;
import cn.healthcaredaas.datasphere.svc.agent.rpc.MetadataRpcService;
import cn.healthcaredaas.datasphere.svc.agent.service.ModelConfigService;
import cn.healthcaredaas.datasphere.svc.agent.tools.Tool;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolContext;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolResult;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据集成管道生成工具
 * 根据需求描述生成数据集成管道配置
 *
 * @author chenpan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PipelineGeneratorTool implements Tool {

    private final MetadataRpcService metadataRpcService;
    private final DatasourceRpcService datasourceRpcService;
    private final IntegrationRpcService integrationRpcService;
    private final LlmAdapterFactory llmAdapterFactory;
    private final ModelConfigService modelConfigService;

    /**
     * 管道生成系统提示词
     */
    private static final String PIPELINE_SYSTEM_PROMPT = """
            你是一位数据集成专家，负责根据用户需求生成数据同步管道配置。

            ## 配置说明
            需要生成的配置包括：
            1. 管道基本信息（名称、描述）
            2. 源端配置（数据源、表、字段、过滤条件）
            3. 目标端配置（数据源、表、写入模式）
            4. 字段映射关系
            5. 转换规则（数据脱敏、格式转换、编码映射等）
            6. 同步策略（全量/增量、调度周期）

            ## 输出格式
            请以JSON格式输出配置：
            ```json
            {
              "pipelineName": "管道名称",
              "description": "管道描述",
              "syncMode": "FULL/INCREMENTAL",
              "schedule": "cron表达式",
              "source": {
                "datasourceId": "源数据源ID",
                "tableName": "源表名",
                "columns": ["字段列表"],
                "filterCondition": "过滤条件"
              },
              "target": {
                "datasourceId": "目标数据源ID",
                "tableName": "目标表名",
                "writeMode": "UPSERT/INSERT/UPDATE"
              },
              "fieldMapping": [
                {
                  "sourceField": "源字段",
                  "targetField": "目标字段",
                  "transformType": "转换类型(NONE/MASK/ENCODE/CONVERT)",
                  "transformConfig": {}
                }
              ]
            }
            ```

            ## 数据源信息
            %s

            ## 表结构信息
            %s

            ## 可用连接器类型
            %s
            """;

    @Override
    public String getName() {
        return "pipeline_generator";
    }

    @Override
    public String getDescription() {
        return "根据需求描述生成数据集成管道配置。自动分析源和目标表结构，生成字段映射和SeaTunnel配置。";
    }

    @Override
    public JSONObject getInputSchema() {
        JSONObject schema = new JSONObject();
        schema.put("type", "object");

        JSONObject properties = new JSONObject();
        properties.put("description", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "数据集成需求描述，例如：将HIS患者信息同步到数据中心，手机号需要脱敏"));
        properties.put("source_datasource_id", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "源数据源ID"));
        properties.put("target_datasource_id", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "目标数据源ID"));
        properties.put("source_table", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "源表名"));
        properties.put("target_table", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "目标表名"));
        properties.put("options", new JSONObject()
                .fluentPut("type", "object")
                .fluentPut("description", "配置选项")
                .fluentPut("properties", new JSONObject()
                        .fluentPut("sync_mode", new JSONObject()
                                .fluentPut("type", "string")
                                .fluentPut("enum", List.of("FULL", "INCREMENTAL"))
                                .fluentPut("description", "同步模式")
                                .fluentPut("default", "FULL"))
                        .fluentPut("write_mode", new JSONObject()
                                .fluentPut("type", "string")
                                .fluentPut("enum", List.of("INSERT", "UPSERT", "UPDATE"))
                                .fluentPut("description", "写入模式")
                                .fluentPut("default", "UPSERT"))
                        .fluentPut("auto_create", new JSONObject()
                                .fluentPut("type", "boolean")
                                .fluentPut("description", "是否自动创建管道")
                                .fluentPut("default", false))));
        properties.put("field_mapping_hints", new JSONObject()
                .fluentPut("type", "object")
                .fluentPut("description", "字段映射提示，用于指导字段映射生成"));

        schema.put("properties", properties);
        schema.put("required", List.of("description"));
        return schema;
    }

    @Override
    public ToolResult execute(JSONObject params, ToolContext context) {
        long startTime = System.currentTimeMillis();

        try {
            String description = params.getString("description");
            String sourceDatasourceId = params.getString("source_datasource_id");
            String targetDatasourceId = params.getString("target_datasource_id");
            String sourceTable = params.getString("source_table");
            String targetTable = params.getString("target_table");
            JSONObject options = params.getJSONObject("options");
            JSONObject fieldMappingHints = params.getJSONObject("field_mapping_hints");

            log.info("生成数据管道 - sourceDatasourceId: {}, targetDatasourceId: {}",
                    sourceDatasourceId, targetDatasourceId);

            // 1. 获取数据源信息
            String datasourceInfo = buildDatasourceInfo(sourceDatasourceId, targetDatasourceId);

            // 2. 获取表结构信息
            String schemaInfo = buildSchemaInfo(sourceDatasourceId, sourceTable, targetDatasourceId, targetTable);

            // 3. 获取可用连接器类型
            String connectorInfo = buildConnectorInfo();

            // 4. 构建用户提示词
            String userPrompt = buildUserPrompt(description, sourceTable, targetTable, options, fieldMappingHints);

            // 5. 获取模型配置
            ModelConfig modelConfig = modelConfigService.getDefaultModel();
            if (modelConfig == null) {
                return ToolResult.error("未配置默认模型，请先在模型配置中添加模型");
            }

            // 6. 获取LLM适配器
            LlmAdapter adapter = llmAdapterFactory.getAdapter(modelConfig);

            // 7. 调用LLM生成配置
            String systemPrompt = String.format(PIPELINE_SYSTEM_PROMPT, datasourceInfo, schemaInfo, connectorInfo);
            String pipelineResponse = adapter.chat(systemPrompt, userPrompt, modelConfig);

            // 8. 解析配置JSON
            JSONObject pipelineConfig = parsePipelineConfig(pipelineResponse);

            // 9. 补充必要字段
            if (pipelineConfig != null) {
                enrichPipelineConfig(pipelineConfig, sourceDatasourceId, targetDatasourceId, sourceTable, targetTable, options);
            }

            // 10. 生成SeaTunnel配置
            String seatunnelConfig = null;
            if (pipelineConfig != null) {
                try {
                    seatunnelConfig = integrationRpcService.generateSeaTunnelConfig(pipelineConfig);
                } catch (Exception e) {
                    log.warn("生成SeaTunnel配置失败: {}", e.getMessage());
                }
            }

            // 11. 如果自动创建，则调用集成服务创建管道
            String pipelineId = null;
            boolean autoCreate = options != null && options.getBooleanValue("auto_create");
            if (autoCreate && pipelineConfig != null) {
                try {
                    pipelineId = integrationRpcService.createPipeline(pipelineConfig);
                    pipelineConfig.put("id", pipelineId);
                    log.info("创建管道成功: {}", pipelineId);
                } catch (Exception e) {
                    log.warn("创建管道失败: {}", e.getMessage());
                    pipelineConfig.put("createError", e.getMessage());
                }
            }

            // 12. 构建结果
            JSONObject result = new JSONObject();
            result.put("description", description);
            result.put("pipelineConfig", pipelineConfig);
            result.put("seatunnelConfig", seatunnelConfig);
            result.put("autoCreated", autoCreate);
            result.put("pipelineId", pipelineId);
            result.put("executionTime", System.currentTimeMillis() - startTime);

            log.info("管道配置生成成功 - 耗时 {}ms", result.getLong("executionTime"));

            return ToolResult.success(result);

        } catch (Exception e) {
            log.error("管道配置生成失败: {}", e.getMessage(), e);
            return ToolResult.error("管道配置生成失败: " + e.getMessage());
        }
    }

    /**
     * 构建数据源信息
     */
    private String buildDatasourceInfo(String sourceDatasourceId, String targetDatasourceId) {
        StringBuilder info = new StringBuilder();

        try {
            if (sourceDatasourceId != null) {
                JSONObject sourceDs = datasourceRpcService.getDatasource(sourceDatasourceId);
                if (sourceDs != null) {
                    info.append("源数据源: ").append(sourceDs.getString("datasourceName"));
                    info.append(" (类型: ").append(sourceDs.getString("datasourceType")).append(")\n");
                }
            }

            if (targetDatasourceId != null) {
                JSONObject targetDs = datasourceRpcService.getDatasource(targetDatasourceId);
                if (targetDs != null) {
                    info.append("目标数据源: ").append(targetDs.getString("datasourceName"));
                    info.append(" (类型: ").append(targetDs.getString("datasourceType")).append(")\n");
                }
            }
        } catch (Exception e) {
            log.warn("获取数据源信息失败: {}", e.getMessage());
        }

        return info.toString();
    }

    /**
     * 构建表结构信息
     */
    private String buildSchemaInfo(String sourceDsId, String sourceTable, String targetDsId, String targetTable) {
        StringBuilder info = new StringBuilder();

        try {
            // 源表结构
            if (sourceDsId != null && sourceTable != null) {
                info.append("### 源表: ").append(sourceTable).append("\n");
                List<JSONObject> sourceColumns = metadataRpcService.getTableColumns(sourceDsId, sourceTable);
                if (sourceColumns != null) {
                    info.append("字段:\n");
                    for (JSONObject col : sourceColumns) {
                        info.append("  - ").append(col.getString("columnName"));
                        info.append(" (").append(col.getString("columnType")).append(")");
                        String comment = col.getString("columnComment");
                        if (comment != null && !comment.isEmpty()) {
                            info.append(" -- ").append(comment);
                        }
                        info.append("\n");
                    }
                }
            }

            // 目标表结构
            if (targetDsId != null && targetTable != null) {
                info.append("\n### 目标表: ").append(targetTable).append("\n");
                List<JSONObject> targetColumns = metadataRpcService.getTableColumns(targetDsId, targetTable);
                if (targetColumns != null) {
                    info.append("字段:\n");
                    for (JSONObject col : targetColumns) {
                        info.append("  - ").append(col.getString("columnName"));
                        info.append(" (").append(col.getString("columnType")).append(")");
                        String comment = col.getString("columnComment");
                        if (comment != null && !comment.isEmpty()) {
                            info.append(" -- ").append(comment);
                        }
                        info.append("\n");
                    }
                }
            }
        } catch (Exception e) {
            log.warn("获取表结构信息失败: {}", e.getMessage());
        }

        return info.toString();
    }

    /**
     * 构建连接器信息
     */
    private String buildConnectorInfo() {
        StringBuilder info = new StringBuilder();

        try {
            List<JSONObject> connectors = integrationRpcService.listConnectorTypes();
            if (connectors != null && !connectors.isEmpty()) {
                info.append("可用连接器:\n");
                for (JSONObject conn : connectors) {
                    info.append("  - ").append(conn.getString("code"));
                    info.append(" (").append(conn.getString("name")).append(")\n");
                }
            }
        } catch (Exception e) {
            log.warn("获取连接器类型失败: {}", e.getMessage());
        }

        return info.toString();
    }

    /**
     * 构建用户提示词
     */
    private String buildUserPrompt(String description, String sourceTable, String targetTable,
                                    JSONObject options, JSONObject fieldMappingHints) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据以下需求生成数据集成管道配置：\n\n");
        prompt.append("需求：").append(description).append("\n\n");

        if (sourceTable != null) {
            prompt.append("源表：").append(sourceTable).append("\n");
        }
        if (targetTable != null) {
            prompt.append("目标表：").append(targetTable).append("\n");
        }

        if (options != null) {
            String syncMode = options.getString("sync_mode");
            if (syncMode != null) {
                prompt.append("同步模式：").append(syncMode).append("\n");
            }
            String writeMode = options.getString("write_mode");
            if (writeMode != null) {
                prompt.append("写入模式：").append(writeMode).append("\n");
            }
        }

        if (fieldMappingHints != null && !fieldMappingHints.isEmpty()) {
            prompt.append("\n字段映射提示：\n");
            for (String key : fieldMappingHints.keySet()) {
                prompt.append("  ").append(key).append(": ").append(fieldMappingHints.get(key)).append("\n");
            }
        }

        prompt.append("\n请直接输出JSON格式的管道配置：");
        return prompt.toString();
    }

    /**
     * 解析管道配置JSON
     */
    private JSONObject parsePipelineConfig(String response) {
        if (response == null || response.isEmpty()) {
            return null;
        }

        try {
            String jsonStr = response;

            // 提取代码块中的JSON
            if (jsonStr.contains("```json")) {
                int start = jsonStr.indexOf("```json") + 7;
                int end = jsonStr.indexOf("```", start);
                if (end > start) {
                    jsonStr = jsonStr.substring(start, end).trim();
                }
            } else if (jsonStr.contains("```")) {
                int start = jsonStr.indexOf("```") + 3;
                int end = jsonStr.indexOf("```", start);
                if (end > start) {
                    jsonStr = jsonStr.substring(start, end).trim();
                }
            }

            if (jsonStr.startsWith("{")) {
                return JSONObject.parseObject(jsonStr);
            }

        } catch (Exception e) {
            log.warn("解析管道配置JSON失败: {}", e.getMessage());
        }

        return null;
    }

    /**
     * 补充管道配置必要字段
     */
    private void enrichPipelineConfig(JSONObject config, String sourceDsId, String targetDsId,
                                       String sourceTable, String targetTable, JSONObject options) {
        // 补充源配置
        JSONObject source = config.getJSONObject("source");
        if (source == null) {
            source = new JSONObject();
            config.put("source", source);
        }
        if (sourceDsId != null && !source.containsKey("datasourceId")) {
            source.put("datasourceId", sourceDsId);
        }
        if (sourceTable != null && !source.containsKey("tableName")) {
            source.put("tableName", sourceTable);
        }

        // 补充目标配置
        JSONObject target = config.getJSONObject("target");
        if (target == null) {
            target = new JSONObject();
            config.put("target", target);
        }
        if (targetDsId != null && !target.containsKey("datasourceId")) {
            target.put("datasourceId", targetDsId);
        }
        if (targetTable != null && !target.containsKey("tableName")) {
            target.put("tableName", targetTable);
        }

        // 补充选项
        if (options != null) {
            if (!config.containsKey("syncMode") && options.containsKey("sync_mode")) {
                config.put("syncMode", options.getString("sync_mode"));
            }
            if (target != null && !target.containsKey("writeMode") && options.containsKey("write_mode")) {
                target.put("writeMode", options.getString("write_mode"));
            }
        }

        // 设置状态
        if (!config.containsKey("status")) {
            config.put("status", "DRAFT");
        }
    }
}