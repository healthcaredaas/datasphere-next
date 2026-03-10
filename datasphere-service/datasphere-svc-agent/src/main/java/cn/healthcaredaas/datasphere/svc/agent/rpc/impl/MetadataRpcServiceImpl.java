package cn.healthcaredaas.datasphere.svc.agent.rpc.impl;

import cn.healthcaredaas.datasphere.svc.agent.feign.MetadataClient;
import cn.healthcaredaas.datasphere.svc.agent.rpc.MetadataRpcService;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 元数据服务实现 - 通过Feign调用元数据服务
 *
 * @author chenpan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MetadataRpcServiceImpl implements MetadataRpcService {

    private final MetadataClient metadataClient;

    @Override
    public JSONObject getTableMetadata(String datasourceId, String tableName) {
        log.info("Feign调用: getTableMetadata, datasourceId={}, tableName={}", datasourceId, tableName);

        try {
            Map<String, Object> result = metadataClient.getTableMetadata(datasourceId, tableName);
            if (result == null || result.isEmpty()) {
                return new JSONObject();
            }
            return new JSONObject(result);
        } catch (Exception e) {
            log.error("调用元数据服务失败: {}", e.getMessage());
            return new JSONObject();
        }
    }

    @Override
    public List<JSONObject> getTableColumns(String datasourceId, String tableName) {
        log.info("Feign调用: getTableColumns, datasourceId={}, tableName={}", datasourceId, tableName);

        try {
            List<Map<String, Object>> columns = metadataClient.listTableColumns(datasourceId, tableName);
            if (columns == null) {
                return List.of();
            }
            return columns.stream()
                    .map(JSONObject::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("调用元数据服务失败: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<JSONObject> searchTables(String datasourceId, String keyword) {
        log.info("Feign调用: searchTables, datasourceId={}, keyword={}", datasourceId, keyword);

        try {
            List<Map<String, Object>> tables = metadataClient.searchTables(datasourceId, keyword);
            if (tables == null) {
                return List.of();
            }
            return tables.stream()
                    .map(JSONObject::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("调用元数据服务失败: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public JSONObject getTableStats(String datasourceId, String tableName) {
        log.info("Feign调用: getTableStats, datasourceId={}, tableName={}", datasourceId, tableName);

        try {
            Map<String, Object> result = metadataClient.getTableStats(datasourceId, tableName);
            if (result == null) {
                return new JSONObject();
            }
            return new JSONObject(result);
        } catch (Exception e) {
            log.error("调用元数据服务失败: {}", e.getMessage());
            return new JSONObject();
        }
    }

    @Override
    public List<JSONObject> getDataDictionary(String datasourceId) {
        log.info("Feign调用: getDataDictionary, datasourceId={}", datasourceId);

        try {
            List<Map<String, Object>> items = metadataClient.listByDatasource(datasourceId);
            if (items == null) {
                return List.of();
            }
            return items.stream()
                    .map(JSONObject::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("调用元数据服务失败: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public void syncMetadata(String datasourceId) {
        log.info("Feign调用: syncMetadata, datasourceId={}", datasourceId);
        // TODO: 调用元数据同步接口
    }

    @Override
    public List<JSONObject> listTables(String datasourceId) {
        log.info("Feign调用: listTables, datasourceId={}", datasourceId);

        try {
            List<Map<String, Object>> tables = metadataClient.listTables(datasourceId);
            if (tables == null) {
                return List.of();
            }
            return tables.stream()
                    .map(JSONObject::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("调用元数据服务失败: {}", e.getMessage());
            return List.of();
        }
    }
}