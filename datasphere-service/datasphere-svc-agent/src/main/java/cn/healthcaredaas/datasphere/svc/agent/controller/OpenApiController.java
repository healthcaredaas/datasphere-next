package cn.healthcaredaas.datasphere.svc.agent.controller;

import cn.healthcaredaas.datasphere.core.web.BaseController;
import cn.healthcaredaas.datasphere.svc.agent.entity.AgentMessage;
import cn.healthcaredaas.datasphere.svc.agent.entity.AgentSession;
import cn.healthcaredaas.datasphere.svc.agent.entity.ApiKey;
import cn.healthcaredaas.datasphere.svc.agent.engine.AgentEngine;
import cn.healthcaredaas.datasphere.svc.agent.service.AgentSessionService;
import cn.healthcaredaas.datasphere.svc.agent.service.ApiKeyService;
import cn.healthcaredaas.datasphere.svc.agent.service.ModelConfigService;
import cn.healthcaredaas.datasphere.svc.agent.tools.Tool;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolContext;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolRegistry;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolResult;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 开放API控制器
 * 提供给第三方系统调用的API接口
 *
 * @author chenpan
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/agent/open")
@RequiredArgsConstructor
@Tag(name = "开放API", description = "第三方系统调用的API接口")
public class OpenApiController extends BaseController {

    private final ApiKeyService apiKeyService;
    private final ToolRegistry toolRegistry;
    private final AgentEngine agentEngine;
    private final AgentSessionService sessionService;
    private final ModelConfigService modelConfigService;

    /**
     * 验证API Key
     */
    private boolean validateApiKey(String apiKey, String permission) {
        if (apiKey == null || !apiKey.startsWith("sk-")) {
            return false;
        }
        return apiKeyService.validateApiKey(apiKey, permission);
    }

    @Operation(summary = "SQL生成API")
    @PostMapping("/sql/generate")
    public RestResult<JSONObject> generateSql(
            @RequestHeader("X-API-Key") String apiKey,
            @RequestBody JSONObject request) {
        if (!validateApiKey(apiKey, "sql:generate")) {
            return error(401, "无效的API密钥");
        }

        apiKeyService.updateLastUsedTime(apiKey);

        Tool tool = toolRegistry.getTool("sql_generator");
        if (tool == null) {
            return error(500, "工具不可用");
        }

        ToolContext context = new ToolContext();
        ToolResult result = tool.execute(request, context);

        if (result.isSuccess()) {
            return success((JSONObject) result.getData());
        } else {
            return error(500, result.getErrorMessage());
        }
    }

    @Operation(summary = "SQL执行API")
    @PostMapping("/sql/execute")
    public RestResult<JSONObject> executeSql(
            @RequestHeader("X-API-Key") String apiKey,
            @RequestBody JSONObject request) {
        if (!validateApiKey(apiKey, "sql:execute")) {
            return error(401, "无效的API密钥");
        }

        apiKeyService.updateLastUsedTime(apiKey);

        Tool tool = toolRegistry.getTool("sql_executor");
        if (tool == null) {
            return error(500, "工具不可用");
        }

        ToolContext context = new ToolContext();
        ToolResult result = tool.execute(request, context);

        if (result.isSuccess()) {
            return success((JSONObject) result.getData());
        } else {
            return error(500, result.getErrorMessage());
        }
    }

    @Operation(summary = "数据管道配置生成API")
    @PostMapping("/pipeline/generate")
    public RestResult<JSONObject> generatePipeline(
            @RequestHeader("X-API-Key") String apiKey,
            @RequestBody JSONObject request) {
        if (!validateApiKey(apiKey, "pipeline:generate")) {
            return error(401, "无效的API密钥");
        }

        apiKeyService.updateLastUsedTime(apiKey);

        Tool tool = toolRegistry.getTool("pipeline_generator");
        if (tool == null) {
            return error(500, "工具不可用");
        }

        ToolContext context = new ToolContext();
        ToolResult result = tool.execute(request, context);

        if (result.isSuccess()) {
            return success((JSONObject) result.getData());
        } else {
            return error(500, result.getErrorMessage());
        }
    }

    @Operation(summary = "质量规则生成API")
    @PostMapping("/quality-rule/generate")
    public RestResult<JSONObject> generateQualityRule(
            @RequestHeader("X-API-Key") String apiKey,
            @RequestBody JSONObject request) {
        if (!validateApiKey(apiKey, "quality:generate")) {
            return error(401, "无效的API密钥");
        }

        apiKeyService.updateLastUsedTime(apiKey);

        Tool tool = toolRegistry.getTool("quality_rule_generator");
        if (tool == null) {
            return error(500, "工具不可用");
        }

        ToolContext context = new ToolContext();
        ToolResult result = tool.execute(request, context);

        if (result.isSuccess()) {
            return success((JSONObject) result.getData());
        } else {
            return error(500, result.getErrorMessage());
        }
    }

