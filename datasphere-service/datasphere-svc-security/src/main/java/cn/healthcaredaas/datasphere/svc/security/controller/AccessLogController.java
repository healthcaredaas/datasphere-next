package cn.healthcaredaas.datasphere.svc.security.controller;

import cn.healthcaredaas.datasphere.svc.security.entity.AccessLog;
import cn.healthcaredaas.datasphere.svc.security.service.AccessLogService;
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
 * 访问审计日志控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/security/access-logs")
@RequiredArgsConstructor
@Tag(name = "访问审计日志管理", description = "访问审计日志管理相关接口")
public class AccessLogController {

    private final AccessLogService accessLogService;

    @Operation(summary = "分页查询访问审计日志列表")
    @GetMapping
    public IPage<AccessLog> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            AccessLog params) {
        return accessLogService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "记录访问日志")
    @PostMapping("/record")
    public void recordAccess(@RequestBody @Validated AccessLog log) {
        accessLogService.recordAccess(log);
    }

    @Operation(summary = "获取访问趋势")
    @GetMapping("/trend")
    public List<Map<String, Object>> getAccessTrend(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return accessLogService.getAccessTrend(startTime, endTime);
    }

    @Operation(summary = "获取访问统计")
    @GetMapping("/stats")
    public Map<String, Object> getAccessStats(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return accessLogService.getAccessStats(startTime, endTime);
    }

    @Operation(summary = "获取访问审计日志详情")
    @GetMapping("/{id}")
    public AccessLog getById(@PathVariable("id") String id) {
        return accessLogService.getById(id);
    }
}
