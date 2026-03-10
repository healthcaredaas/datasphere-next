package cn.healthcaredaas.datasphere.svc.metadata.controller;

import cn.healthcaredaas.datasphere.svc.metadata.entity.MetaItem;
import cn.healthcaredaas.datasphere.svc.metadata.service.MetaItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 元数据项控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/metadata/items")
@RequiredArgsConstructor
@Tag(name = "元数据项管理", description = "元数据项管理相关接口")
public class MetaItemController {

    private final MetaItemService metaItemService;

    @Operation(summary = "分页查询元数据项列表")
    @GetMapping
    public IPage<MetaItem> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            MetaItem params) {
        return metaItemService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "根据数据源查询元数据项")
    @GetMapping("/by-datasource/{datasourceId}")
    public List<MetaItem> listByDatasource(@PathVariable("datasourceId") String datasourceId) {
        return metaItemService.listByDatasource(datasourceId);
    }

    @Operation(summary = "获取元数据项详情")
    @GetMapping("/{id}")
    public MetaItem getById(@PathVariable("id") String id) {
        return metaItemService.getById(id);
    }

    @Operation(summary = "新增元数据项")
    @PostMapping
    public MetaItem save(@RequestBody @Validated MetaItem metaItem) {
        metaItemService.save(metaItem);
        return metaItem;
    }

    @Operation(summary = "更新元数据项")
    @PutMapping("/{id}")
    public MetaItem update(@PathVariable("id") String id, @RequestBody @Validated MetaItem metaItem) {
        metaItem.setId(id);
        metaItemService.updateById(metaItem);
        return metaItemService.getById(id);
    }

    @Operation(summary = "删除元数据项")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        metaItemService.removeById(id);
    }

    // ========== Agent服务专用接口 ==========

    @Operation(summary = "获取数据源表列表")
    @GetMapping("/tables/{datasourceId}")
    public List<Map<String, Object>> listTables(@PathVariable("datasourceId") String datasourceId) {
        LambdaQueryWrapper<MetaItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MetaItem::getDatasourceId, datasourceId)
               .eq(MetaItem::getDeleteFlag, "0")
               .isNotNull(MetaItem::getTableName)
               .select(MetaItem::getTableName, MetaItem::getDatabaseName, MetaItem::getDescription)
               .groupBy(MetaItem::getTableName, MetaItem::getDatabaseName, MetaItem::getDescription);

        List<MetaItem> items = metaItemService.list(wrapper);
        return items.stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("tableName", item.getTableName());
                    map.put("databaseName", item.getDatabaseName());
                    map.put("tableComment", item.getDescription());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Operation(summary = "获取表字段列表")
    @GetMapping("/columns/{datasourceId}/{tableName}")
    public List<Map<String, Object>> listTableColumns(
            @PathVariable("datasourceId") String datasourceId,
            @PathVariable("tableName") String tableName) {
        LambdaQueryWrapper<MetaItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MetaItem::getDatasourceId, datasourceId)
               .eq(MetaItem::getTableName, tableName)
               .eq(MetaItem::getDeleteFlag, "0")
               .orderByAsc(MetaItem::getId);

        List<MetaItem> items = metaItemService.list(wrapper);
        return items.stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("columnName", item.getColumnName());
                    map.put("columnType", item.getDataType());
                    map.put("columnComment", item.getDescription());
                    map.put("nullable", true);
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Operation(summary = "搜索表")
    @GetMapping("/search/{datasourceId}")
    public List<Map<String, Object>> searchTables(
            @PathVariable("datasourceId") String datasourceId,
            @RequestParam("keyword") String keyword) {
        LambdaQueryWrapper<MetaItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MetaItem::getDatasourceId, datasourceId)
               .eq(MetaItem::getDeleteFlag, "0")
               .and(w -> w.like(MetaItem::getTableName, keyword)
                          .or()
                          .like(MetaItem::getDescription, keyword))
               .select(MetaItem::getTableName, MetaItem::getDatabaseName, MetaItem::getDescription)
               .groupBy(MetaItem::getTableName, MetaItem::getDatabaseName, MetaItem::getDescription)
               .last("LIMIT 20");

        List<MetaItem> items = metaItemService.list(wrapper);
        return items.stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("tableName", item.getTableName());
                    map.put("databaseName", item.getDatabaseName());
                    map.put("tableComment", item.getDescription());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Operation(summary = "获取表元数据")
    @GetMapping("/table-meta/{datasourceId}/{tableName}")
    public Map<String, Object> getTableMetadata(
            @PathVariable("datasourceId") String datasourceId,
            @PathVariable("tableName") String tableName) {
        LambdaQueryWrapper<MetaItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MetaItem::getDatasourceId, datasourceId)
               .eq(MetaItem::getTableName, tableName)
               .eq(MetaItem::getDeleteFlag, "0")
               .select(MetaItem::getTableName, MetaItem::getDatabaseName, MetaItem::getDescription)
               .last("LIMIT 1");

        MetaItem item = metaItemService.getOne(wrapper);
        if (item == null) {
            return new HashMap<>();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("tableName", item.getTableName());
        result.put("databaseName", item.getDatabaseName());
        result.put("tableComment", item.getDescription());
        return result;
    }

    @Operation(summary = "获取表统计信息")
    @GetMapping("/table-stats/{datasourceId}/{tableName}")
    public Map<String, Object> getTableStats(
            @PathVariable("datasourceId") String datasourceId,
            @PathVariable("tableName") String tableName) {
        LambdaQueryWrapper<MetaItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MetaItem::getDatasourceId, datasourceId)
               .eq(MetaItem::getTableName, tableName)
               .eq(MetaItem::getDeleteFlag, "0");

        long count = metaItemService.count(wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("columnCount", count);
        return result;
    }
}
