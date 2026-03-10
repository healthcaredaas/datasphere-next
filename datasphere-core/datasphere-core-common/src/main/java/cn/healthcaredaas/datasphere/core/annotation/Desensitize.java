package cn.healthcaredaas.datasphere.core.annotation;

import java.lang.annotation.*;

/**
 * 字段脱敏注解
 *
 * @author chenpan
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Desensitize {

    /**
     * 脱敏类型
     */
    DesensitizeType type();

    /**
     * 自定义正则表达式（当type为CUSTOM时有效）
     */
    String pattern() default "";

    /**
     * 自定义替换值（当type为CUSTOM时有效）
     */
    String replacement() default "*";

    /**
     * 脱敏类型枚举
     */
    enum DesensitizeType {
        /**
         * 手机号
         */
        PHONE,

        /**
         * 邮箱
         */
        EMAIL,

        /**
         * 身份证号
         */
        ID_CARD,

        /**
         * 银行卡号
         */
        BANK_CARD,

        /**
         * 姓名
         */
        NAME,

        /**
         * 地址
         */
        ADDRESS,

        /**
         * 自定义
         */
        CUSTOM
    }
}
