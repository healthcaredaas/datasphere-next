package cn.healthcaredaas.datasphere.svc.integration.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
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

    @ExceptionHandler(BindException.class)
    public Map<String, Object> handleBindException(BindException e) {
        log.warn("Validation error: {}", e.getMessage());
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", 400);
        result.put("message", message);
        return result;
    }

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
