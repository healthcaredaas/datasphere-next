package cn.healthcaredaas.datasphere.svc.agent.exception;

import cn.healthcaredaas.datasphere.svc.agent.constant.AgentConstants;

/**
 * 模型配置异常
 *
 * @author chenpan
 */
public class ModelConfigException extends AgentException {

    public ModelConfigException(String message) {
        super(AgentConstants.ERROR_CODE_MODEL_NOT_FOUND, message);
    }

    public ModelConfigException(String message, Throwable cause) {
        super(AgentConstants.ERROR_CODE_MODEL_NOT_FOUND, message, cause);
    }

    public static ModelConfigException notFound(String modelId) {
        return new ModelConfigException("模型配置不存在: " + modelId);
    }

    public static ModelConfigException disabled(String modelId) {
        return new ModelConfigException("模型已禁用: " + modelId);
    }

    public static ModelConfigException noDefaultModel() {
        return new ModelConfigException("未配置默认模型");
    }

    public static ModelConfigException connectionFailed(String modelId, Throwable cause) {
        return new ModelConfigException("模型连接失败: " + modelId, cause);
    }
}