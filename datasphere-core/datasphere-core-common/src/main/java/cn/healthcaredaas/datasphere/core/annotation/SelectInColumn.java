package cn.healthcaredaas.datasphere.core.annotation;

import java.lang.annotation.*;

/**
 * IN查询字段注解
 * 用于标记实体类字段支持IN查询
 *
 * @author chenpan
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SelectInColumn {

    /**
     * 对应的数据库字段名（可选）
     */
    String column() default "";
}
