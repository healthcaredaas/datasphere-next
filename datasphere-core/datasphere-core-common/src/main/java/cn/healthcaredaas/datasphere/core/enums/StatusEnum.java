package cn.healthcaredaas.datasphere.core.enums;

import lombok.Getter;

/**
 * 状态枚举
 *
 * @author chenpan
 */
@Getter
public enum StatusEnum {

    DISABLE(0, "禁用"),
    ENABLE(1, "启用");

    private final int code;
    private final String desc;

    StatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
