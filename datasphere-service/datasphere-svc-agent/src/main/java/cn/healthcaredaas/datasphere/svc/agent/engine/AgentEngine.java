package cn.healthcaredaas.datasphere.svc.agent.engine;

import cn.healthcaredaas.datasphere.svc.agent.entity.AgentMessage;
import cn.healthcaredaas.datasphere.svc.agent.entity.AgentSession;
import cn.healthcaredaas.datasphere.svc.agent.entity.ModelConfig;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapter;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapterFactory;
import cn.healthcaredaas.datasphere.svc.agent.service.AgentMessageService;
import cn.healthcaredaas.datasphere.svc.agent.service.AgentSessionService;
import cn.healthcaredaas.datasphere.svc.agent.service.AuditLogService;
import cn.healthcaredaas.datasphere.svc.agent.service.ModelConfigService;
import cn.healthcaredaas.datasphere.svc.agent.service.TokenUsageService;
import cn.healthcaredaas.datasphere.svc.agent.tools.Tool;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolContext;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolRegistry;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolResult;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Agent引擎
 * 核心对话处理引擎
 *
 * @author chenpan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AgentEngine {

    private final LlmAdapterFactory llmAdapterFactory;
    private final ToolRegistry toolRegistry;
    private final AgentSessionService sessionService;
    private final AgentMessageService messageService;
    private final ModelConfigService modelConfigService;
    private final AuditLogService auditLogService;
    private final TokenUsageService tokenUsageService;

    @Value("${agent.max-history-messages:20}")
    private int maxHistoryMessages;

    @Value("${agent.max-tool-iterations:5}")
    private int maxToolIterations;

    /**
     * 增强的系统提示词
     */
    private static final String SYSTEM_PROMPT = """
            你是DataSphere数据中台的AI助手，专注于帮助用户完成数据治理任务。

            ## 核心能力
            1. **数据查询**：将自然语言转换为SQL查询，执行查询并展示结果
            2. **数据集成**：配置数据同步管道，生成SeaTunnel配置
            3. **数据质量**：生成数据质量检测规则，分析质量问题
            4. **元数据查询**：查询数据源、表、字段信息
            5. **标准映射**：生成字段到标准(FHIR/国标)的映射配置
            6. **问题诊断**：分析数据问题、任务失败原因，提供解决方案

            ## 工具调用格式
            当需要调用工具时，请使用以下JSON格式：
            ```json
            {
              "tool_calls": [
                {
                  "name": "工具名称",
                  "parameters": {
                    "参数名": "参数值"
                  }
                }
              ]
            }
            ```

            ## 可用工具
            %s

            ## 交互规范
            1. 理解用户意图后，选择合适的工具执行
            2. 如果需要多个工具，按依赖顺序依次调用
            3. 执行结果以清晰格式展示给用户
            4. 提供专业的数据治理建议

            ## 安全约束
            - 只执行SELECT查询，不修改数据
            - 对敏感字段进行脱敏处理
            - 遵守数据权限和租户隔离规则
            """;

    /**
     * 工具调用JSON模式
     */
    private static final Pattern TOOL_CALL_PATTERN = Pattern.compile(
            "\\{\\s*[\"']?tool_calls[\"']?\\s*:\\s*\\[.*?\\]\\s*\\}",
            Pattern.DOTALL
    );

    /**
     * 处理对话消息
     */
    public AgentMessage processMessage(String sessionId, String content, String userId, String tenantId) {
        long startTime = System.currentTimeMillis();
        String operationType = "CHAT";

        // 获取会话
        AgentSession session = sessionService.getById(sessionId);
        if (session == null) {
            throw new RuntimeException("会话不存在");
        }

        // 获取模型配置
        ModelConfig modelConfig = modelConfigService.getById(session.getModelId());
        if (modelConfig == null) {
            modelConfig = modelConfigService.getDefaultModel();
        }

        if (modelConfig == null) {
            return messageService.addAssistantMessage(sessionId,
                    "系统未配置可用的LLM模型，请联系管理员配置模型。", "ERROR", null, null);
        }

        // 获取LLM适配器
        LlmAdapter adapter = llmAdapterFactory.getAdapter(modelConfig);

        // 获取工具描述
        String toolsDescription = buildToolsDescription();

        // 构建系统提示词
        String systemPrompt = String.format(SYSTEM_PROMPT, toolsDescription);

        // 添加用户消息
        AgentMessage userMessage = messageService.addUserMessage(sessionId, content);

        // 获取历史消息作为上下文
        List<AgentMessage> historyMessages = messageService.getRecentMessages(sessionId, maxHistoryMessages);

        try {
            String finalResponse = content;
            List<JSONObject> allToolCalls = new ArrayList<>();
            int iterations = 0;

            // 迭代处理，支持多轮工具调用
            while (iterations < maxToolIterations) {
                // 调用LLM
                String response = adapter.chat(systemPrompt, buildUserPrompt(historyMessages, finalResponse), modelConfig);

                // 解析响应，检查是否需要调用工具
                ParsedResponse parsed = parseResponse(response);

                if (parsed.hasToolCalls()) {
                    // 执行工具调用
                    List<ToolExecutionResult> results = executeTools(parsed.getToolCalls(), sessionId, userId, tenantId);

                    // 记录工具调用
                    for (ToolExecutionResult result : results) {
                        allToolCalls.add(result.toJson());
                    }

                    // 构建工具结果作为下一次输入
                    finalResponse = buildToolResultsMessage(results);
                    operationType = "TOOL_CALL";
                    iterations++;

                    // 如果工具执行有错误，停止迭代
                    boolean hasError = results.stream().anyMatch(r -> !r.getResult().isSuccess());
                    if (hasError) {
                        break;
                    }
                } else {
                    // 没有工具调用，返回最终响应
                    String assistantContent = parsed.getContent();
                    String toolCallsJson = allToolCalls.isEmpty() ? null : JSON.toJSONString(allToolCalls);

                    // 添加助手消息
                    AgentMessage assistantMessage = messageService.addAssistantMessage(
                            sessionId, assistantContent, "TEXT", toolCallsJson, null);

                    // 更新会话活跃时间和消息数
                    sessionService.updateActiveTime(sessionId);
                    sessionService.incrementMessageCount(sessionId);

                    // 记录审计日志
                    recordAuditLog(sessionId, assistantMessage.getId(), userId, tenantId,
                            operationType, content, assistantContent, allToolCalls,
                            System.currentTimeMillis() - startTime, "SUCCESS", null);

                    log.info("处理消息完成 - sessionId: {}, iterations: {}, 耗时: {}ms",
                            sessionId, iterations, System.currentTimeMillis() - startTime);

                    return assistantMessage;
                }
            }

            // 达到最大迭代次数
            String warningMsg = "工具调用次数达到上限，请简化需求或分步执行。";
            AgentMessage message = messageService.addAssistantMessage(sessionId, warningMsg, "TEXT",
                    JSON.toJSONString(allToolCalls), null);

            recordAuditLog(sessionId, message.getId(), userId, tenantId, operationType,
                    content, warningMsg, allToolCalls,
                    System.currentTimeMillis() - startTime, "WARNING", "达到最大工具调用次数");

            return message;

        } catch (Exception e) {
            log.error("处理消息失败: {}", e.getMessage(), e);

            AgentMessage errorMessage = messageService.addAssistantMessage(
                    sessionId, "处理请求时发生错误: " + e.getMessage(), "ERROR", null, null);

            recordAuditLog(sessionId, errorMessage.getId(), userId, tenantId, operationType,
                    content, null, null, System.currentTimeMillis() - startTime, "FAIL", e.getMessage());

            return errorMessage;
        }
    }

    /**
     * 流式处理消息
     */
    public void processMessageStream(String sessionId, String content, String userId, String tenantId,
                                      LlmAdapter.StreamCallback callback) {
        long startTime = System.currentTimeMillis();

        AgentSession session = sessionService.getById(sessionId);
        if (session == null) {
            callback.onError(new RuntimeException("会话不存在"));
            return;
        }

        ModelConfig modelConfig = modelConfigService.getById(session.getModelId());
        if (modelConfig == null) {
            modelConfig = modelConfigService.getDefaultModel();
        }

        if (modelConfig == null) {
            callback.onError(new RuntimeException("系统未配置可用的LLM模型"));
            return;
        }

        LlmAdapter adapter = llmAdapterFactory.getAdapter(modelConfig);
        String toolsDescription = buildToolsDescription();
        String systemPrompt = String.format(SYSTEM_PROMPT, toolsDescription);

        messageService.addUserMessage(sessionId, content);
        List<AgentMessage> historyMessages = messageService.getRecentMessages(sessionId, maxHistoryMessages);

        adapter.chatStream(systemPrompt, buildUserPrompt(historyMessages, content), modelConfig, new LlmAdapter.StreamCallback() {
            private StringBuilder fullResponse = new StringBuilder();

            @Override
            public void onToken(String token) {
                fullResponse.append(token);
                callback.onToken(token);
            }

            @Override
            public void onComplete(String fullResponseStr) {
                messageService.addAssistantMessage(sessionId, fullResponse.toString(), "TEXT", null, null);
                sessionService.updateActiveTime(sessionId);
                sessionService.incrementMessageCount(sessionId);

                // 记录审计日志
                recordAuditLog(sessionId, null, userId, tenantId, "CHAT_STREAM",
                        content, fullResponse.toString(), null,
                        System.currentTimeMillis() - startTime, "SUCCESS", null);

                callback.onComplete(fullResponseStr);
            }

            @Override
            public void onError(Throwable error) {
                recordAuditLog(sessionId, null, userId, tenantId, "CHAT_STREAM",
                        content, null, null, System.currentTimeMillis() - startTime, "FAIL", error.getMessage());
                callback.onError(error);
            }
        });
    }

    /**
     * 执行工具列表
     */
    private List<ToolExecutionResult> executeTools(List<ToolCall> toolCalls, String sessionId, String userId, String tenantId) {
        List<ToolExecutionResult> results = new ArrayList<>();

        for (ToolCall toolCall : toolCalls) {
            long toolStartTime = System.currentTimeMillis();

            Tool tool = toolRegistry.getTool(toolCall.getName());
            if (tool == null) {
                results.add(new ToolExecutionResult(toolCall.getName(), null,
                        ToolResult.error("工具不存在: " + toolCall.getName()),
                        System.currentTimeMillis() - toolStartTime));
                continue;
            }

            try {
                ToolContext context = ToolContext.of(sessionId, userId, tenantId);
                ToolResult result = tool.execute(toolCall.getParameters(), context);

                results.add(new ToolExecutionResult(toolCall.getName(), toolCall.getParameters(),
                        result, System.currentTimeMillis() - toolStartTime));

                log.info("工具执行完成 - name: {}, success: {}, time: {}ms",
                        toolCall.getName(), result.isSuccess(), System.currentTimeMillis() - toolStartTime);

            } catch (Exception e) {
                log.error("工具执行异常 - name: {}, error: {}", toolCall.getName(), e.getMessage());
                results.add(new ToolExecutionResult(toolCall.getName(), toolCall.getParameters(),
                        ToolResult.error("工具执行异常: " + e.getMessage()),
                        System.currentTimeMillis() - toolStartTime));
            }
        }

        return results;
    }

    /**
     * 构建工具描述
     */
    private String buildToolsDescription() {
        StringBuilder sb = new StringBuilder();
        for (JSONObject schema : toolRegistry.getAllToolSchemas()) {
            String name = schema.getString("name");
            String description = schema.getString("description");
            JSONObject parameters = schema.getJSONObject("parameters");

            sb.append("\n### ").append(name).append("\n");
            sb.append(description).append("\n");

            if (parameters != null && parameters.containsKey("properties")) {
                sb.append("**参数**:\n");
                JSONObject props = parameters.getJSONObject("properties");
                for (String key : props.keySet()) {
                    JSONObject prop = props.getJSONObject(key);
                    sb.append("- `").append(key).append("`");
                    if (prop != null) {
                        sb.append(": ").append(prop.getString("description", ""));
                    }
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 构建用户提示词
     */
    private String buildUserPrompt(List<AgentMessage> historyMessages, String currentMessage) {
        StringBuilder sb = new StringBuilder();

        for (AgentMessage msg : historyMessages) {
            if ("USER".equals(msg.getRole())) {
                sb.append("用户: ").append(msg.getContent()).append("\n\n");
            } else if ("ASSISTANT".equals(msg.getRole())) {
                sb.append("助手: ").append(msg.getContent()).append("\n\n");
            }
        }

        // 添加当前消息
        if (currentMessage != null && !currentMessage.equals(historyMessages.isEmpty() ? "" :
                historyMessages.get(historyMessages.size() - 1).getContent())) {
            sb.append("用户: ").append(currentMessage).append("\n\n");
        }

        return sb.toString();
    }

    /**
     * 解析LLM响应
     */
    private ParsedResponse parseResponse(String response) {
        ParsedResponse result = new ParsedResponse();

        if (response == null || response.isEmpty()) {
            result.setContent("");
            return result;
        }

        // 尝试提取工具调用JSON
        Matcher matcher = TOOL_CALL_PATTERN.matcher(response);
        if (matcher.find()) {
            try {
                String jsonStr = matcher.group();
                JSONObject json = JSONObject.parseObject(jsonStr);

                if (json.containsKey("tool_calls")) {
                    JSONArray toolCallsArray = json.getJSONArray("tool_calls");
                    List<ToolCall> toolCalls = new ArrayList<>();

                    for (int i = 0; i < toolCallsArray.size(); i++) {
                        JSONObject tc = toolCallsArray.getJSONObject(i);
                        toolCalls.add(new ToolCall(
                                tc.getString("name"),
                                tc.getJSONObject("parameters")
                        ));
                    }

                    result.setToolCalls(toolCalls);

                    // 提取非工具调用的内容
                    String remainingContent = response.replace(jsonStr, "").trim();
                    result.setContent(remainingContent.isEmpty() ? "" : remainingContent);

                    return result;
                }
            } catch (Exception e) {
                log.warn("解析工具调用失败: {}", e.getMessage());
            }
        }

        // 没有工具调用，直接返回内容
        result.setContent(response);
        return result;
    }

    /**
     * 构建工具结果消息
     */
    private String buildToolResultsMessage(List<ToolExecutionResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("工具执行结果:\n\n");

        for (ToolExecutionResult result : results) {
            sb.append("### ").append(result.getToolName()).append("\n");
            sb.append("执行时间: ").append(result.getExecutionTime()).append("ms\n");
            sb.append("状态: ").append(result.getResult().isSuccess() ? "成功" : "失败").append("\n");

            if (result.getResult().isSuccess()) {
                Object data = result.getResult().getData();
                if (data != null) {
                    sb.append("结果:\n```json\n");
                    sb.append(JSON.toJSONString(data, true));
                    sb.append("\n```\n");
                }
            } else {
                sb.append("错误: ").append(result.getResult().getErrorMessage()).append("\n");
            }
            sb.append("\n");
        }

        sb.append("请根据以上工具执行结果，给出最终回复。");
        return sb.toString();
    }

    /**
     * 记录审计日志
     */
    private void recordAuditLog(String sessionId, String messageId, String userId, String tenantId,
                                 String operationType, String requestContent, String responseContent,
                                 List<JSONObject> toolsUsed, long executionTime, String status, String errorMsg) {
        try {
            auditLogService.log(sessionId, messageId, userId, tenantId, operationType,
                    requestContent, responseContent, toolsUsed, executionTime, status, errorMsg);
        } catch (Exception e) {
            log.warn("记录审计日志失败: {}", e.getMessage());
        }
    }

    // ========== 内部类 ==========

    /**
     * 解析后的响应
     */
    private static class ParsedResponse {
        private String content;
        private List<ToolCall> toolCalls;

        public boolean hasToolCalls() {
            return toolCalls != null && !toolCalls.isEmpty();
        }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public List<ToolCall> getToolCalls() { return toolCalls; }
        public void setToolCalls(List<ToolCall> toolCalls) { this.toolCalls = toolCalls; }
    }

    /**
     * 工具调用
     */
    private static class ToolCall {
        private final String name;
        private final JSONObject parameters;

        public ToolCall(String name, JSONObject parameters) {
            this.name = name;
            this.parameters = parameters;
        }

        public String getName() { return name; }
        public JSONObject getParameters() { return parameters; }
    }

    /**
     * 工具执行结果
     */
    private static class ToolExecutionResult {
        private final String toolName;
        private final JSONObject parameters;
        private final ToolResult result;
        private final long executionTime;

        public ToolExecutionResult(String toolName, JSONObject parameters, ToolResult result, long executionTime) {
            this.toolName = toolName;
            this.parameters = parameters;
            this.result = result;
            this.executionTime = executionTime;
        }

        public String getToolName() { return toolName; }
        public JSONObject getParameters() { return parameters; }
        public ToolResult getResult() { return result; }
        public long getExecutionTime() { return executionTime; }

        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            json.put("name", toolName);
            json.put("parameters", parameters);
            json.put("success", result.isSuccess());
            json.put("executionTime", executionTime);
            if (!result.isSuccess()) {
                json.put("error", result.getErrorMessage());
            }
            return json;
        }
    }
}