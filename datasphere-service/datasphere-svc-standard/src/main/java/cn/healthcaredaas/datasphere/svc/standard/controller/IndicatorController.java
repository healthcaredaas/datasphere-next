package cn.healthcaredaas.datasphere.svc.standard.controller;

import cn.healthcaredaas.datasphere.svc.standard.entity.Indicator;
import cn.healthcaredaas.datasphere.svc.standard.service.IndicatorService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 指标控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/standard/indicators")
@RequiredArgsConstructor
@Tag(name = "指标管理", description = "指标管理相关接口")
public class IndicatorController {

    private final IndicatorService indicatorService;

    @Operation(summary = "分页查询指标列表")
    @GetMapping
    public IPage<Indicator> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            Indicator params) {
        return indicatorService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取指标详情")
    @GetMapping("/{id}")
    public Indicator getById(@PathVariable("id") String id) {
        return indicatorService.getById(id);
    }

    @Operation(summary = "新增指标")
    @PostMapping
    public Indicator save(@RequestBody @Validated Indicator indicator) {
        indicatorService.save(indicator);
        return indicator;
    }

    @Operation(summary = "更新指标")
    @PutMapping("/{id}")
    public Indicator update(@PathVariable("id") String id, @RequestBody @Validated Indicator indicator) {
        indicator.setId(id);
        indicatorService.updateById(indicator);
        return indicatorService.getById(id);
    }

    @Operation(summary = "删除指标")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        indicatorService.removeById(id);
    }
}
