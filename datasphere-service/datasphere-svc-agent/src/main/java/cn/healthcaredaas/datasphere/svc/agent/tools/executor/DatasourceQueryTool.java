package cn.healthcaredaas.datasphere.svc.agent.tools.executor;

import cn.healthcaredaas.datasphere.svc.agent.rpc.DatasourceRpcService;
import cn.healthcaredaas.datasphere.svc.agent.tools.Tool;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolContext;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolResult;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据源查询工具
 * 查询数据源列表和详情信息
 *
 * @author chenpan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatasourceQueryTool implements Tool {

    private final DatasourceRpcService datasourceRpcService;

    @Override
    public String getName() {
        return "datasource_query";
    }

    @Override
    public String getDescription() {
        return "查询数据源列表和详情信息，支持连接测试。可获取数据源的基本信息、连接状态、表列表等。";
    }

    @Override
    public JSONObject getInputSchema() {
        JSONObject schema = new JSONObject();
        schema.put("type", "object");

        JSONObject properties = new JSONObject();
        properties.put("query_type", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "查询类型")
                .fluentPut("enum", List.of("LIST", "DETAIL", "TABLES", "SCHEMA", "TEST_CONNECTION")));
        properties.put("datasource_id", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "数据源ID(查询详情、表列表、Schema、测试连接时需要)"));
        properties.put("table_name", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "表名(查询Schema时需要)"));

        schema.put("properties", properties);
        schema.put("required", List.of("query_type"));
        return schema;
    }

    @Override
    public ToolResult execute(JSONObject params, ToolContext context) {
        long startTime = System.currentTimeMillis();

        try {
            String queryType = params.getString("query_type");
            String datasourceId = params.getString("datasource_id");
            String tableName = params.getString("table_name");
            String tenantId = context.getTenantId();

            log.info("查询数据源 - queryType: {}, datasourceId: {}", queryType, datasourceId);

            JSONObject result = new JSONObject();
            result.put("queryType", queryType);

            switch (queryType) {
                case "LIST" -> {
                    // 查询数据源列表
                    List<JSONObject> datasources = datasourceRpcService.listDatasources(tenantId);
                    result.put("datasources", datasources);
                    result.put("count", datasources != null ? datasources.size() : 0);
                }
                case "DETAIL" -> {
                    // 查询数据源详情
                    if (datasourceId == null || datasourceId.isEmpty()) {
                        return ToolResult.error("查询数据源详情时数据源ID不能为空");
                    }
                    JSONObject datasource = datasourceRpcService.getDatasource(datasourceId);
                    result.put("datasourceId", datasourceId);
                    result.put("datasource", datasource);
                }
                case "TABLES" -> {
                    // 查询数据源表列表
                    if (datasourceId == null || datasourceId.isEmpty()) {
                        return ToolResult.error("查询表列表时数据源ID不能为空");
                    }
                    List<JSONObject> tables = datasourceRpcService.listTables(datasourceId);
                    result.put("datasourceId", datasourceId);
                    result.put("tables", tables);
                    result.put("count", tables != null ? tables.size() : 0);
                }
                case "SCHEMA" -> {
                    // 查询表结构
                    if (datasourceId == null || datasourceId.isEmpty()) {
                        return ToolResult.error("查询表结构时数据源ID不能为空");
                    }
                    if (tableName == null || tableName.isEmpty()) {
                        return ToolResult.error("查询表结构时表名不能为空");
                    }
                    JSONObject schema = datasourceRpcService.getTableSchema(datasourceId, tableName);
                    result.put("datasourceId", datasourceId);
                    result.put("tableName", tableName);
                    result.put("schema", schema);
                }
                case "TEST_CONNECTION" -> {
                    // 测试连接
                    if (datasourceId == null || datasourceId.isEmpty()) {
                        return ToolResult.error("测试连接时数据源ID不能为空");
                    }
                    boolean connected = datasourceRpcService.testConnection(datasourceId);
                    result.put("datasourceId", datasourceId);
                    result.put("connected", connected);
                    result.put("status", connected ? "ONLINE" : "OFFLINE");
                }
                default -> {
                    return ToolResult.error("不支持的查询类型: " + queryType);
                }
            }

            result.put("executionTime", System.currentTimeMillis() - startTime);
            log.info("数据源查询成功 - 耗时 {}ms", result.getLong("executionTime"));

            return ToolResult.success(result);

        } catch (Exception e) {
            log.error("数据源查询失败: {}", e.getMessage(), e);
            return ToolResult.error("数据源查询失败: " + e.getMessage());
        }
    }
}