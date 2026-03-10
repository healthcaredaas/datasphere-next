package cn.healthcaredaas.datasphere.core.annotation;

import java.lang.annotation.*;

/**
 * 模糊查询字段注解
 * 用于标记实体类字段支持模糊查询
 *
 * @author chenpan
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SelectLikeColumn {

    /**
     * 通配符位置
     */
    WildcardPosition wildcardPosition() default WildcardPosition.BOTH;

    /**
     * 通配符位置枚举
     */
    enum WildcardPosition {
        /**
         * 前缀匹配，如：value%
         */
        PREFIX,
        /**
         * 后缀匹配，如：%value
         */
        SUFFIX,
        /**
         * 前后匹配，如：%value%
         */
        BOTH
    }
}
