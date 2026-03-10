package cn.healthcaredaas.datasphere.svc.datasource.controller;

import cn.healthcaredaas.datasphere.svc.datasource.entity.DatasourceClassify;
import cn.healthcaredaas.datasphere.svc.datasource.service.DatasourceClassifyService;
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
 * 数据源分类控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/datasource-classifies")
@RequiredArgsConstructor
@Tag(name = "数据源分类管理", description = "数据源分类管理相关接口")
public class DatasourceClassifyController {

    private final DatasourceClassifyService datasourceClassifyService;

    @Operation(summary = "分页查询数据源分类列表")
    @GetMapping
    public IPage<DatasourceClassify> page(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") long size,
            DatasourceClassify params) {
        return datasourceClassifyService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取数据源分类详情")
    @GetMapping("/{id}")
    public DatasourceClassify getById(@PathVariable("id") String id) {
        return datasourceClassifyService.getById(id);
    }

    @Operation(summary = "根据编码获取分类")
    @GetMapping("/by-code/{classifyCode}")
    public DatasourceClassify getByClassifyCode(@PathVariable("classifyCode") String classifyCode) {
        return datasourceClassifyService.getByClassifyCode(classifyCode);
    }

    @Operation(summary = "新增数据源分类")
    @PostMapping
    public DatasourceClassify save(@RequestBody @Validated DatasourceClassify datasourceClassify) {
        datasourceClassifyService.save(datasourceClassify);
        return datasourceClassify;
    }

    @Operation(summary = "更新数据源分类")
    @PutMapping("/{id}")
    public DatasourceClassify update(@PathVariable("id") String id, @RequestBody @Validated DatasourceClassify datasourceClassify) {
        datasourceClassify.setId(id);
        datasourceClassifyService.updateById(datasourceClassify);
        return datasourceClassifyService.getById(id);
    }

    @Operation(summary = "删除数据源分类")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        datasourceClassifyService.removeById(id);
    }

    @Operation(summary = "批量删除数据源分类")
    @DeleteMapping
    public void batchDelete(@RequestParam List<String> ids) {
        datasourceClassifyService.removeByIds(ids);
    }

    @Operation(summary = "查询所有分类（按序号排序）")
    @GetMapping("/all")
    public List<DatasourceClassify> listAll() {
        return datasourceClassifyService.listAllOrderByNo();
    }
}
