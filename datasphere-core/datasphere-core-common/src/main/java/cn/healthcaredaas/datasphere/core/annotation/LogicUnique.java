package cn.healthcaredaas.datasphere.core.annotation;

import java.lang.annotation.*;

/**
 * 逻辑唯一性约束注解
 * 用于标记字段在逻辑删除范围内唯一
 *
 * @author chenpan
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogicUnique {

    /**
     * 唯一性约束字段（别名，兼容旧代码）
     */
    String[] columns() default {};

    /**
     * 唯一性约束字段
     */
    String[] fields() default {};

    /**
     * 错误消息
     */
    String message() default "数据已存在";
}
