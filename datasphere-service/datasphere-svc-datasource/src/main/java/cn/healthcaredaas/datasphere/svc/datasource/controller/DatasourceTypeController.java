package cn.healthcaredaas.datasphere.svc.datasource.controller;

import cn.healthcaredaas.datasphere.svc.datasource.entity.DatasourceType;
import cn.healthcaredaas.datasphere.svc.datasource.service.DatasourceTypeService;
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
 * 数据源类型控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/datasource-types")
@RequiredArgsConstructor
@Tag(name = "数据源类型管理", description = "数据源类型管理相关接口")
public class DatasourceTypeController {

    private final DatasourceTypeService datasourceTypeService;

    @Operation(summary = "分页查询数据源类型列表")
    @GetMapping
    public IPage<DatasourceType> page(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") long size,
            DatasourceType params) {
        return datasourceTypeService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取数据源类型详情")
    @GetMapping("/{id}")
    public DatasourceType getById(@PathVariable("id") String id) {
        return datasourceTypeService.getById(id);
    }

    @Operation(summary = "根据类型编码获取类型")
    @GetMapping("/by-code/{dataType}")
    public DatasourceType getByDataType(@PathVariable("dataType") String dataType) {
        return datasourceTypeService.getByDataType(dataType);
    }

    @Operation(summary = "新增数据源类型")
    @PostMapping
    public DatasourceType save(@RequestBody @Validated DatasourceType datasourceType) {
        datasourceTypeService.save(datasourceType);
        return datasourceType;
    }

    @Operation(summary = "更新数据源类型")
    @PutMapping("/{id}")
    public DatasourceType update(@PathVariable("id") String id, @RequestBody @Validated DatasourceType datasourceType) {
        datasourceType.setId(id);
        datasourceTypeService.updateById(datasourceType);
        return datasourceTypeService.getById(id);
    }

    @Operation(summary = "删除数据源类型")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        datasourceTypeService.removeById(id);
    }

    @Operation(summary = "批量删除数据源类型")
    @DeleteMapping
    public void batchDelete(@RequestParam List<String> ids) {
        datasourceTypeService.removeByIds(ids);
    }

    @Operation(summary = "根据分类查询数据源类型")
    @GetMapping("/by-classify/{classifyCode}")
    public List<DatasourceType> listByClassify(@PathVariable("classifyCode") String classifyCode) {
        return datasourceTypeService.listByClassify(classifyCode);
    }
}
