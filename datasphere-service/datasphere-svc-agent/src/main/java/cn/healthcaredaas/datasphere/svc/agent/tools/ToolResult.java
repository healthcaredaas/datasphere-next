package cn.healthcaredaas.datasphere.svc.agent.tools;

import lombok.Data;

/**
 * 工具执行结果
 *
 * @author chenpan
 */
@Data
public class ToolResult {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 结果数据
     */
    private Object data;

    /**
     * 错误消息
     */
    private String errorMessage;

    /**
     * 执行时间(ms)
     */
    private long executionTime;

    public static ToolResult success(Object data) {
        ToolResult result = new ToolResult();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }

    public static ToolResult error(String errorMessage) {
        ToolResult result = new ToolResult();
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        return result;
    }

    public static ToolResult error(String errorMessage, Object data) {
        ToolResult result = new ToolResult();
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        result.setData(data);
        return result;
    }
}