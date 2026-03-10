package cn.healthcaredaas.datasphere.svc.integration.runner.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 *
 * @author chenpan
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleException(Exception e) {
        log.error("System error: ", e);
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", 500);
        result.put("message", "系统错误: " + e.getMessage());
        return result;
    }
}
