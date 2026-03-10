package cn.healthcaredaas.datasphere.svc.asset.controller;

import cn.healthcaredaas.datasphere.svc.asset.entity.DataLineage;
import cn.healthcaredaas.datasphere.svc.asset.service.DataLineageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据血缘控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/asset/lineage")
@RequiredArgsConstructor
@Tag(name = "数据血缘管理", description = "数据血缘管理相关接口")
public class DataLineageController {

    private final DataLineageService lineageService;

    @Operation(summary = "分页查询数据血缘列表")
    @GetMapping
    public IPage<DataLineage> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            DataLineage params) {
        return lineageService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取资产的上游血缘")
    @GetMapping("/upstream/{assetId}")
    public List<DataLineage> getUpstream(@PathVariable("assetId") String assetId) {
        return lineageService.getUpstreamLineage(assetId);
    }

    @Operation(summary = "获取资产的下游血缘")
    @GetMapping("/downstream/{assetId}")
    public List<DataLineage> getDownstream(@PathVariable("assetId") String assetId) {
        return lineageService.getDownstreamLineage(assetId);
    }

    @Operation(summary = "获取资产血缘图")
    @GetMapping("/graph/{assetId}")
    public Map<String, Object> getGraph(@PathVariable("assetId") String assetId) {
        return lineageService.getLineageGraph(assetId);
    }

    @Operation(summary = "获取数据血缘详情")
    @GetMapping("/{id}")
    public DataLineage getById(@PathVariable("id") String id) {
        return lineageService.getById(id);
    }

    @Operation(summary = "新增数据血缘")
    @PostMapping
    public DataLineage save(@RequestBody @Validated DataLineage lineage) {
        lineageService.save(lineage);
        return lineage;
    }

    @Operation(summary = "更新数据血缘")
    @PutMapping("/{id}")
    public DataLineage update(@PathVariable("id") String id, @RequestBody @Validated DataLineage lineage) {
        lineage.setId(id);
        lineageService.updateById(lineage);
        return lineageService.getById(id);
    }

    @Operation(summary = "删除数据血缘")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        lineageService.removeById(id);
    }
}
