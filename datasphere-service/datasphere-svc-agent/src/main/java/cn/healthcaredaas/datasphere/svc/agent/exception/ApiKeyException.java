package cn.healthcaredaas.datasphere.svc.agent.exception;

import cn.healthcaredaas.datasphere.svc.agent.constant.AgentConstants;

/**
 * API密钥异常
 *
 * @author chenpan
 */
public class ApiKeyException extends AgentException {

    public ApiKeyException(String message) {
        super(AgentConstants.ERROR_CODE_API_KEY_INVALID, message);
    }

    public static ApiKeyException notFound(String apiKey) {
        return new ApiKeyException("API密钥不存在: " + maskApiKey(apiKey));
    }

    public static ApiKeyException expired(String apiKey) {
        return new ApiKeyException("API密钥已过期: " + maskApiKey(apiKey));
    }

    public static ApiKeyException disabled(String apiKey) {
        return new ApiKeyException("API密钥已禁用: " + maskApiKey(apiKey));
    }

    public static ApiKeyException permissionDenied(String apiKey, String permission) {
        return new ApiKeyException("API密钥无权限 [" + permission + "]: " + maskApiKey(apiKey));
    }

    public static ApiKeyException rateLimitExceeded(String apiKey) {
        return new ApiKeyException("API密钥请求频率超限: " + maskApiKey(apiKey));
    }

    private static String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 10) {
            return "***";
        }
        return apiKey.substring(0, 6) + "****" + apiKey.substring(apiKey.length() - 4);
    }
}