package cn.healthcaredaas.datasphere.svc.agent.controller;

import cn.healthcaredaas.datasphere.core.web.BaseController;
import cn.healthcaredaas.datasphere.svc.agent.entity.ModelConfig;
import cn.healthcaredaas.datasphere.svc.agent.service.ModelConfigService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模型配置控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/agent/models")
@RequiredArgsConstructor
@Tag(name = "模型配置管理", description = "模型配置管理相关接口")
public class ModelConfigController extends BaseController {

    private final ModelConfigService modelConfigService;

    @Operation(summary = "分页查询模型配置")
    @GetMapping
    public IPage<ModelConfig> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            ModelConfig params) {
        return modelConfigService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取启用的模型列表")
    @GetMapping("/enabled")
    public List<ModelConfig> listEnabled() {
        return modelConfigService.listEnabled();
    }

    @Operation(summary = "获取模型详情")
    @GetMapping("/{id}")
    public ModelConfig getById(@PathVariable("id") String id) {
        return modelConfigService.getById(id);
    }

    @Operation(summary = "新增模型配置")
    @PostMapping
    public ModelConfig save(@RequestBody ModelConfig modelConfig) {
        modelConfigService.save(modelConfig);
        return modelConfig;
    }

    @Operation(summary = "更新模型配置")
    @PutMapping("/{id}")
    public ModelConfig update(@PathVariable("id") String id, @RequestBody ModelConfig modelConfig) {
        modelConfig.setId(id);
        modelConfigService.updateById(modelConfig);
        return modelConfigService.getById(id);
    }

    @Operation(summary = "删除模型配置")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        modelConfigService.removeById(id);
    }

    @Operation(summary = "测试模型连接")
    @PostMapping("/{id}/test")
    public Map<String, Object> testConnection(@PathVariable("id") String id) {
        boolean success = modelConfigService.testConnection(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "连接成功" : "连接失败");
        return result;
    }

    @Operation(summary = "启用/禁用模型")
    @PutMapping("/{id}/status")
    public ModelConfig updateStatus(@PathVariable("id") String id, @RequestParam Integer status) {
        ModelConfig modelConfig = modelConfigService.getById(id);
        if (modelConfig != null) {
            modelConfig.setStatus(status);
            modelConfigService.updateById(modelConfig);
        }
        return modelConfigService.getById(id);
    }
}