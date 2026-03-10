package cn.healthcaredaas.datasphere.svc.asset.controller;

import cn.healthcaredaas.datasphere.svc.asset.entity.AssetUsage;
import cn.healthcaredaas.datasphere.svc.asset.service.AssetUsageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 资产使用记录控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/asset/usage")
@RequiredArgsConstructor
@Tag(name = "资产使用记录管理", description = "资产使用记录管理相关接口")
public class AssetUsageController {

    private final AssetUsageService usageService;

    @Operation(summary = "分页查询资产使用记录列表")
    @GetMapping
    public IPage<AssetUsage> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            AssetUsage params) {
        return usageService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "记录资产访问")
    @PostMapping("/record")
    public void recordAccess(@RequestBody @Validated AssetUsage usage) {
        usageService.recordAccess(usage);
    }

    @Operation(summary = "获取热门资产")
    @GetMapping("/hot-assets")
    public List<Map<String, Object>> getHotAssets(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "10") int limit) {
        return usageService.getHotAssets(startTime, endTime, limit);
    }

    @Operation(summary = "获取资产访问统计")
    @GetMapping("/stats/{assetId}")
    public Map<String, Object> getAssetStats(
            @PathVariable("assetId") String assetId,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return usageService.getAssetAccessStats(assetId, startTime, endTime);
    }

    @Operation(summary = "获取资产使用记录详情")
    @GetMapping("/{id}")
    public AssetUsage getById(@PathVariable("id") String id) {
        return usageService.getById(id);
    }
}
