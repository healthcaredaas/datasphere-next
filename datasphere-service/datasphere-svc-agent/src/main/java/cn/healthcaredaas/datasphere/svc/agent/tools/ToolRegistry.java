package cn.healthcaredaas.datasphere.svc.agent.tools;

import cn.healthcaredaas.datasphere.svc.agent.entity.ToolDefinition;
import cn.healthcaredaas.datasphere.svc.agent.service.ToolDefinitionService;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工具注册中心
 *
 * @author chenpan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ToolRegistry {

    private final List<Tool> tools;
    private final ToolDefinitionService toolDefinitionService;
    private final Map<String, Tool> toolMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (Tool tool : tools) {
            toolMap.put(tool.getName(), tool);
            log.info("注册工具: {}", tool.getName());
        }
    }

    /**
     * 获取工具
     */
    public Tool getTool(String name) {
        return toolMap.get(name);
    }

    /**
     * 检查工具是否存在
     */
    public boolean hasTool(String name) {
        return toolMap.containsKey(name);
    }

    /**
     * 获取所有工具名称
     */
    public List<String> getToolNames() {
        return List.copyOf(toolMap.keySet());
    }

    /**
     * 获取工具定义列表
     */
    public List<ToolDefinition> getToolDefinitions() {
        return toolDefinitionService.listEnabled();
    }

    /**
     * 获取工具的JSON Schema描述(用于LLM调用)
     */
    public JSONObject getToolSchema(String name) {
        Tool tool = getTool(name);
        if (tool == null) {
            return null;
        }

        JSONObject schema = new JSONObject();
        schema.put("name", tool.getName());
        schema.put("description", tool.getDescription());
        schema.put("parameters", tool.getInputSchema());
        return schema;
    }

    /**
     * 获取所有工具的JSON Schema描述
     */
    public List<JSONObject> getAllToolSchemas() {
        return toolMap.keySet().stream()
                .map(this::getToolSchema)
                .toList();
    }
}