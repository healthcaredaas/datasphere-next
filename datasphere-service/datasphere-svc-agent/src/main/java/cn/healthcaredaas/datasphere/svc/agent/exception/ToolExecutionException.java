package cn.healthcaredaas.datasphere.svc.agent.exception;

import cn.healthcaredaas.datasphere.svc.agent.constant.AgentConstants;

/**
 * 工具执行异常
 *
 * @author chenpan
 */
public class ToolExecutionException extends AgentException {

    private final String toolName;

    public ToolExecutionException(String toolName, String message) {
        super(AgentConstants.ERROR_CODE_TOOL_EXECUTION_FAILED,
                "工具执行失败 [" + toolName + "]: " + message);
        this.toolName = toolName;
    }

    public ToolExecutionException(String toolName, String message, Throwable cause) {
        super(AgentConstants.ERROR_CODE_TOOL_EXECUTION_FAILED,
                "工具执行失败 [" + toolName + "]: " + message, cause);
        this.toolName = toolName;
    }

    public String getToolName() {
        return toolName;
    }

    public static ToolExecutionException notFound(String toolName) {
        return new ToolExecutionException(toolName, "工具不存在");
    }

    public static ToolExecutionException permissionDenied(String toolName) {
        return new ToolExecutionException(toolName, "无执行权限");
    }

    public static ToolExecutionException timeout(String toolName) {
        return new ToolExecutionException(toolName, "执行超时");
    }

    public static ToolExecutionException invalidParams(String toolName, String detail) {
        return new ToolExecutionException(toolName, "参数无效: " + detail);
    }
}