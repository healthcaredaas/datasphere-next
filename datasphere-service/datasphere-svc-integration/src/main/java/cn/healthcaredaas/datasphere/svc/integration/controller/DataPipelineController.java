package cn.healthcaredaas.datasphere.svc.integration.controller;

import cn.healthcaredaas.datasphere.svc.integration.entity.DataPipeline;
import cn.healthcaredaas.datasphere.svc.integration.service.DataPipelineService;
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
 * 数据管道控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/integration/pipelines")
@RequiredArgsConstructor
@Tag(name = "数据管道管理", description = "数据管道管理相关接口")
public class DataPipelineController {

    private final DataPipelineService dataPipelineService;

    @Operation(summary = "分页查询数据管道列表")
    @GetMapping
    public IPage<DataPipeline> page(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") long size,
            DataPipeline params) {
        return dataPipelineService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取数据管道详情")
    @GetMapping("/{id}")
    public DataPipeline getById(@PathVariable("id") String id) {
        return dataPipelineService.getById(id);
    }

    @Operation(summary = "根据项目查询管道")
    @GetMapping("/by-project/{projectId}")
    public List<DataPipeline> listByProject(@PathVariable("projectId") String projectId) {
        return dataPipelineService.listByProject(projectId);
    }

    @Operation(summary = "新增数据管道")
    @PostMapping
    public DataPipeline save(@RequestBody @Validated DataPipeline dataPipeline) {
        dataPipelineService.save(dataPipeline);
        return dataPipeline;
    }

    @Operation(summary = "更新数据管道")
    @PutMapping("/{id}")
    public DataPipeline update(@PathVariable("id") String id, @RequestBody @Validated DataPipeline dataPipeline) {
        dataPipeline.setId(id);
        dataPipelineService.updateById(dataPipeline);
        return dataPipelineService.getById(id);
    }

    @Operation(summary = "删除数据管道")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        dataPipelineService.removeById(id);
    }

    @Operation(summary = "批量删除数据管道")
    @DeleteMapping
    public void batchDelete(@RequestParam List<String> ids) {
        dataPipelineService.removeByIds(ids);
    }
}
