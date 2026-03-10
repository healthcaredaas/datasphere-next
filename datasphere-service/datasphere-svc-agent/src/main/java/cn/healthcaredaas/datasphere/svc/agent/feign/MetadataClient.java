package cn.healthcaredaas.datasphere.svc.agent.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 元数据服务Feign客户端
 *
 * @author chenpan
 */
@FeignClient(name = "datasphere-svc-metadata", path = "/api/v1/metadata/items")
public interface MetadataClient {

    /**
     * 获取数据源表列表
     */
    @GetMapping("/tables/{datasourceId}")
    List<Map<String, Object>> listTables(@PathVariable("datasourceId") String datasourceId);

    /**
     * 获取表字段列表
     */
    @GetMapping("/columns/{datasourceId}/{tableName}")
    List<Map<String, Object>> listTableColumns(@PathVariable("datasourceId") String datasourceId,
                                                 @PathVariable("tableName") String tableName);

    /**
     * 搜索表
     */
    @GetMapping("/search/{datasourceId}")
    List<Map<String, Object>> searchTables(@PathVariable("datasourceId") String datasourceId,
                                            @RequestParam("keyword") String keyword);

    /**
     * 获取表元数据
     */
    @GetMapping("/table-meta/{datasourceId}/{tableName}")
    Map<String, Object> getTableMetadata(@PathVariable("datasourceId") String datasourceId,
                                          @PathVariable("tableName") String tableName);

    /**
     * 获取表统计信息
     */
    @GetMapping("/table-stats/{datasourceId}/{tableName}")
    Map<String, Object> getTableStats(@PathVariable("datasourceId") String datasourceId,
                                       @PathVariable("tableName") String tableName);

    /**
     * 根据数据源查询元数据项
     */
    @GetMapping("/by-datasource/{datasourceId}")
    List<Map<String, Object>> listByDatasource(@PathVariable("datasourceId") String datasourceId);
}