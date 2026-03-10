package cn.healthcaredaas.datasphere.svc.agent.constant;

/**
 * Agent常量定义
 *
 * @author chenpan
 */
public class AgentConstants {

    // ========== 会话状态 ==========
    public static final String SESSION_STATUS_ACTIVE = "ACTIVE";
    public static final String SESSION_STATUS_ARCHIVED = "ARCHIVED";
    public static final String SESSION_STATUS_DELETED = "DELETED";

    // ========== 消息角色 ==========
    public static final String MESSAGE_ROLE_USER = "USER";
    public static final String MESSAGE_ROLE_ASSISTANT = "ASSISTANT";
    public static final String MESSAGE_ROLE_SYSTEM = "SYSTEM";
    public static final String MESSAGE_ROLE_TOOL = "TOOL";

    // ========== 内容类型 ==========
    public static final String CONTENT_TYPE_TEXT = "TEXT";
    public static final String CONTENT_TYPE_SQL = "SQL";
    public static final String CONTENT_TYPE_TABLE = "TABLE";
    public static final String CONTENT_TYPE_CHART = "CHART";
    public static final String CONTENT_TYPE_ERROR = "ERROR";

    // ========== 模型类型 ==========
    public static final String MODEL_TYPE_CLAUDE = "CLAUDE";
    public static final String MODEL_TYPE_GPT = "GPT";
    public static final String MODEL_TYPE_QWEN = "QWEN";
    public static final String MODEL_TYPE_LLAMA = "LLAMA";
    public static final String MODEL_TYPE_LOCAL = "LOCAL";

    // ========== 知识类型 ==========
    public static final String KNOWLEDGE_TYPE_STANDARD = "STANDARD";
    public static final String KNOWLEDGE_TYPE_TEMPLATE = "TEMPLATE";
    public static final String KNOWLEDGE_TYPE_FAQ = "FAQ";
    public static final String KNOWLEDGE_TYPE_METADATA = "METADATA";

    // ========== 工具类型 ==========
    public static final String TOOL_TYPE_EXECUTION = "EXECUTION";
    public static final String TOOL_TYPE_GENERATION = "GENERATION";
    public static final String TOOL_TYPE_QUERY = "QUERY";
    public static final String TOOL_TYPE_ANALYSIS = "ANALYSIS";

    // ========== 操作类型 ==========
    public static final String OPERATION_CHAT = "CHAT";
    public static final String OPERATION_SQL_GENERATE = "SQL_GENERATE";
    public static final String OPERATION_SQL_EXECUTE = "SQL_EXECUTE";
    public static final String OPERATION_PIPELINE_GENERATE = "PIPELINE_GENERATE";
    public static final String OPERATION_QUALITY_RULE_GENERATE = "QUALITY_RULE_GENERATE";
    public static final String OPERATION_METADATA_QUERY = "METADATA_QUERY";

    // ========== 执行状态 ==========
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAIL = "FAIL";
    public static final String STATUS_TIMEOUT = "TIMEOUT";

    // ========== 错误码 ==========
    public static final int ERROR_CODE_SESSION_NOT_FOUND = 10001;
    public static final int ERROR_CODE_MODEL_NOT_FOUND = 10002;
    public static final int ERROR_CODE_API_KEY_INVALID = 10003;
    public static final int ERROR_CODE_TOKEN_EXHAUSTED = 10004;
    public static final int ERROR_CODE_TOOL_EXECUTION_FAILED = 10005;
    public static final int ERROR_CODE_LLM_CALL_FAILED = 10006;
    public static final int ERROR_CODE_PERMISSION_DENIED = 10007;
    public static final int ERROR_CODE_RATE_LIMIT_EXCEEDED = 10008;

    private AgentConstants() {
    }
}