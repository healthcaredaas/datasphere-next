package cn.healthcaredaas.datasphere.svc.standard.controller;

import cn.healthcaredaas.datasphere.svc.standard.entity.Dataset;
import cn.healthcaredaas.datasphere.svc.standard.service.DatasetService;
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
 * 数据集控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/standard/datasets")
@RequiredArgsConstructor
@Tag(name = "数据集管理", description = "数据集管理相关接口")
public class DatasetController {

    private final DatasetService datasetService;

    @Operation(summary = "分页查询数据集列表")
    @GetMapping
    public IPage<Dataset> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            Dataset params) {
        return datasetService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取数据集详情")
    @GetMapping("/{id}")
    public Dataset getById(@PathVariable("id") String id) {
        return datasetService.getById(id);
    }

    @Operation(summary = "新增数据集")
    @PostMapping
    public Dataset save(@RequestBody @Validated Dataset dataset) {
        datasetService.save(dataset);
        return dataset;
    }

    @Operation(summary = "更新数据集")
    @PutMapping("/{id}")
    public Dataset update(@PathVariable("id") String id, @RequestBody @Validated Dataset dataset) {
        dataset.setId(id);
        datasetService.updateById(dataset);
        return datasetService.getById(id);
    }

    @Operation(summary = "删除数据集")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        datasetService.removeById(id);
    }
}
