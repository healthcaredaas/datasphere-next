package cn.healthcaredaas.datasphere.core.aop;

import cn.healthcaredaas.datasphere.core.annotation.OperationLog;
import cn.healthcaredaas.datasphere.core.util.IpUtils;
import cn.healthcaredaas.datasphere.core.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志AOP
 *
 * @author chenpan
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    /**
     * 定义切点 - 所有带有@OperationLog注解的方法
     */
    @Pointcut("@annotation(cn.healthcaredaas.datasphere.core.annotation.OperationLog)")
    public void operationLogPointcut() {
    }

    /**
     * 环绕通知
     */
    @Around("operationLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        // 构建日志数据
        Map<String, Object> logData = new HashMap<>();
        logData.put("module", operationLog.module());
        logData.put("type", operationLog.type().name());
        logData.put("description", operationLog.desc());
        logData.put("method", method.getName());
        logData.put("class", joinPoint.getTarget().getClass().getSimpleName());
        logData.put("timestamp", LocalDateTime.now().toString());

        if (request != null) {
            logData.put("url", request.getRequestURI());
            logData.put("httpMethod", request.getMethod());
            logData.put("ip", IpUtils.getIpAddress(request));
            // TODO: 从SecurityContext获取当前用户
            // logData.put("userId", SecurityUtils.getCurrentUserId());
            // logData.put("username", SecurityUtils.getCurrentUsername());
        }

        // 记录请求参数
        if (operationLog.recordParams()) {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                // 过滤掉敏感信息
                logData.put("params", filterSensitiveParams(args));
            }
        }

        // 执行方法
        Object result = null;
        long startTime = System.currentTimeMillis();
        try {
            result = joinPoint.proceed();
            logData.put("status", "SUCCESS");

            // 记录响应结果
            if (operationLog.recordResult() && result != null) {
                logData.put("result", result);
            }
        } catch (Throwable e) {
            logData.put("status", "FAILURE");
            logData.put("error", e.getMessage());
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            logData.put("duration", endTime - startTime);

            // 输出日志
            saveOperationLog(logData);
        }

        return result;
    }

    /**
     * 过滤敏感参数
     */
    private Object filterSensitiveParams(Object[] args) {
        // 实现敏感参数过滤逻辑
        // 例如：密码、token等字段不记录
        return args;
    }

    /**
     * 保存操作日志
     */
    private void saveOperationLog(Map<String, Object> logData) {
        // TODO: 可以异步保存到数据库或消息队列
        log.info("[OperationLog] {}", JsonUtils.toJson(logData));
    }
}