    @Operation(summary = "元数据查询API")
    @PostMapping("/metadata/query")
    public RestResult<JSONObject> queryMetadata(
            @RequestHeader("X-API-Key") String apiKey,
            @RequestBody JSONObject request) {
        if (!validateApiKey(apiKey, "metadata:query")) {
            return error(401, "无效的API密钥");
        }

        apiKeyService.updateLastUsedTime(apiKey);

        Tool tool = toolRegistry.getTool("metadata_query");
        if (tool == null) {
            return error(500, "工具不可用");
        }

        ToolContext context = new ToolContext();
        ToolResult result = tool.execute(request, context);

        if (result.isSuccess()) {
            return success((JSONObject) result.getData());
        } else {
            return error(500, result.getErrorMessage());
        }
    }

    @Operation(summary = "对话API")
    @PostMapping("/chat")
    public RestResult<JSONObject> chat(
            @RequestHeader("X-API-Key") String apiKey,
            @RequestBody JSONObject request) {
        if (!validateApiKey(apiKey, "chat")) {
            return error(401, "无效的API密钥");
        }

        apiKeyService.updateLastUsedTime(apiKey);

        try {
            String query = request.getString("query");
            String modelId = request.getString("modelId");
            String userId = request.getString("userId");
            String tenantId = request.getString("tenantId");

            if (query == null || query.isBlank()) {
                return error(400, "query参数不能为空");
            }

            // 获取API密钥关联的用户信息
            ApiKey keyInfo = apiKeyService.getByApiKey(apiKey);
            if (keyInfo == null) {
                return error(401, "API密钥不存在");
            }

            // 使用API密钥关联的用户ID和租户ID作为默认值
            if (userId == null || userId.isBlank()) {
                userId = keyInfo.getUserId();
            }
            if (tenantId == null || tenantId.isBlank()) {
                tenantId = keyInfo.getTenantId();
            }

            // 创建临时会话
            String sessionTitle = "API对话 - " + query.substring(0, Math.min(50, query.length()));
            AgentSession session = sessionService.createSession(sessionTitle, modelId, userId, tenantId);

            // 使用AgentEngine处理消息
            AgentMessage response = agentEngine.processMessage(session.getId(), query, userId, tenantId);

            // 构建返回结果
            JSONObject result = new JSONObject();
            result.put("sessionId", session.getId());
            result.put("messageId", response.getId());
            result.put("content", response.getContent());
            result.put("contentType", response.getContentType());

            // 如果有工具调用记录，也返回
            if (response.getToolCalls() != null && !response.getToolCalls().isEmpty()) {
                result.put("toolCalls", response.getToolCalls());
            }

            return success(result);

        } catch (Exception e) {
            log.error("对话API调用失败: {}", e.getMessage(), e);
            return error(500, "对话处理失败: " + e.getMessage());
        }
    }

    @Operation(summary = "多轮对话API")
    @PostMapping("/chat/{sessionId}")
    public RestResult<JSONObject> continueChat(
            @RequestHeader("X-API-Key") String apiKey,
            @PathVariable("sessionId") String sessionId,
            @RequestBody JSONObject request) {
        if (!validateApiKey(apiKey, "chat")) {
            return error(401, "无效的API密钥");
        }

        apiKeyService.updateLastUsedTime(apiKey);

        try {
            String query = request.getString("query");
            String userId = request.getString("userId");
            String tenantId = request.getString("tenantId");

            if (query == null || query.isBlank()) {
                return error(400, "query参数不能为空");
            }

            // 验证会话存在
            AgentSession session = sessionService.getById(sessionId);
            if (session == null) {
                return error(404, "会话不存在");
            }

            // 获取API密钥关联的用户信息
            ApiKey keyInfo = apiKeyService.getByApiKey(apiKey);
            if (userId == null || userId.isBlank()) {
                userId = keyInfo.getUserId();
            }
            if (tenantId == null || tenantId.isBlank()) {
                tenantId = keyInfo.getTenantId();
            }

            // 使用AgentEngine处理消息
            AgentMessage response = agentEngine.processMessage(sessionId, query, userId, tenantId);

            // 构建返回结果
            JSONObject result = new JSONObject();
            result.put("sessionId", sessionId);
            result.put("messageId", response.getId());
            result.put("content", response.getContent());
            result.put("contentType", response.getContentType());

            if (response.getToolCalls() != null && !response.getToolCalls().isEmpty()) {
                result.put("toolCalls", response.getToolCalls());
            }

            return success(result);

        } catch (Exception e) {
            log.error("多轮对话API调用失败: {}", e.getMessage(), e);
            return error(500, "对话处理失败: " + e.getMessage());
        }
    }
}