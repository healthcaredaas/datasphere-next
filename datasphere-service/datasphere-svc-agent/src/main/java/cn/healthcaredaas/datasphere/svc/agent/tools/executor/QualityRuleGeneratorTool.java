package cn.healthcaredaas.datasphere.svc.agent.tools.executor;

import cn.healthcaredaas.datasphere.svc.agent.entity.ModelConfig;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapter;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapterFactory;
import cn.healthcaredaas.datasphere.svc.agent.rpc.MetadataRpcService;
import cn.healthcaredaas.datasphere.svc.agent.rpc.QualityRpcService;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 质量规则生成工具
 * 根据需求描述生成数据质量规则配置
 *
 * @author chenpan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QualityRuleGeneratorTool implements Tool {

    private final MetadataRpcService metadataRpcService;
    private final QualityRpcService qualityRpcService;
    private final LlmAdapterFactory llmAdapterFactory;
    private final ModelConfigService modelConfigService;

    /**
     * 质量规则生成系统提示词
     */
    private static final String RULE_SYSTEM_PROMPT = """
            你是一位数据质量专家，负责根据用户需求生成数据质量检测规则。

            ## 规则类型说明
            - COMPLETENESS: 完整性检查，检查字段是否为空
            - UNIQUENESS: 唯一性检查，检查字段值是否唯一
            - FORMAT: 格式检查，检查字段格式是否符合规范（如身份证号、手机号、邮箱等）
            - VALUE_RANGE: 值域检查，检查字段值是否在指定范围内
            - CONSISTENCY: 一致性检查，检查关联字段数据是否一致
            - ACCURACY: 准确性检查，检查数据是否准确（如日期范围、逻辑校验等）
            - TIMELINESS: 及时性检查，检查数据更新是否及时

            ## 输出格式
            请以JSON数组格式输出规则配置，每个规则包含以下字段：
            ```json
            [
              {
                "ruleName": "规则名称",
                "ruleType": "规则类型",
                "columnName": "字段名",
                "ruleExpression": "规则表达式",
                "errorMessage": "错误提示信息",
                "severity": "严重级别(ERROR/WARNING/INFO)"
              }
            ]
            ```

            ## 表结构信息
            %s

            ## 已有规则模板
            %s
            """;

    @Override
    public String getName() {
        return "quality_rule_generator";
    }

    @Override
    public String getDescription() {
        return "根据需求描述生成数据质量规则配置。会自动分析表结构，匹配规则模板，生成符合规范的规则。";
    }

    @Override
    public JSONObject getInputSchema() {
        JSONObject schema = new JSONObject();
        schema.put("type", "object");

        JSONObject properties = new JSONObject();
        properties.put("description", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "质量规则需求描述，例如：为患者信息表设置身份证号格式检查、手机号格式检查"));
        properties.put("datasource_id", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "数据源ID"));
        properties.put("table_name", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "表名"));
        properties.put("rule_types", new JSONObject()
                .fluentPut("type", "array")
                .fluentPut("items", new JSONObject()
                        .fluentPut("type", "string")
                        .fluentPut("enum", List.of("COMPLETENESS", "UNIQUENESS", "FORMAT", "VALUE_RANGE", "CONSISTENCY", "ACCURACY", "TIMELINESS")))
                .fluentPut("description", "规则类型列表(可选)，不指定则自动推断"));
        properties.put("auto_create", new JSONObject()
                .fluentPut("type", "boolean")
                .fluentPut("description", "是否自动创建规则到系统")
                .fluentPut("default", false));

        schema.put("properties", properties);
        schema.put("required", List.of("description", "datasource_id", "table_name"));
        return schema;
    }

    @Override
    public ToolResult execute(JSONObject params, ToolContext context) {
        long startTime = System.currentTimeMillis();

        try {
            String description = params.getString("description");
            String datasourceId = params.getString("datasource_id");
            String tableName = params.getString("table_name");
            List<String> ruleTypes = params.getList("rule_types", String.class);
            boolean autoCreate = params.getBooleanValue("auto_create");

            log.info("生成质量规则 - datasourceId: {}, tableName: {}", datasourceId, tableName);

            // 1. 获取表结构信息
            String schemaInfo = buildSchemaInfo(datasourceId, tableName);

            // 2. 获取规则模板
            String templateInfo = buildTemplateInfo();

            // 3. 构建用户提示词
            String userPrompt = buildUserPrompt(description, tableName, ruleTypes);

            // 4. 获取模型配置
            ModelConfig modelConfig = modelConfigService.getDefaultModel();
            if (modelConfig == null) {
                return ToolResult.error("未配置默认模型，请先在模型配置中添加模型");
            }

            // 5. 获取LLM适配器
            LlmAdapter adapter = llmAdapterFactory.getAdapter(modelConfig);

            // 6. 调用LLM生成规则
            String systemPrompt = String.format(RULE_SYSTEM_PROMPT, schemaInfo, templateInfo);
            String ruleResponse = adapter.chat(systemPrompt, userPrompt, modelConfig);

            // 7. 解析规则JSON
            List<JSONObject> rules = parseRules(ruleResponse);

            // 8. 如果自动创建，则调用质量服务创建规则
            List<String> createdRuleIds = new ArrayList<>();
            if (autoCreate) {
                for (JSONObject rule : rules) {
                    try {
                        // 补充必要字段
                        rule.put("datasourceId", datasourceId);
                        rule.put("tableName", tableName);

                        String ruleId = qualityRpcService.createRule(rule);
                        createdRuleIds.add(ruleId);
                        rule.put("id", ruleId);
                        log.info("创建规则成功: {}", ruleId);
                    } catch (Exception e) {
                        log.warn("创建规则失败: {}", e.getMessage());
                        rule.put("createError", e.getMessage());
                    }
                }
            }

            // 9. 构建结果
            JSONObject result = new JSONObject();
            result.put("description", description);
            result.put("datasourceId", datasourceId);
            result.put("tableName", tableName);
            result.put("rules", rules);
            result.put("ruleCount", rules.size());
            result.put("autoCreated", autoCreate);
            result.put("createdRuleIds", createdRuleIds);
            result.put("executionTime", System.currentTimeMillis() - startTime);

            log.info("质量规则生成成功 - 生成 {} 条规则，耗时 {}ms",
                    rules.size(), result.getLong("executionTime"));

            return ToolResult.success(result);

        } catch (Exception e) {
            log.error("质量规则生成失败: {}", e.getMessage(), e);
            return ToolResult.error("质量规则生成失败: " + e.getMessage());
        }
    }

    /**
     * 构建表结构信息
     */
    private String buildSchemaInfo(String datasourceId, String tableName) {
        StringBuilder schemaBuilder = new StringBuilder();

        try {
            // 获取表元数据
            JSONObject tableMeta = metadataRpcService.getTableMetadata(datasourceId, tableName);
            if (tableMeta != null) {
                String comment = tableMeta.getString("tableComment");
                schemaBuilder.append("表: ").append(tableName);
                if (comment != null && !comment.isEmpty()) {
                    schemaBuilder.append(" (").append(comment).append(")");
                }
                schemaBuilder.append("\n\n");
            }

            // 获取字段信息
            List<JSONObject> columns = metadataRpcService.getTableColumns(datasourceId, tableName);
            if (columns != null && !columns.isEmpty()) {
                schemaBuilder.append("字段列表:\n");
                for (JSONObject col : columns) {
                    String colName = col.getString("columnName");
                    String colType = col.getString("columnType");
                    String colComment = col.getString("columnComment");
                    Boolean nullable = col.getBoolean("isNullable");

                    schemaBuilder.append("  - ").append(colName);
                    schemaBuilder.append(" (").append(colType).append(")");
                    if (nullable != null && !nullable) {
                        schemaBuilder.append(" [必填]");
                    }
                    if (colComment != null && !colComment.isEmpty()) {
                        schemaBuilder.append(" -- ").append(colComment);
                    }
                    schemaBuilder.append("\n");
                }
            }

        } catch (Exception e) {
            log.warn("获取表结构信息失败: {}", e.getMessage());
            schemaBuilder.append("表: ").append(tableName).append(" (获取结构信息失败)");
        }

        return schemaBuilder.toString();
    }

    /**
     * 构建规则模板信息
     */
    private String buildTemplateInfo() {
        StringBuilder templateBuilder = new StringBuilder();

        try {
            List<JSONObject> templates = qualityRpcService.listRuleTemplates();
            if (templates != null && !templates.isEmpty()) {
                templateBuilder.append("可用规则模板:\n");
                for (JSONObject template : templates) {
                    String id = template.getString("id");
                    String name = template.getString("templateName");
                    String type = template.getString("ruleType");
                    String desc = template.getString("description");

                    templateBuilder.append("  - [").append(id).append("] ");
                    templateBuilder.append(name).append(" (").append(type).append(")");
                    if (desc != null && !desc.isEmpty()) {
                        templateBuilder.append(": ").append(desc);
                    }
                    templateBuilder.append("\n");
                }
            }
        } catch (Exception e) {
            log.warn("获取规则模板失败: {}", e.getMessage());
        }

        return templateBuilder.toString();
    }

    /**
     * 构建用户提示词
     */
    private String buildUserPrompt(String description, String tableName, List<String> ruleTypes) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据以下需求为表 ").append(tableName).append(" 生成数据质量规则：\n\n");
        prompt.append("需求：").append(description).append("\n\n");

        if (ruleTypes != null && !ruleTypes.isEmpty()) {
            prompt.append("限制规则类型：").append(String.join(", ", ruleTypes)).append("\n\n");
        }

        prompt.append("请直接输出JSON数组格式的规则配置：");
        return prompt.toString();
    }

    /**
     * 解析规则JSON
     */
    private List<JSONObject> parseRules(String response) {
        List<JSONObject> rules = new ArrayList<>();

        if (response == null || response.isEmpty()) {
            return rules;
        }

        try {
            // 尝试提取JSON数组
            String jsonStr = response;

            // 如果包含代码块，提取内容
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

            // 解析JSON
            if (jsonStr.startsWith("[")) {
                JSONArray array = JSONArray.parseArray(jsonStr);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject rule = array.getJSONObject(i);
                    // 设置默认值
                    if (!rule.containsKey("severity")) {
                        rule.put("severity", "ERROR");
                    }
                    if (!rule.containsKey("status")) {
                        rule.put("status", 1);
                    }
                    rules.add(rule);
                }
            } else if (jsonStr.startsWith("{")) {
                // 单个规则对象
                JSONObject rule = JSONObject.parseObject(jsonStr);
                if (!rule.containsKey("severity")) {
                    rule.put("severity", "ERROR");
                }
                rules.add(rule);
            }

        } catch (Exception e) {
            log.warn("解析规则JSON失败: {}", e.getMessage());
        }

        return rules;
    }
}