package cn.healthcaredaas.datasphere.svc.agent.tools.executor;

import cn.healthcaredaas.datasphere.svc.agent.rpc.MetadataRpcService;
import cn.healthcaredaas.datasphere.svc.agent.tools.Tool;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolContext;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolResult;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 元数据查询工具
 * 查询数据源的元数据信息，包括表结构、字段信息等
 *
 * @author chenpan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MetadataQueryTool implements Tool {

    private final MetadataRpcService metadataRpcService;

    @Override
    public String getName() {
        return "metadata_query";
    }

    @Override
    public String getDescription() {
        return "查询数据源的元数据信息，包括表列表、表结构、字段详情等。支持关键字搜索。";
    }

    @Override
    public JSONObject getInputSchema() {
        JSONObject schema = new JSONObject();
        schema.put("type", "object");

        JSONObject properties = new JSONObject();
        properties.put("datasource_id", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "数据源ID"));
        properties.put("query_type", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "查询类型")
                .fluentPut("enum", List.of("TABLES", "COLUMNS", "TABLE_DETAIL", "SEARCH", "STATS", "DICTIONARY")));
        properties.put("table_name", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "表名(查询TABLE_DETAIL、COLUMNS、STATS时需要)"));
        properties.put("keyword", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "搜索关键字(SEARCH类型时使用)"));

        schema.put("properties", properties);
        schema.put("required", List.of("datasource_id", "query_type"));
        return schema;
    }

    @Override
    public ToolResult execute(JSONObject params, ToolContext context) {
        long startTime = System.currentTimeMillis();

        try {
            String datasourceId = params.getString("datasource_id");
            String queryType = params.getString("query_type");
            String tableName = params.getString("table_name");
            String keyword = params.getString("keyword");

            log.info("查询元数据 - datasourceId: {}, queryType: {}, tableName: {}",
                    datasourceId, queryType, tableName);

            // 参数校验
            if (datasourceId == null || datasourceId.isEmpty()) {
                return ToolResult.error("数据源ID不能为空");
            }

            JSONObject result = new JSONObject();
            result.put("datasourceId", datasourceId);
            result.put("queryType", queryType);

            switch (queryType) {
                case "TABLES" -> {
                    // 查询表列表
                    List<JSONObject> tables = metadataRpcService.listTables(datasourceId);
                    result.put("tables", tables);
                    result.put("count", tables != null ? tables.size() : 0);
                }
                case "COLUMNS" -> {
                    // 查询表字段
                    if (tableName == null || tableName.isEmpty()) {
                        return ToolResult.error("查询字段信息时表名不能为空");
                    }
                    List<JSONObject> columns = metadataRpcService.getTableColumns(datasourceId, tableName);
                    result.put("tableName", tableName);
                    result.put("columns", columns);
                    result.put("columnCount", columns != null ? columns.size() : 0);
                }
                case "TABLE_DETAIL" -> {
                    // 查询表详情
                    if (tableName == null || tableName.isEmpty()) {
                        return ToolResult.error("查询表详情时表名不能为空");
                    }
                    JSONObject tableMeta = metadataRpcService.getTableMetadata(datasourceId, tableName);
                    List<JSONObject> columns = metadataRpcService.getTableColumns(datasourceId, tableName);
                    JSONObject stats = metadataRpcService.getTableStats(datasourceId, tableName);

                    JSONObject detail = new JSONObject();
                    detail.put("metadata", tableMeta);
                    detail.put("columns", columns);
                    detail.put("stats", stats);

                    result.put("tableName", tableName);
                    result.put("detail", detail);
                }
                case "SEARCH" -> {
                    // 搜索表
                    List<JSONObject> searchResults = metadataRpcService.searchTables(datasourceId, keyword);
                    result.put("keyword", keyword);
                    result.put("results", searchResults);
                    result.put("count", searchResults != null ? searchResults.size() : 0);
                }
                case "STATS" -> {
                    // 查询表统计信息
                    if (tableName == null || tableName.isEmpty()) {
                        return ToolResult.error("查询统计信息时表名不能为空");
                    }
                    JSONObject stats = metadataRpcService.getTableStats(datasourceId, tableName);
                    result.put("tableName", tableName);
                    result.put("stats", stats);
                }
                case "DICTIONARY" -> {
                    // 获取数据字典
                    List<JSONObject> dictionary = metadataRpcService.getDataDictionary(datasourceId);
                    result.put("dictionary", dictionary);
                    result.put("count", dictionary != null ? dictionary.size() : 0);
                }
                default -> {
                    return ToolResult.error("不支持的查询类型: " + queryType);
                }
            }

            result.put("executionTime", System.currentTimeMillis() - startTime);
            log.info("元数据查询成功 - 耗时 {}ms", result.getLong("executionTime"));

            return ToolResult.success(result);

        } catch (Exception e) {
            log.error("元数据查询失败: {}", e.getMessage(), e);
            return ToolResult.error("元数据查询失败: " + e.getMessage());
        }
    }
}