package cn.healthcaredaas.datasphere.svc.agent.rpc;

import com.alibaba.fastjson2.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 数据源服务接口（RPC调用）
 *
 * @author chenpan
 */
public interface DatasourceRpcService {

    /**
     * 获取数据源列表
     */
    List<JSONObject> listDatasources(String tenantId);

    /**
     * 获取数据源详情
     */
    JSONObject getDatasource(String datasourceId);

    /**
     * 测试数据源连接
     */
    boolean testConnection(String datasourceId);

    /**
     * 执行SQL查询
     */
    SqlResult executeQuery(String datasourceId, String sql, int limit);

    /**
     * 获取数据源表列表
     */
    List<JSONObject> listTables(String datasourceId);

    /**
     * 获取表结构信息
     */
    JSONObject getTableSchema(String datasourceId, String tableName);

    /**
     * SQL执行结果
     */
    record SqlResult(
            boolean success,
            String message,
            List<String> columns,
            List<Map<String, Object>> data,
            int rowCount,
            long executionTime
    ) {}
}