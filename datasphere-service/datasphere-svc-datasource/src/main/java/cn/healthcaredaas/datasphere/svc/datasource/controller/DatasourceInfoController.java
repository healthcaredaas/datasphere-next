package cn.healthcaredaas.datasphere.svc.datasource.controller;

import cn.healthcaredaas.datasphere.core.dto.ColumnMeta;
import cn.healthcaredaas.datasphere.svc.datasource.entity.DatasourceInfo;
import cn.healthcaredaas.datasphere.svc.datasource.service.DatasourceInfoService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据源信息控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/datasources")
@RequiredArgsConstructor
@Tag(name = "数据源管理", description = "数据源管理相关接口")
public class DatasourceInfoController {

    private final DatasourceInfoService datasourceInfoService;

    @Operation(summary = "分页查询数据源列表")
    @GetMapping
    public IPage<DatasourceInfo> page(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") long size,
            DatasourceInfo params) {
        return datasourceInfoService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取数据源详情")
    @GetMapping("/{id}")
    public DatasourceInfo getById(@PathVariable("id") String id) {
        return datasourceInfoService.getById(id);
    }

    @Operation(summary = "新增数据源")
    @PostMapping
    public DatasourceInfo save(@RequestBody @Validated DatasourceInfo datasourceInfo) {
        datasourceInfoService.save(datasourceInfo);
        return datasourceInfo;
    }

    @Operation(summary = "更新数据源")
    @PutMapping("/{id}")
    public DatasourceInfo update(@PathVariable("id") String id, @RequestBody @Validated DatasourceInfo datasourceInfo) {
        datasourceInfo.setId(id);
        datasourceInfoService.updateById(datasourceInfo);
        return datasourceInfoService.getById(id);
    }

    @Operation(summary = "删除数据源")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        datasourceInfoService.removeById(id);
    }

    @Operation(summary = "批量删除数据源")
    @DeleteMapping
    public void batchDelete(@RequestParam List<String> ids) {
        datasourceInfoService.removeByIds(ids);
    }

    @Operation(summary = "获取数据源配置信息")
    @GetMapping("/{id}/config")
    public JSONObject getDsConfig(@PathVariable("id") String id) {
        return datasourceInfoService.getDsConfig(id);
    }

    @Operation(summary = "数据源连接测试")
    @PostMapping("/test")
    public String testDatasource(@RequestBody @Validated DatasourceInfo datasourceInfo) {
        datasourceInfoService.testJdbcConnection(datasourceInfo);
        return "连接成功";
    }

    @Operation(summary = "获取数据源所有表")
    @GetMapping("/{id}/tables")
    public List<String> listTables(@PathVariable("id") String id) {
        return datasourceInfoService.getTables(id);
    }

    @Operation(summary = "获取表的所有字段")
    @GetMapping("/{id}/tables/{tableName}/columns")
    public List<ColumnMeta> listColumns(
            @PathVariable("id") String id,
            @PathVariable("tableName") String tableName) {
        return datasourceInfoService.getColumns(id, tableName);
    }

    @Operation(summary = "获取SQL查询的列信息")
    @PostMapping("/{id}/sql/columns")
    public List<ColumnMeta> getSqlColumns(
            @PathVariable("id") String id,
            @RequestBody String sql) {
        return datasourceInfoService.getSqlColumns(id, sql);
    }
}
