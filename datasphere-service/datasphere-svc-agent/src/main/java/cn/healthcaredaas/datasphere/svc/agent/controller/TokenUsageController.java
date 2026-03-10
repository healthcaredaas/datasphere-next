package cn.healthcaredaas.datasphere.svc.agent.controller;

import cn.healthcaredaas.datasphere.core.web.BaseController;
import cn.healthcaredaas.datasphere.svc.agent.entity.TokenUsage;
import cn.healthcaredaas.datasphere.svc.agent.service.TokenUsageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * Token用量控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/agent/usage")
@RequiredArgsConstructor
@Tag(name = "Token用量统计", description = "Token用量统计相关接口")
public class TokenUsageController extends BaseController {

    private final TokenUsageService tokenUsageService;

    @Operation(summary = "分页查询用量记录")
    @GetMapping
    public IPage<TokenUsage> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            TokenUsage params) {
        return tokenUsageService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取用户日用量统计")
    @GetMapping("/user/{userId}/daily")
    public Map<String, Object> getUserDailyUsage(
            @PathVariable("userId") String userId,
            @Parameter(description = "日期(yyyy-MM-dd)") @RequestParam(required = false) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return tokenUsageService.getUserDailyUsage(userId, date);
    }

    @Operation(summary = "获取租户月用量统计")
    @GetMapping("/tenant/{tenantId}/monthly")
    public Map<String, Object> getTenantMonthlyUsage(
            @PathVariable("tenantId") String tenantId,
            @Parameter(description = "年份") @RequestParam(required = false) Integer year,
            @Parameter(description = "月份") @RequestParam(required = false) Integer month) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        if (month == null) {
            month = LocalDate.now().getMonthValue();
        }
        return tokenUsageService.getTenantMonthlyUsage(tenantId, year, month);
    }
}