package cn.healthcaredaas.datasphere.svc.quality.engine;

import lombok.Getter;

/**
 * 规则类型枚举
 *
 * @author chenpan
 */
@Getter
public enum RuleType {
    COMPLETENESS("COMPLETENESS", "完整性检查", "检查字段值是否为空"),
    UNIQUENESS("UNIQUENESS", "唯一性检查", "检查字段值是否重复"),
    FORMAT("FORMAT", "格式检查", "检查字段值是否符合指定格式"),
    VALUE_RANGE("VALUE_RANGE", "值域检查", "检查字段值是否在指定范围内"),
    CONSISTENCY("CONSISTENCY", "一致性检查", "检查跨表或跨字段的一致性"),
    ACCURACY("ACCURACY", "准确性检查", "检查数据是否符合业务规则"),
    CUSTOM("CUSTOM", "自定义检查", "使用自定义SQL进行检查");

    private final String code;
    private final String name;
    private final String description;

    RuleType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static RuleType fromCode(String code) {
        for (RuleType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
