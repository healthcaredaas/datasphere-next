package cn.healthcaredaas.datasphere.svc.agent.tools.executor;

import cn.healthcaredaas.datasphere.svc.agent.entity.ModelConfig;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapter;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapterFactory;
import cn.healthcaredaas.datasphere.svc.agent.rpc.MetadataRpcService;
import cn.healthcaredaas.datasphere.svc.agent.service.ModelConfigService;
import cn.healthcaredaas.datasphere.svc.agent.tools.Tool;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolContext;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolResult;
import cn.healthcaredaas.datasphere.svc.agent.util.SqlSecurityChecker;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SQL生成工具
 * 根据自然语言描述生成SQL查询语句
 *
 * @author chenpan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SqlGeneratorTool implements Tool {

    private final MetadataRpcService metadataRpcService;
    private final LlmAdapterFactory llmAdapterFactory;
    private final ModelConfigService modelConfigService;

    /**
     * SQL生成系统提示词
     */
    private static final String SQL_SYSTEM_PROMPT = """
            你是一位专业的SQL专家，负责根据用户的自然语言描述生成准确、高效的SQL查询语句。

            ## 规则要求
            1. 只生成SELECT查询语句，不允许生成INSERT、UPDATE、DELETE等修改数据的语句
            2. 生成的SQL必须符合数据库的语法规范
            3. 优先使用标准SQL语法，避免使用数据库特有的函数（除非明确指定）
            4. 对于复杂的查询需求，请添加适当的注释说明
            5. 注意SQL注入防护，不要在SQL中拼接用户输入
            6. 合理使用索引字段进行查询优化
            7. 对于大表查询，建议添加LIMIT限制

            ## 输出格式
            请直接输出SQL语句，不需要解释说明。如果需要说明，请在SQL中使用注释。

            ## 表结构信息
            %s
            """;

    @Override
    public String getName() {
        return "sql_generator";
    }

    @Override
    public String getDescription() {
        return "根据自然语言描述生成SQL查询语句。会自动查询表结构信息，生成符合规范的SQL。";
    }

    @Override
    public JSONObject getInputSchema() {
        JSONObject schema = new JSONObject();
        schema.put("type", "object");

        JSONObject properties = new JSONObject();
        properties.put("datasource_id", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "数据源ID"));
        properties.put("natural_language", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "自然语言查询描述，例如：查询最近一个月门诊量前10的科室"));
        properties.put("tables", new JSONObject()
                .fluentPut("type", "array")
                .fluentPut("items", new JSONObject().fluentPut("type", "string"))
                .fluentPut("description", "相关表名列表(可选)，如果不提供会自动搜索相关表"));
        properties.put("options", new JSONObject()
                .fluentPut("type", "object")
                .fluentPut("description", "生成选项")
                .fluentPut("properties", new JSONObject()
                        .fluentPut("include_explanation", new JSONObject()
                                .fluentPut("type", "boolean")
                                .fluentPut("description", "是否包含解释说明")
                                .fluentPut("default", true))
                        .fluentPut("validate_syntax", new JSONObject()
                                .fluentPut("type", "boolean")
                                .fluentPut("description", "是否验证SQL语法")
                                .fluentPut("default", true))
                        .fluentPut("max_rows", new JSONObject()
                                .fluentPut("type", "integer")
                                .fluentPut("description", "返回结果最大行数限制")
                                .fluentPut("default", 1000))));

        schema.put("properties", properties);
        schema.put("required", List.of("datasource_id", "natural_language"));
        return schema;
    }

    @Override
    public ToolResult execute(JSONObject params, ToolContext context) {
        long startTime = System.currentTimeMillis();

        try {
            String datasourceId = params.getString("datasource_id");
            String naturalLanguage = params.getString("natural_language");
            List<String> tables = params.getList("tables", String.class);
            JSONObject options = params.getJSONObject("options");

            log.info("生成SQL - datasourceId: {}, naturalLanguage: {}", datasourceId, naturalLanguage);

            // 1. 获取表结构信息
            String schemaInfo = buildSchemaInfo(datasourceId, tables, naturalLanguage);

            // 2. 构建用户提示词
            String userPrompt = buildUserPrompt(naturalLanguage, options);

            // 3. 获取模型配置
            ModelConfig modelConfig = modelConfigService.getDefaultModel();
            if (modelConfig == null) {
                return ToolResult.error("未配置默认模型，请先在模型配置中添加模型");
            }

            // 4. 获取LLM适配器
            LlmAdapter adapter = llmAdapterFactory.getAdapter(modelConfig);

            // 5. 调用LLM生成SQL
            String systemPrompt = String.format(SQL_SYSTEM_PROMPT, schemaInfo);
            String sqlResponse = adapter.chat(systemPrompt, userPrompt, modelConfig);

            // 6. 提取SQL语句
            String sql = extractSql(sqlResponse);

            // 7. SQL安全检查
            String securityCheck = SqlSecurityChecker.checkSimple(sql);
            if (securityCheck != null) {
                return ToolResult.error("SQL安全检查未通过: " + securityCheck);
            }

            // 8. 格式化SQL
            sql = formatSql(sql);

            // 9. 构建结果
            JSONObject result = new JSONObject();
            result.put("datasourceId", datasourceId);
            result.put("naturalLanguage", naturalLanguage);
            result.put("sql", sql);
            result.put("explanation", generateExplanation(sql, naturalLanguage));
            result.put("tables", tables != null ? tables : List.of());
            result.put("executionTime", System.currentTimeMillis() - startTime);

            log.info("SQL生成成功 - 耗时: {}ms", result.getLong("executionTime"));
            return ToolResult.success(result);

        } catch (Exception e) {
            log.error("SQL生成失败: {}", e.getMessage(), e);
            return ToolResult.error("SQL生成失败: " + e.getMessage());
        }
    }

    /**
     * 构建表结构信息
     */
    private String buildSchemaInfo(String datasourceId, List<String> tables, String naturalLanguage) {
        StringBuilder schemaBuilder = new StringBuilder();

        try {
            // 如果未指定表，尝试搜索相关表
            if (tables == null || tables.isEmpty()) {
                // 从自然语言中提取关键词搜索
                String keyword = extractKeyword(naturalLanguage);
                List<JSONObject> searchResults = metadataRpcService.searchTables(datasourceId, keyword);
                tables = searchResults.stream()
                        .map(t -> t.getString("tableName"))
                        .limit(5)  // 最多取5个相关表
                        .collect(Collectors.toList());
            }

            // 获取每个表的详细结构
            for (String tableName : tables) {
                schemaBuilder.append("\n### 表: ").append(tableName).append("\n");

                // 获取表元数据
                JSONObject tableMeta = metadataRpcService.getTableMetadata(datasourceId, tableName);
                if (tableMeta != null) {
                    String comment = tableMeta.getString("tableComment");
                    if (comment != null && !comment.isEmpty()) {
                        schemaBuilder.append("说明: ").append(comment).append("\n");
                    }
                }

                // 获取字段信息
                List<JSONObject> columns = metadataRpcService.getTableColumns(datasourceId, tableName);
                if (columns != null && !columns.isEmpty()) {
                    schemaBuilder.append("字段:\n");
                    for (JSONObject col : columns) {
                        String colName = col.getString("columnName");
                        String colType = col.getString("columnType");
                        String colComment = col.getString("columnComment");
                        String isPk = col.getString("isPrimaryKey");

                        schemaBuilder.append("  - ").append(colName);
                        schemaBuilder.append(" (").append(colType).append(")");
                        if ("Y".equals(isPk) || "1".equals(isPk)) {
                            schemaBuilder.append(" [主键]");
                        }
                        if (colComment != null && !colComment.isEmpty()) {
                            schemaBuilder.append(" -- ").append(colComment);
                        }
                        schemaBuilder.append("\n");
                    }
                }

                // 获取表统计信息
                JSONObject stats = metadataRpcService.getTableStats(datasourceId, tableName);
                if (stats != null) {
                    Long rowCount = stats.getLong("rowCount");
                    if (rowCount != null) {
                        schemaBuilder.append("数据量: 约 ").append(formatNumber(rowCount)).append(" 行\n");
                    }
                }
            }

            if (schemaBuilder.length() == 0) {
                schemaBuilder.append("未找到相关表结构信息，请明确指定表名或检查数据源配置。");
            }

        } catch (Exception e) {
            log.warn("获取表结构信息失败: {}", e.getMessage());
            schemaBuilder.append("获取表结构信息失败: ").append(e.getMessage());
        }

        return schemaBuilder.toString();
    }

    /**
     * 构建用户提示词
     */
    private String buildUserPrompt(String naturalLanguage, JSONObject options) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据以下需求生成SQL查询语句：\n\n");
        prompt.append("需求：").append(naturalLanguage).append("\n\n");

        if (options != null) {
            Integer maxRows = options.getInteger("max_rows");
            if (maxRows != null) {
                prompt.append("要求：返回结果限制在 ").append(maxRows).append(" 行以内\n");
            }
        }

        prompt.append("\n请直接输出SQL语句：");
        return prompt.toString();
    }

    /**
     * 从自然语言中提取关键词
     */
    private String extractKeyword(String naturalLanguage) {
        // 简单的关键词提取：移除常见停用词
        String[] stopWords = {"查询", "统计", "分析", "获取", "找出", "显示", "列出", "最近", "本月", "上月", "今天", "昨天"};

        String keyword = naturalLanguage;
        for (String stopWord : stopWords) {
            keyword = keyword.replace(stopWord, " ");
        }

        // 提取可能的表名关键词
        String[] words = keyword.split("\\s+");
        for (String word : words) {
            if (word.length() >= 2) {
                return word;
            }
        }

        return "";
    }

    /**
     * 从LLM响应中提取SQL语句
     */
    private String extractSql(String response) {
        if (response == null || response.isEmpty()) {
            return "";
        }

        // 尝试提取代码块中的SQL
        if (response.contains("```sql")) {
            int start = response.indexOf("```sql") + 6;
            int end = response.indexOf("```", start);
            if (end > start) {
                return response.substring(start, end).trim();
            }
        }

        // 尝试提取普通代码块
        if (response.contains("```")) {
            int start = response.indexOf("```") + 3;
            // 跳过可能的语言标识
            while (start < response.length() && !Character.isWhitespace(response.charAt(start)) && response.charAt(start) != '\n') {
                start++;
            }
            int end = response.indexOf("```", start);
            if (end > start) {
                return response.substring(start, end).trim();
            }
        }

        // 直接返回，假设整个响应就是SQL
        return response.trim();
    }

    /**
     * 格式化SQL
     */
    private String formatSql(String sql) {
        // 统一关键字大写
        String[] keywords = {"SELECT", "FROM", "WHERE", "AND", "OR", "JOIN", "LEFT", "RIGHT", "INNER", "ON",
                "GROUP BY", "ORDER BY", "HAVING", "LIMIT", "OFFSET", "AS", "DISTINCT", "COUNT", "SUM",
                "AVG", "MAX", "MIN", "INSERT", "UPDATE", "DELETE", "CREATE", "ALTER", "DROP"};

        String formatted = sql;
        for (String keyword : keywords) {
            formatted = formatted.replaceAll("(?i)\\b" + keyword + "\\b", keyword);
        }

        return formatted.trim();
    }

    /**
     * 生成SQL解释说明
     */
    private String generateExplanation(String sql, String naturalLanguage) {
        StringBuilder explanation = new StringBuilder();
        explanation.append("根据您的需求「").append(naturalLanguage).append("」，生成了以下SQL查询：\n\n");

        // 分析SQL类型
        String upperSql = sql.toUpperCase().trim();
        if (upperSql.startsWith("SELECT")) {
            explanation.append("这是一个查询语句，");

            if (upperSql.contains("JOIN")) {
                explanation.append("涉及多表关联查询。");
            } else {
                explanation.append("查询单表数据。");
            }

            if (upperSql.contains("GROUP BY")) {
                explanation.append("包含分组聚合操作。");
            }

            if (upperSql.contains("ORDER BY")) {
                explanation.append("结果进行了排序。");
            }

            if (upperSql.contains("LIMIT")) {
                explanation.append("限制了返回行数。");
            }
        }

        return explanation.toString();
    }

    /**
     * 格式化数字
     */
    private String formatNumber(long num) {
        if (num >= 100000000) {
            return String.format("%.1f亿", num / 100000000.0);
        } else if (num >= 10000) {
            return String.format("%.1f万", num / 10000.0);
        }
        return String.valueOf(num);
    }
}