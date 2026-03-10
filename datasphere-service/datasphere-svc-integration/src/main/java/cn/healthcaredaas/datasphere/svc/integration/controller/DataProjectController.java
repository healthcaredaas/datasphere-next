package cn.healthcaredaas.datasphere.svc.integration.controller;

import cn.healthcaredaas.datasphere.svc.integration.entity.DataProject;
import cn.healthcaredaas.datasphere.svc.integration.service.DataProjectService;
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
 * 数据项目控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/integration/projects")
@RequiredArgsConstructor
@Tag(name = "数据项目管理", description = "数据项目管理相关接口")
public class DataProjectController {

    private final DataProjectService dataProjectService;

    @Operation(summary = "分页查询数据项目列表")
    @GetMapping
    public IPage<DataProject> page(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") long size,
            DataProject params) {
        return dataProjectService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取数据项目详情")
    @GetMapping("/{id}")
    public DataProject getById(@PathVariable("id") String id) {
        return dataProjectService.getById(id);
    }

    @Operation(summary = "新增数据项目")
    @PostMapping
    public DataProject save(@RequestBody @Validated DataProject dataProject) {
        dataProjectService.save(dataProject);
        return dataProject;
    }

    @Operation(summary = "更新数据项目")
    @PutMapping("/{id}")
    public DataProject update(@PathVariable("id") String id, @RequestBody @Validated DataProject dataProject) {
        dataProject.setId(id);
        dataProjectService.updateById(dataProject);
        return dataProjectService.getById(id);
    }

    @Operation(summary = "删除数据项目")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        dataProjectService.removeById(id);
    }

    @Operation(summary = "批量删除数据项目")
    @DeleteMapping
    public void batchDelete(@RequestParam List<String> ids) {
        dataProjectService.removeByIds(ids);
    }
}
