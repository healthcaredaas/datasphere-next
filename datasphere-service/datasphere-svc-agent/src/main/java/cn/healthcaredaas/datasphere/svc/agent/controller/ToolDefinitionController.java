package cn.healthcaredaas.datasphere.svc.agent.controller;

import cn.healthcaredaas.datasphere.core.web.BaseController;
import cn.healthcaredaas.datasphere.svc.agent.entity.ToolDefinition;
import cn.healthcaredaas.datasphere.svc.agent.service.ToolDefinitionService;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolRegistry;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工具定义控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/agent/tools")
@RequiredArgsConstructor
@Tag(name = "工具定义管理", description = "工具定义管理相关接口")
public class ToolDefinitionController extends BaseController {

    private final ToolDefinitionService toolDefinitionService;
    private final ToolRegistry toolRegistry;

    @Operation(summary = "分页查询工具定义")
    @GetMapping
    public IPage<ToolDefinition> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            ToolDefinition params) {
        return toolDefinitionService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取启用的工具列表")
    @GetMapping("/enabled")
    public List<ToolDefinition> listEnabled() {
        return toolDefinitionService.listEnabled();
    }

    @Operation(summary = "获取工具详情")
    @GetMapping("/{id}")
    public ToolDefinition getById(@PathVariable("id") String id) {
        return toolDefinitionService.getById(id);
    }

    @Operation(summary = "获取工具Schema")
    @GetMapping("/{name}/schema")
    public JSONObject getToolSchema(@PathVariable("name") String name) {
        return toolRegistry.getToolSchema(name);
    }

    @Operation(summary = "获取所有工具Schema")
    @GetMapping("/schemas")
    public List<JSONObject> getAllToolSchemas() {
        return toolRegistry.getAllToolSchemas();
    }

    @Operation(summary = "获取工具名称列表")
    @GetMapping("/names")
    public List<String> getToolNames() {
        return toolRegistry.getToolNames();
    }

    @Operation(summary = "新增工具定义")
    @PostMapping
    public ToolDefinition save(@RequestBody ToolDefinition toolDefinition) {
        toolDefinitionService.save(toolDefinition);
        return toolDefinition;
    }

    @Operation(summary = "更新工具定义")
    @PutMapping("/{id}")
    public ToolDefinition update(@PathVariable("id") String id, @RequestBody ToolDefinition toolDefinition) {
        toolDefinition.setId(id);
        toolDefinitionService.updateById(toolDefinition);
        return toolDefinitionService.getById(id);
    }

    @Operation(summary = "删除工具定义")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        toolDefinitionService.removeById(id);
    }
}