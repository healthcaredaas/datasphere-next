package cn.healthcaredaas.datasphere.svc.agent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Agent配置属性
 *
 * @author chenpan
 */
@Data
@Component
@ConfigurationProperties(prefix = "datasphere.agent")
public class AgentProperties {

    /**
     * 默认模型类型
     */
    private String defaultModel = "CLAUDE";

    /**
     * 最大历史消息数
     */
    private int maxHistoryMessages = 20;

    /**
     * 流式响应超时时间(秒)
     */
    private int streamTimeout = 60;

    /**
     * 工具执行超时时间(秒)
     */
    private int toolTimeout = 30;

    /**
     * SQL执行配置
     */
    private SqlConfig sql = new SqlConfig();

    /**
     * 限流配置
     */
    private RateLimitConfig rateLimit = new RateLimitConfig();

    @Data
    public static class SqlConfig {
        /**
         * 最大返回行数
         */
        private int maxRows = 10000;

        /**
         * 执行超时(秒)
         */
        private int timeout = 30;

        /**
         * 是否只允许SELECT
         */
        private boolean selectOnly = true;
    }

    @Data
    public static class RateLimitConfig {
        /**
         * 每分钟最大请求数
         */
        private int requestsPerMinute = 100;

        /**
         * 每分钟最大Token数
         */
        private int tokensPerMinute = 100000;
    }
}