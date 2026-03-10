package cn.healthcaredaas.datasphere.core.enums;

import lombok.Getter;

/**
 * 启用状态枚举
 *
 * @author chenpan
 */
@Getter
public enum EnableStatusEnum {

    /**
     * 启用
     */
    ENABLE("0", "启用"),

    /**
     * 禁用
     */
    DISABLE("1", "禁用");

    private final String code;
    private final String desc;

    EnableStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EnableStatusEnum getByCode(String code) {
        for (EnableStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
