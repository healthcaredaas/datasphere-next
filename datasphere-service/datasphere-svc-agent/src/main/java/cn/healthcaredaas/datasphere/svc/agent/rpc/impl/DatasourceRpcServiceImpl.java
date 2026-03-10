package cn.healthcaredaas.datasphere.svc.agent.rpc.impl;

import cn.healthcaredaas.datasphere.svc.agent.rpc.DatasourceRpcService;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 数据源服务本地实现（占位实现）
 * 实际生产环境中应通过Dubbo/Feign调用远程服务
 *
 * @author chenpan
 */
@Slf4j
@Component
public class DatasourceRpcServiceImpl implements DatasourceRpcService {

    @Override
    public List<JSONObject> listDatasources(String tenantId) {
        log.info("RPC调用: listDatasources, tenantId={}", tenantId);
        // TODO: 通过Dubbo调用datasource服务
        return List.of(
                createMockDatasource("ds_001", "HIS生产库", "MySQL"),
                createMockDatasource("ds_002", "LIS检验系统", "Oracle"),
                createMockDatasource("ds_003", "数据中心", "MySQL")
        );
    }

    @Override
    public JSONObject getDatasource(String datasourceId) {
        log.info("RPC调用: getDatasource, id={}", datasourceId);
        return createMockDatasource(datasourceId, "数据源", "MySQL");
    }

    @Override
    public boolean testConnection(String datasourceId) {
        log.info("RPC调用: testConnection, id={}", datasourceId);
        return true;
    }

    @Override
    public SqlResult executeQuery(String datasourceId, String sql, int limit) {
        log.info("RPC调用: executeQuery, datasourceId={}, sql={}", datasourceId, sql);
        long startTime = System.currentTimeMillis();

        // 模拟执行
        return new SqlResult(
                true,
                "查询成功",
                List.of("id", "name", "status"),
                List.of(
                        Map.of("id", "1", "name", "测试数据1", "status", "1"),
                        Map.of("id", "2", "name", "测试数据2", "status", "0")
                ),
                2,
                System.currentTimeMillis() - startTime
        );
    }

    @Override
    public List<JSONObject> listTables(String datasourceId) {
        log.info("RPC调用: listTables, datasourceId={}", datasourceId);
        return List.of(
                createMockTable("patient_info", "患者信息表"),
                createMockTable("visit_record", "就诊记录表"),
                createMockTable("order_info", "医嘱信息表")
        );
    }

    @Override
    public JSONObject getTableSchema(String datasourceId, String tableName) {
        log.info("RPC调用: getTableSchema, datasourceId={}, tableName={}", datasourceId, tableName);

        JSONObject schema = new JSONObject();
        schema.put("tableName", tableName);
        schema.put("datasourceId", datasourceId);
        schema.put("columns", List.of(
                createMockColumn("id", "VARCHAR", "主键", true),
                createMockColumn("name", "VARCHAR", "名称", false),
                createMockColumn("status", "INT", "状态", false),
                createMockColumn("create_time", "DATETIME", "创建时间", false)
        ));
        return schema;
    }

    private JSONObject createMockDatasource(String id, String name, String type) {
        JSONObject ds = new JSONObject();
        ds.put("id", id);
        ds.put("name", name);
        ds.put("type", type);
        ds.put("status", "ONLINE");
        return ds;
    }

    private JSONObject createMockTable(String name, String comment) {
        JSONObject table = new JSONObject();
        table.put("tableName", name);
        table.put("tableComment", comment);
        return table;
    }

    private JSONObject createMockColumn(String name, String type, String comment, boolean primaryKey) {
        JSONObject column = new JSONObject();
        column.put("columnName", name);
        column.put("columnType", type);
        column.put("columnComment", comment);
        column.put("primaryKey", primaryKey);
        return column;
    }
}