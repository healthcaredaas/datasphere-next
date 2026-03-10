package cn.healthcaredaas.datasphere.svc.agent.controller;

import cn.healthcaredaas.datasphere.core.web.BaseController;
import cn.healthcaredaas.datasphere.svc.agent.entity.ApiKey;
import cn.healthcaredaas.datasphere.svc.agent.service.ApiKeyService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * API密钥控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/agent/api-keys")
@RequiredArgsConstructor
@Tag(name = "API密钥管理", description = "API密钥管理相关接口")
public class ApiKeyController extends BaseController {

    private final ApiKeyService apiKeyService;

    @Operation(summary = "分页查询API密钥")
    @GetMapping
    public IPage<ApiKey> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            ApiKey params) {
        return apiKeyService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取API密钥详情")
    @GetMapping("/{id}")
    public ApiKey getById(@PathVariable("id") String id) {
        return apiKeyService.getById(id);
    }

    @Operation(summary = "生成新的API密钥")
    @PostMapping
    public ApiKey generate(@RequestBody Map<String, Object> request) {
        String keyName = (String) request.get("keyName");
        String userId = (String) request.getOrDefault("userId", "default");
        String tenantId = (String) request.getOrDefault("tenantId", "default");
        List<String> permissions = (List<String>) request.get("permissions");
        Integer rateLimit = request.get("rateLimit") != null ? (Integer) request.get("rateLimit") : 100;

        return apiKeyService.generateApiKey(keyName, userId, tenantId, permissions, rateLimit);
    }

    @Operation(summary = "吊销API密钥")
    @DeleteMapping("/{id}")
    public void revoke(@PathVariable("id") String id) {
        ApiKey apiKey = apiKeyService.getById(id);
        if (apiKey != null) {
            apiKeyService.revokeApiKey(apiKey.getApiKey());
        }
    }

    @Operation(summary = "启用/禁用API密钥")
    @PutMapping("/{id}/status")
    public ApiKey updateStatus(@PathVariable("id") String id, @RequestParam Integer status) {
        ApiKey apiKey = apiKeyService.getById(id);
        if (apiKey != null) {
            apiKey.setStatus(status);
            apiKeyService.updateById(apiKey);
        }
        return apiKeyService.getById(id);
    }
}