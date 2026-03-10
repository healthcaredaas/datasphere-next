package cn.healthcaredaas.datasphere.svc.agent.tools.executor;

import cn.healthcaredaas.datasphere.svc.agent.rpc.DatasourceRpcService;
import cn.healthcaredaas.datasphere.svc.agent.rpc.DatasourceRpcService.SqlResult;
import cn.healthcaredaas.datasphere.svc.agent.tools.Tool;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolContext;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolResult;
import cn.healthcaredaas.datasphere.svc.agent.util.SqlSecurityChecker;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SQL执行工具
 * 执行SQL查询并返回结果
 *
 * @author chenpan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SqlExecutionTool implements Tool {

    private final DatasourceRpcService datasourceRpcService;

    /**
     * 最大返回行数
     */
    private static final int MAX_ROWS = 10000;

    /**
     * 默认返回行数
     */
    private static final int DEFAULT_ROWS = 100;

    @Override
    public String getName() {
        return "sql_executor";
    }

    @Override
    public String getDescription() {
        return "执行SQL查询并返回结果。支持SELECT查询，自动进行安全检查和结果限制。";
    }

    @Override
    public JSONObject getInputSchema() {
        JSONObject schema = new JSONObject();
        schema.put("type", "object");

        JSONObject properties = new JSONObject();
        properties.put("datasource_id", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "数据源ID"));
        properties.put("sql", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "要执行的SQL查询语句"));
        properties.put("limit", new JSONObject()
                .fluentPut("type", "integer")
                .fluentPut("description", "返回结果行数限制，默认100，最大10000")
                .fluentPut("default", DEFAULT_ROWS)
                .fluentPut("maximum", MAX_ROWS));

        schema.put("properties", properties);
        schema.put("required", List.of("datasource_id", "sql"));
        return schema;
    }

    @Override
    public ToolResult execute(JSONObject params, ToolContext context) {
        long startTime = System.currentTimeMillis();

        try {
            String datasourceId = params.getString("datasource_id");
            String sql = params.getString("sql");
            int limit = params.getIntValue("limit", DEFAULT_ROWS);

            log.info("执行SQL查询 - datasourceId: {}, limit: {}", datasourceId, limit);
            log.debug("SQL: {}", sql);

            // 1. 参数校验
            if (datasourceId == null || datasourceId.isEmpty()) {
                return ToolResult.error("数据源ID不能为空");
            }
            if (sql == null || sql.trim().isEmpty()) {
                return ToolResult.error("SQL语句不能为空");
            }

            // 2. 安全限制：最大行数
            if (limit <= 0) {
                limit = DEFAULT_ROWS;
            }
            if (limit > MAX_ROWS) {
                limit = MAX_ROWS;
                log.info("限制返回行数为最大值: {}", MAX_ROWS);
            }

            // 3. SQL安全检查
            String securityCheck = SqlSecurityChecker.checkSimple(sql);
            if (securityCheck != null) {
                log.warn("SQL安全检查未通过: {}", securityCheck);
                return ToolResult.error("SQL安全检查未通过: " + securityCheck);
            }

            // 4. 只允许SELECT语句
            String trimmedSql = sql.trim().toUpperCase();
            if (!trimmedSql.startsWith("SELECT") && !trimmedSql.startsWith("WITH")) {
                return ToolResult.error("只允许执行SELECT查询语句，不允许修改数据操作");
            }

            // 5. 添加LIMIT（如果没有）
            if (!trimmedSql.contains("LIMIT")) {
                sql = addLimitClause(sql, limit);
                log.debug("添加LIMIT: {}", sql);
            }

            // 6. 调用数据源服务执行SQL
            SqlResult sqlResult = datasourceRpcService.executeQuery(datasourceId, sql, limit);

            // 7. 构建返回结果
            JSONObject result = new JSONObject();
            result.put("datasourceId", datasourceId);
            result.put("sql", sql);
            result.put("success", sqlResult.success());

            if (sqlResult.success()) {
                result.put("rowCount", sqlResult.rowCount());
                result.put("columns", sqlResult.columns());
                result.put("data", sqlResult.data());
                result.put("executionTime", sqlResult.executionTime());

                log.info("SQL执行成功 - 返回 {} 行数据，耗时 {}ms",
                        sqlResult.rowCount(), sqlResult.executionTime());
            } else {
                result.put("message", sqlResult.message());
                result.put("rowCount", 0);
                result.put("columns", List.of());
                result.put("data", List.of());

                log.warn("SQL执行失败: {}", sqlResult.message());
            }

            result.put("totalTime", System.currentTimeMillis() - startTime);
            return ToolResult.success(result);

        } catch (Exception e) {
            log.error("SQL执行异常: {}", e.getMessage(), e);
            return ToolResult.error("SQL执行异常: " + e.getMessage());
        }
    }

    /**
     * 添加LIMIT子句
     */
    private String addLimitClause(String sql, int limit) {
        // 移除末尾分号
        String trimmed = sql.trim();
        if (trimmed.endsWith(";")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1).trim();
        }

        // 添加LIMIT
        return trimmed + " LIMIT " + limit;
    }
}