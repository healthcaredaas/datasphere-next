package cn.healthcaredaas.datasphere.core.util;

import java.util.UUID;

/**
 * ID生成工具类
 *
 * @author chenpan
 */
public class IdUtils {

    /**
     * 生成UUID（无横线）
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成雪花ID（如果引入了Snowflake算法）
     */
    public static String snowflakeId() {
        // 可以集成Snowflake算法
        return uuid();
    }
}
