package cn.healthcaredaas.datasphere.core.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解
 *
 * @author chenpan
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流key前缀
     */
    String prefix() default "rate:limit:";

    /**
     * 限流key，支持SpEL表达式
     */
    String key() default "";

    /**
     * 每秒允许的请求数
     */
    double permitsPerSecond() default 10.0;

    /**
     * 限流时间窗口（秒）
     */
    int timeout() default 1;

    /**
     * 限流提示消息
     */
    String message() default "请求过于频繁，请稍后重试";

    /**
     * 限流类型
     */
    LimitType limitType() default LimitType.DEFAULT;

    /**
     * 限流类型枚举
     */
    enum LimitType {
        /**
         * 默认限流（针对方法）
         */
        DEFAULT,

        /**
         * 根据IP限流
         */
        IP,

        /**
         * 根据用户限流
         */
        USER,

        /**
         * 根据IP+方法限流
         */
        IP_METHOD,

        /**
         * 根据用户+方法限流
         */
        USER_METHOD
    }
}
