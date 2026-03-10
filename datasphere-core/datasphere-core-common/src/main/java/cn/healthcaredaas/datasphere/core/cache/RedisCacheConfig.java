package cn.healthcaredaas.datasphere.core.cache;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis缓存配置
 *
 * @author chenpan
 */
@Configuration
@EnableCaching
public class RedisCacheConfig extends CachingConfigurerSupport {

    /**
     * 默认缓存过期时间（分钟）
     */
    private static final long DEFAULT_TTL = 30;

    /**
     * 缓存名称及过期时间配置
     */
    private static final Map<String, Long> CACHE_CONFIG = new HashMap<>();

    static {
        // 数据源缓存 - 1小时
        CACHE_CONFIG.put("datasource", 60L);
        // 元数据缓存 - 30分钟
        CACHE_CONFIG.put("metadata", 30L);
        // 质量规则缓存 - 10分钟
        CACHE_CONFIG.put("quality:rule", 10L);
        // 质量报告缓存 - 5分钟
        CACHE_CONFIG.put("quality:report", 5L);
        // 资产数据缓存 - 20分钟
        CACHE_CONFIG.put("asset", 20L);
        // 安全规则缓存 - 30分钟
        CACHE_CONFIG.put("security:rule", 30L);
        // 字典数据缓存 - 60分钟
        CACHE_CONFIG.put("dict", 60L);
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getSimpleName()).append(":");
            sb.append(method.getName()).append(":");
            for (Object param : params) {
                if (param != null) {
                    sb.append(param.toString()).append("_");
                }
            }
            return sb.toString();
        };
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用String序列化器作为key的序列化器
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // 使用JSON序列化器作为value的序列化器
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 默认缓存配置
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(DEFAULT_TTL))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        // 构建不同缓存的过期时间配置
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        CACHE_CONFIG.forEach((cacheName, ttl) -> {
            configMap.put(cacheName, defaultConfig.entryTtl(Duration.ofMinutes(ttl)));
        });

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configMap)
                .transactionAware()
                .build();
    }
}
