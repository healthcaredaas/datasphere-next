package cn.healthcaredaas.datasphere.core.exception;

import lombok.Getter;

/**
 * 错误码枚举
 *
 * @author chenpan
 */
@Getter
public enum ErrorCode {

    // 系统级错误 (1xxxx)
    SUCCESS(200, "操作成功"),
    ERROR(500, "系统错误"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    REQUEST_TIMEOUT(408, "请求超时"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    // 数据相关错误 (2xxxx)
    DATA_NOT_FOUND(20001, "数据不存在"),
    DATA_ALREADY_EXISTS(20002, "数据已存在"),
    DATA_SAVE_ERROR(20003, "数据保存失败"),
    DATA_UPDATE_ERROR(20004, "数据更新失败"),
    DATA_DELETE_ERROR(20005, "数据删除失败"),
    DATA_FORMAT_ERROR(20006, "数据格式错误"),

    // 数据源相关错误 (3xxxx)
    DATASOURCE_NOT_FOUND(30001, "数据源不存在"),
    DATASOURCE_CONNECT_ERROR(30002, "数据源连接失败"),
    DATASOURCE_TEST_ERROR(30003, "数据源测试失败"),
    DATASOURCE_TYPE_NOT_SUPPORT(30004, "不支持的数据源类型"),

    // 数据质量相关错误 (4xxxx)
    QUALITY_RULE_NOT_FOUND(40001, "质量规则不存在"),
    QUALITY_RULE_EXECUTE_ERROR(40002, "规则执行失败"),
    QUALITY_TEMPLATE_NOT_FOUND(40003, "规则模板不存在"),
    QUALITY_TEMPLATE_PARAM_ERROR(40004, "模板参数错误"),
    QUALITY_TASK_NOT_FOUND(40005, "检测任务不存在"),
    QUALITY_TASK_EXECUTE_ERROR(40006, "任务执行失败"),

    // 数据资产相关错误 (5xxxx)
    ASSET_NOT_FOUND(50001, "资产不存在"),
    ASSET_CATEGORY_NOT_FOUND(50002, "资产分类不存在"),
    ASSET_TAG_NOT_FOUND(50003, "资产标签不存在"),
    ASSET_LINEAGE_ERROR(50004, "血缘解析失败"),

    // 数据安全相关错误 (6xxxx)
    MASK_RULE_NOT_FOUND(60001, "脱敏规则不存在"),
    MASK_ALGORITHM_ERROR(60002, "脱敏算法错误"),
    SENSITIVE_FIELD_DETECT_ERROR(60003, "敏感字段识别失败"),

    // 数据集成相关错误 (7xxxx)
    INTEGRATION_JOB_NOT_FOUND(70001, "集成作业不存在"),
    INTEGRATION_JOB_EXECUTE_ERROR(70002, "作业执行失败"),
    INTEGRATION_PIPELINE_NOT_FOUND(70003, "数据管道不存在"),
    SEATUNNEL_CONFIG_ERROR(70004, "SeaTunnel配置错误"),

    // 主数据相关错误 (8xxxx)
    MASTER_DATA_NOT_FOUND(80001, "主数据不存在"),
    MASTER_DATA_CODE_EXISTS(80002, "主数据编码已存在"),

    // 元数据相关错误 (9xxxx)
    METADATA_NOT_FOUND(90001, "元数据不存在"),
    METAMODEL_NOT_FOUND(90002, "元模型不存在");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
