package cn.healthcaredaas.datasphere.svc.agent.rpc;

import com.alibaba.fastjson2.JSONObject;

import java.util.List;

/**
 * 元数据服务接口（RPC调用）
 *
 * @author chenpan
 */
public interface MetadataRpcService {

    /**
     * 查询表元数据
     */
    JSONObject getTableMetadata(String datasourceId, String tableName);

    /**
     * 查询表字段信息
     */
    List<JSONObject> getTableColumns(String datasourceId, String tableName);

    /**
     * 搜索表
     */
    List<JSONObject> searchTables(String datasourceId, String keyword);

    /**
     * 获取表统计信息
     */
    JSONObject getTableStats(String datasourceId, String tableName);

    /**
     * 获取数据字典
     */
    List<JSONObject> getDataDictionary(String datasourceId);

    /**
     * 同步元数据
     */
    void syncMetadata(String datasourceId);

    /**
     * 获取数据源表列表
     */
    List<JSONObject> listTables(String datasourceId);
}