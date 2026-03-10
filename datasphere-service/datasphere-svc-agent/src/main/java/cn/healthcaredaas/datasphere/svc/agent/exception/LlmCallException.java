package cn.healthcaredaas.datasphere.svc.agent.exception;

import cn.healthcaredaas.datasphere.svc.agent.constant.AgentConstants;

/**
 * LLM调用异常
 *
 * @author chenpan
 */
public class LlmCallException extends AgentException {

    private final String modelType;
    private final String modelName;

    public LlmCallException(String modelType, String modelName, String message) {
        super(AgentConstants.ERROR_CODE_LLM_CALL_FAILED,
                "LLM调用失败 [" + modelType + "/" + modelName + "]: " + message);
        this.modelType = modelType;
        this.modelName = modelName;
    }

    public LlmCallException(String modelType, String modelName, String message, Throwable cause) {
        super(AgentConstants.ERROR_CODE_LLM_CALL_FAILED,
                "LLM调用失败 [" + modelType + "/" + modelName + "]: " + message, cause);
        this.modelType = modelType;
        this.modelName = modelName;
    }

    public String getModelType() {
        return modelType;
    }

    public String getModelName() {
        return modelName;
    }

    public static LlmCallException apiKeyInvalid(String modelType) {
        return new LlmCallException(modelType, "unknown", "API密钥无效");
    }

    public static LlmCallException rateLimitExceeded(String modelType, String modelName) {
        return new LlmCallException(modelType, modelName, "请求频率超限");
    }

    public static LlmCallException contextLengthExceeded(String modelType, String modelName, int maxTokens) {
        return new LlmCallException(modelType, modelName,
                "上下文长度超限，最大支持 " + maxTokens + " tokens");
    }

    public static LlmCallException connectionFailed(String modelType, String modelName, Throwable cause) {
        return new LlmCallException(modelType, modelName, "连接失败", cause);
    }

    public static LlmCallException responseError(String modelType, String modelName, String error) {
        return new LlmCallException(modelType, modelName, "响应错误: " + error);
    }
}