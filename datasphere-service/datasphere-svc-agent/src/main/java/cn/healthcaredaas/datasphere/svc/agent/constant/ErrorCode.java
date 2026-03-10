package cn.healthcaredaas.datasphere.svc.agent.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 *
 * @author chenpan
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),

    // Agent模块错误 10xxx
    SESSION_NOT_FOUND(10001, "会话不存在"),
    MODEL_NOT_FOUND(10002, "模型配置不存在"),
    API_KEY_INVALID(10003, "API密钥无效"),
    TOKEN_EXHAUSTED(10004, "Token额度不足"),
    TOOL_EXECUTION_FAILED(10005, "工具执行失败"),
    LLM_CALL_FAILED(10006, "LLM调用失败"),
    PERMISSION_DENIED(10007, "权限不足"),
    RATE_LIMIT_EXCEEDED(10008, "请求频率超限"),

    // 参数错误 11xxx
    INVALID_PARAM(11001, "参数无效"),
    MISSING_PARAM(11002, "缺少必填参数"),
    PARAM_TYPE_ERROR(11003, "参数类型错误"),

    // 业务错误 12xxx
    SQL_NOT_ALLOWED(12001, "SQL语句不允许执行"),
    SQL_SYNTAX_ERROR(12002, "SQL语法错误"),
    DATASOURCE_ERROR(12003, "数据源错误"),
    METADATA_NOT_FOUND(12004, "元数据不存在"),

    // 服务错误 5xx
    INTERNAL_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    GATEWAY_TIMEOUT(504, "网关超时");

    private final int code;
    private final String message;

    /**
     * 根据错误码获取枚举
     */
    public static ErrorCode fromCode(int code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return INTERNAL_ERROR;
    }
}