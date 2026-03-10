package cn.healthcaredaas.datasphere.core.annotation;

import java.lang.annotation.*;

/**
 * 启用选择选项注解
 * 用于标记实体类支持选择选项
 *
 * @author chenpan
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableSelectOption {

    /**
     * 标签字段
     */
    String label() default "name";

    /**
     * 值字段
     */
    String value() default "id";
}
