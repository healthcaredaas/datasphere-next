package cn.healthcaredaas.datasphere.svc.agent.tools.executor;

import cn.healthcaredaas.datasphere.svc.agent.entity.ModelConfig;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapter;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapterFactory;
import cn.healthcaredaas.datasphere.svc.agent.rpc.IntegrationRpcService;
import cn.healthcaredaas.datasphere.svc.agent.rpc.QualityRpcService;
import cn.healthcaredaas.datasphere.svc.agent.service.ModelConfigService;
import cn.healthcaredaas.datasphere.svc.agent.tools.Tool;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolContext;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolResult;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 诊断分析工具
 * 分析系统问题、任务失败原因，并提供解决方案
 *
 * @author chenpan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DiagnosticTool implements Tool {

    private final IntegrationRpcService integrationRpcService;
    private final QualityRpcService qualityRpcService;
    private final LlmAdapterFactory llmAdapterFactory;
    private final ModelConfigService modelConfigService;

    /**
     * 诊断系统提示词
     */
    private static final String DIAGNOSTIC_SYSTEM_PROMPT = """
            你是一位专业的数据平台运维专家，负责分析问题原因并提供解决方案。

            ## 分析框架
            1. 问题定位：识别问题的根本原因
            2. 影响评估：分析问题的影响范围
            3. 解决方案：提供具体的修复步骤
            4. 预防措施：建议如何避免类似问题

            ## 输出格式
            请以JSON格式输出分析结果：
            ```json
            {
              "rootCause": "根本原因",
              "rootCauseDetail": "详细原因说明",
              "impactLevel": "HIGH/MEDIUM/LOW",
              "affectedComponents": ["受影响组件"],
              "timeline": [
                {"time": "时间", "event": "事件描述"}
              ],
              "solutions": [
                {
                  "priority": 1,
                  "description": "解决方案描述",
                  "steps": ["步骤1", "步骤2"],
                  "autoFix": true/false,
                  "autoFixAction": "自动修复动作标识"
                }
              ],
              "prevention": ["预防措施1", "预防措施2"]
            }
            ```
            """;

    @Override
    public String getName() {
        return "diagnostic_analyzer";
    }

    @Override
    public String getDescription() {
        return "分析系统问题、任务失败原因，提供详细的诊断报告和解决方案。支持数据同步、数据质量、连接问题等场景。";
    }

    @Override
    public JSONObject getInputSchema() {
        JSONObject schema = new JSONObject();
        schema.put("type", "object");

        JSONObject properties = new JSONObject();
        properties.put("analysis_type", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "分析类型")
                .fluentPut("enum", List.of("JOB_FAILURE", "DATA_QUALITY", "SYNC_ERROR", "CONNECTION", "PERFORMANCE")));
        properties.put("job_id", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "任务ID(JOB_FAILURE、SYNC_ERROR时需要)"));
        properties.put("rule_id", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "规则ID(DATA_QUALITY时需要)"));
        properties.put("datasource_id", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "数据源ID(CONNECTION、PERFORMANCE时需要)"));
        properties.put("error_message", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "错误信息"));
        properties.put("log_snippet", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "日志片段"));

        schema.put("properties", properties);
        schema.put("required", List.of("analysis_type"));
        return schema;
    }

    @Override
    public ToolResult execute(JSONObject params, ToolContext context) {
        long startTime = System.currentTimeMillis();

        try {
            String analysisType = params.getString("analysis_type");
            String jobId = params.getString("job_id");
            String ruleId = params.getString("rule_id");
            String datasourceId = params.getString("datasource_id");
            String errorMessage = params.getString("error_message");
            String logSnippet = params.getString("log_snippet");

            log.info("执行诊断分析 - analysisType: {}, jobId: {}", analysisType, jobId);

            // 1. 收集诊断信息
            JSONObject diagnosticData = collectDiagnosticData(analysisType, jobId, ruleId, datasourceId);

            // 2. 获取模型配置
            ModelConfig modelConfig = modelConfigService.getDefaultModel();

            JSONObject analysisResult;

            if (modelConfig != null) {
                // 使用LLM进行深度分析
                analysisResult = analyzeWithLLM(modelConfig, analysisType, diagnosticData, errorMessage, logSnippet);
            } else {
                // 使用规则分析
                analysisResult = analyzeWithRules(analysisType, diagnosticData, errorMessage);
            }

            // 3. 构建结果
            JSONObject result = new JSONObject();
            result.put("analysisType", analysisType);
            result.put("analysisResult", analysisResult);
            result.put("executionTime", System.currentTimeMillis() - startTime);

            log.info("诊断分析完成 - 耗时 {}ms", result.getLong("executionTime"));

            return ToolResult.success(result);

        } catch (Exception e) {
            log.error("诊断分析失败: {}", e.getMessage(), e);
            return ToolResult.error("诊断分析失败: " + e.getMessage());
        }
    }

    /**
     * 收集诊断数据
     */
    private JSONObject collectDiagnosticData(String analysisType, String jobId, String ruleId, String datasourceId) {
        JSONObject data = new JSONObject();

        try {
            switch (analysisType) {
                case "JOB_FAILURE", "SYNC_ERROR" -> {
                    if (jobId != null) {
                        // 获取执行日志
                        List<JSONObject> logs = integrationRpcService.getExecutionLogs(jobId);
                        data.put("logs", logs);

                        // 获取管道信息
                        if (!logs.isEmpty()) {
                            String pipelineId = logs.get(0).getString("pipelineId");
                            if (pipelineId != null) {
                                JSONObject pipeline = integrationRpcService.getPipeline(pipelineId);
                                data.put("pipeline", pipeline);
                            }
                        }
                    }
                }
                case "DATA_QUALITY" -> {
                    if (ruleId != null) {
                        JSONObject rule = qualityRpcService.getRule(ruleId);
                        data.put("rule", rule);
                    }
                }
                case "CONNECTION", "PERFORMANCE" -> {
                    if (datasourceId != null) {
                        // 数据源信息会在调用时获取
                        data.put("datasourceId", datasourceId);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("收集诊断数据失败: {}", e.getMessage());
            data.put("collectError", e.getMessage());
        }

        return data;
    }

    /**
     * 使用LLM分析
     */
    private JSONObject analyzeWithLLM(ModelConfig modelConfig, String analysisType,
                                        JSONObject diagnosticData, String errorMessage, String logSnippet) {
        try {
            LlmAdapter adapter = llmAdapterFactory.getAdapter(modelConfig);

            // 构建用户提示词
            StringBuilder userPrompt = new StringBuilder();
            userPrompt.append("请分析以下").append(getAnalysisTypeDesc(analysisType)).append("问题：\n\n");

            if (errorMessage != null && !errorMessage.isEmpty()) {
                userPrompt.append("错误信息：\n").append(errorMessage).append("\n\n");
            }

            if (logSnippet != null && !logSnippet.isEmpty()) {
                userPrompt.append("日志片段：\n```\n").append(logSnippet).append("\n```\n\n");
            }

            if (!diagnosticData.isEmpty()) {
                userPrompt.append("诊断数据：\n").append(diagnosticData.toJSONString()).append("\n\n");
            }

            userPrompt.append("请输出JSON格式的分析结果：");

            String response = adapter.chat(DIAGNOSTIC_SYSTEM_PROMPT, userPrompt.toString(), modelConfig);

            // 解析结果
            return parseAnalysisResult(response);

        } catch (Exception e) {
            log.warn("LLM分析失败，回退到规则分析: {}", e.getMessage());
            return analyzeWithRules(analysisType, diagnosticData, errorMessage);
        }
    }

    /**
     * 使用规则分析
     */
    private JSONObject analyzeWithRules(String analysisType, JSONObject diagnosticData, String errorMessage) {
        JSONObject result = new JSONObject();

        switch (analysisType) {
            case "JOB_FAILURE" -> result.putAll(analyzeJobFailure(errorMessage));
            case "DATA_QUALITY" -> result.putAll(analyzeDataQuality(diagnosticData, errorMessage));
            case "SYNC_ERROR" -> result.putAll(analyzeSyncError(errorMessage));
            case "CONNECTION" -> result.putAll(analyzeConnection(errorMessage));
            case "PERFORMANCE" -> result.putAll(analyzePerformance(diagnosticData, errorMessage));
            default -> {
                result.put("rootCause", "未知问题类型");
                result.put("solutions", List.of("请提供更多信息以便分析"));
            }
        }

        return result;
    }

    /**
     * 分析任务失败
     */
    private JSONObject analyzeJobFailure(String errorMessage) {
        JSONObject result = new JSONObject();

        // 根据错误信息判断原因
        if (errorMessage != null) {
            String lower = errorMessage.toLowerCase();

            if (lower.contains("timeout") || lower.contains("超时")) {
                result.put("rootCause", "连接超时");
                result.put("rootCauseDetail", "数据库或网络连接超时，可能是网络不稳定或数据库负载过高");
                result.put("impactLevel", "MEDIUM");
                result.put("solutions", List.of(
                        createSolution(1, "增加连接超时时间配置", List.of("修改配置文件中的timeout参数"), false, null),
                        createSolution(2, "检查网络连接状态", List.of("ping目标服务器", "检查防火墙设置"), false, null),
                        createSolution(3, "检查数据库状态", List.of("查看数据库连接数", "检查慢查询日志"), false, null)
                ));
            } else if (lower.contains("connection refused") || lower.contains("连接被拒绝")) {
                result.put("rootCause", "连接被拒绝");
                result.put("rootCauseDetail", "目标服务未启动或端口不可访问");
                result.put("impactLevel", "HIGH");
                result.put("solutions", List.of(
                        createSolution(1, "检查目标服务状态", List.of("确认服务是否运行", "检查端口监听状态"), false, null),
                        createSolution(2, "检查网络连通性", List.of("telnet测试端口", "检查防火墙规则"), false, null)
                ));
            } else if (lower.contains("out of memory") || lower.contains("内存不足")) {
                result.put("rootCause", "内存不足");
                result.put("rootCauseDetail", "系统内存耗尽，无法完成操作");
                result.put("impactLevel", "HIGH");
                result.put("solutions", List.of(
                        createSolution(1, "增加系统内存", List.of("扩展服务器内存配置"), false, null),
                        createSolution(2, "优化数据处理逻辑", List.of("分批处理数据", "减少内存占用"), false, null),
                        createSolution(3, "调整JVM参数", List.of("增大堆内存配置"), false, null)
                ));
            } else {
                result.put("rootCause", "执行异常");
                result.put("rootCauseDetail", "任务执行过程中发生错误: " + errorMessage);
                result.put("impactLevel", "MEDIUM");
                result.put("solutions", List.of(
                        createSolution(1, "查看详细日志", List.of("检查完整错误堆栈"), false, null),
                        createSolution(2, "联系技术支持", List.of("提供错误日志", "描述操作步骤"), false, null)
                ));
            }
        } else {
            result.put("rootCause", "未知错误");
            result.put("impactLevel", "LOW");
            result.put("solutions", List.of(createSolution(1, "收集更多信息", List.of("查看日志", "检查监控"), false, null)));
        }

        result.put("prevention", List.of(
                "配置任务监控告警",
                "定期检查系统资源",
                "建立错误处理机制"
        ));

        return result;
    }

    /**
     * 分析数据质量问题
     */
    private JSONObject analyzeDataQuality(JSONObject diagnosticData, String errorMessage) {
        JSONObject result = new JSONObject();
        result.put("rootCause", "数据质量问题");
        result.put("rootCauseDetail", "数据不符合质量规则要求");
        result.put("impactLevel", "MEDIUM");

        result.put("solutions", List.of(
                createSolution(1, "查看质量检测报告", List.of("分析错误数据分布", "定位问题字段"), false, null),
                createSolution(2, "修复源数据", List.of("更新错误数据", "补充缺失数据"), false, null),
                createSolution(3, "优化质量规则", List.of("调整规则阈值", "添加例外配置"), false, null)
        ));

        result.put("prevention", List.of(
                "在数据入库前进行校验",
                "配置实时质量监控",
                "建立数据问题反馈机制"
        ));

        return result;
    }

    /**
     * 分析同步错误
     */
    private JSONObject analyzeSyncError(String errorMessage) {
        JSONObject result = new JSONObject();
        result.put("rootCause", "数据同步异常");
        result.put("rootCauseDetail", errorMessage != null ? errorMessage : "同步过程发生错误");
        result.put("impactLevel", "MEDIUM");

        result.put("solutions", List.of(
                createSolution(1, "检查源表结构变更", List.of("对比表结构差异", "更新字段映射"), false, null),
                createSolution(2, "重新同步数据", List.of("清除已同步数据", "重新执行同步任务"), false, null),
                createSolution(3, "检查数据类型兼容性", List.of("确认类型转换规则", "调整转换配置"), false, null)
        ));

        return result;
    }

    /**
     * 分析连接问题
     */
    private JSONObject analyzeConnection(String errorMessage) {
        JSONObject result = new JSONObject();
        result.put("rootCause", "数据库连接问题");
        result.put("rootCauseDetail", errorMessage != null ? errorMessage : "无法连接到数据库");
        result.put("impactLevel", "HIGH");

        result.put("solutions", List.of(
                createSolution(1, "检查数据库服务状态", List.of("确认服务运行中", "检查端口监听"), false, null),
                createSolution(2, "验证连接配置", List.of("检查连接字符串", "验证用户名密码"), false, null),
                createSolution(3, "检查网络连接", List.of("测试网络连通性", "检查防火墙规则"), false, null)
        ));

        result.put("prevention", List.of(
                "配置连接池",
                "设置连接健康检查",
                "配置连接失败告警"
        ));

        return result;
    }

    /**
     * 分析性能问题
     */
    private JSONObject analyzePerformance(JSONObject diagnosticData, String errorMessage) {
        JSONObject result = new JSONObject();
        result.put("rootCause", "性能瓶颈");
        result.put("rootCauseDetail", "系统响应缓慢或处理效率低");
        result.put("impactLevel", "MEDIUM");

        result.put("solutions", List.of(
                createSolution(1, "优化查询语句", List.of("添加索引", "优化SQL"), false, null),
                createSolution(2, "扩展资源配置", List.of("增加内存", "增加CPU"), false, null),
                createSolution(3, "调整并发配置", List.of("优化线程池配置", "调整并行度"), false, null)
        ));

        return result;
    }

    /**
     * 解析分析结果
     */
    private JSONObject parseAnalysisResult(String response) {
        if (response == null || response.isEmpty()) {
            return new JSONObject();
        }

        try {
            String jsonStr = response;

            if (jsonStr.contains("```json")) {
                int start = jsonStr.indexOf("```json") + 7;
                int end = jsonStr.indexOf("```", start);
                if (end > start) {
                    jsonStr = jsonStr.substring(start, end).trim();
                }
            } else if (jsonStr.contains("```")) {
                int start = jsonStr.indexOf("```") + 3;
                int end = jsonStr.indexOf("```", start);
                if (end > start) {
                    jsonStr = jsonStr.substring(start, end).trim();
                }
            }

            if (jsonStr.startsWith("{")) {
                return JSONObject.parseObject(jsonStr);
            }

        } catch (Exception e) {
            log.warn("解析分析结果失败: {}", e.getMessage());
        }

        return new JSONObject();
    }

    /**
     * 创建解决方案
     */
    private JSONObject createSolution(int priority, String description, List<String> steps, boolean autoFix, String autoFixAction) {
        JSONObject solution = new JSONObject();
        solution.put("priority", priority);
        solution.put("description", description);
        solution.put("steps", steps);
        solution.put("autoFix", autoFix);
        if (autoFixAction != null) {
            solution.put("autoFixAction", autoFixAction);
        }
        return solution;
    }

    /**
     * 获取分析类型描述
     */
    private String getAnalysisTypeDesc(String analysisType) {
        return switch (analysisType) {
            case "JOB_FAILURE" -> "任务失败";
            case "DATA_QUALITY" -> "数据质量";
            case "SYNC_ERROR" -> "同步错误";
            case "CONNECTION" -> "连接问题";
            case "PERFORMANCE" -> "性能问题";
            default -> "系统";
        };
    }
}