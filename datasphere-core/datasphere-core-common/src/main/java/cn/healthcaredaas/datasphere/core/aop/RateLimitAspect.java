package cn.healthcaredaas.datasphere.core.aop;

import cn.healthcaredaas.datasphere.core.annotation.RateLimit;
import cn.healthcaredaas.datasphere.core.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * 接口限流AOP
 *
 * @author chenpan
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final StringRedisTemplate redisTemplate;

    /**
     * Lua脚本：限流判断
     * KEYS[1]: 限流key
     * ARGV[1]: 限流阈值
     * ARGV[2]: 时间窗口（秒）
     * ARGV[3]: 当前时间戳（毫秒）
     */
    private static final String RATE_LIMIT_LUA =
            "local key = KEYS[1]\n" +
            "local limit = tonumber(ARGV[1])\n" +
            "local window = tonumber(ARGV[2])\n" +
            "local now = tonumber(ARGV[3])\n" +
            "local windowStart = now - (window * 1000)\n" +
            "redis.call('ZREMRANGEBYSCORE', key, 0, windowStart)\n" +
            "local count = redis.call('ZCARD', key)\n" +
            "if count < limit then\n" +
            "    redis.call('ZADD', key, now, now)\n" +
            "    redis.call('EXPIRE', key, window)\n" +
            "    return 1\n" +
            "else\n" +
            "    return 0\n" +
            "end";

    private final RedisScript<Long> rateLimitScript = RedisScript.of(RATE_LIMIT_LUA, Long.class);

    @Around("@annotation(cn.healthcaredaas.datasphere.core.annotation.RateLimit)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        String limitKey = getLimitKey(rateLimit, method);
        double permits = rateLimit.permitsPerSecond();
        int timeout = rateLimit.timeout();

        // 执行限流判断
        boolean allowed = tryAcquire(limitKey, permits, timeout);

        if (!allowed) {
            log.warn("Rate limit exceeded, key: {}, method: {}", limitKey, method.getName());
            throw new BizException(429, rateLimit.message());
        }

        return point.proceed();
    }

    /**
     * 尝试获取许可
     */
    private boolean tryAcquire(String key, double permits, int timeout) {
        try {
            long now = Instant.now().toEpochMilli();
            List<String> keys = Collections.singletonList(key);
            List<String> args = List.of(
                    String.valueOf((int) permits),
                    String.valueOf(timeout),
                    String.valueOf(now)
            );

            Long result = redisTemplate.execute(rateLimitScript, keys, args.toArray(new String[0]));
            return result != null && result == 1;
        } catch (Exception e) {
            log.error("Rate limit check failed: {}", e.getMessage());
            // Redis异常时放行，避免影响业务
            return true;
        }
    }

    /**
     * 获取限流key
     */
    private String getLimitKey(RateLimit rateLimit, Method method) {
        String prefix = rateLimit.prefix();
        String key = rateLimit.key();

        // 根据限流类型构建key
        RateLimit.LimitType limitType = rateLimit.limitType();
        String suffix = "";

        switch (limitType) {
            case IP:
                suffix = ":" + getClientIp();
                break;
            case USER:
                suffix = ":" + getCurrentUserId();
                break;
            case IP_METHOD:
                suffix = ":" + getClientIp() + ":" + method.getName();
                break;
            case USER_METHOD:
                suffix = ":" + getCurrentUserId() + ":" + method.getName();
                break;
            case DEFAULT:
            default:
                suffix = ":" + method.getDeclaringClass().getSimpleName() + "." + method.getName();
                break;
        }

        return prefix + key + suffix;
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return "unknown";
            }
            HttpServletRequest request = attributes.getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = request.getHeader("X-Real-IP");
            }
            if (ip == null || ip.isEmpty()) {
                ip = request.getRemoteAddr();
            }
            return ip != null ? ip.split(",")[0].trim() : "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * 获取当前用户ID
     */
    private String getCurrentUserId() {
        // 从SecurityContext获取当前用户
        // 这里简化处理，实际需要根据认证框架实现
        return "anonymous";
    }
}
