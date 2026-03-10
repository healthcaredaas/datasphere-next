package cn.healthcaredaas.datasphere.svc.agent.controller;

import cn.healthcaredaas.datasphere.core.web.BaseController;
import cn.healthcaredaas.datasphere.svc.agent.entity.AuditLog;
import cn.healthcaredaas.datasphere.svc.agent.service.AuditLogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 审计日志控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/agent/audit-logs")
@RequiredArgsConstructor
@Tag(name = "审计日志管理", description = "审计日志管理相关接口")
public class AuditLogController extends BaseController {

    private final AuditLogService auditLogService;

    @Operation(summary = "分页查询审计日志")
    @GetMapping
    public IPage<AuditLog> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") long size,
            AuditLog params) {
        return auditLogService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取审计日志详情")
    @GetMapping("/{id}")
    public AuditLog getById(@PathVariable("id") String id) {
        return auditLogService.getById(id);
    }
}