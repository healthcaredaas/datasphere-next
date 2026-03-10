package cn.healthcaredaas.datasphere.svc.agent.advice;

import cn.healthcaredaas.datasphere.svc.agent.constant.ErrorCode;
import cn.healthcaredaas.datasphere.svc.agent.exception.AgentException;
import cn.healthcaredaas.datasphere.svc.agent.exception.ApiKeyException;
import cn.healthcaredaas.datasphere.svc.agent.exception.LlmCallException;
import cn.healthcaredaas.datasphere.svc.agent.exception.ToolExecutionException;
import cn.healthcaredaas.datasphere.svc.agent.exception.TokenExhaustedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理
 *
 * @author chenpan
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理Agent异常
     */
    @ExceptionHandler(AgentException.class)
    public ResponseEntity<Map<String, Object>> handleAgentException(AgentException e) {
        log.warn("Agent异常: code={}, message={}", e.getCode(), e.getMessage());

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", e.getCode());
        result.put("message", e.getMessage());
        return ResponseEntity.status(getHttpStatus(e.getCode())).body(result);
    }

    /**
     * 处理API密钥异常
     */
    @ExceptionHandler(ApiKeyException.class)
    public ResponseEntity<Map<String, Object>> handleApiKeyException(ApiKeyException e) {
        log.warn("API密钥异常: {}", e.getMessage());

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", e.getCode());
        result.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }

    /**
     * 处理Token额度异常
     */
    @ExceptionHandler(TokenExhaustedException.class)
    public ResponseEntity<Map<String, Object>> handleTokenExhaustedException(TokenExhaustedException e) {
        log.warn("Token额度不足: userId={}, used={}, max={}",
                e.getUserId(), e.getUsedTokens(), e.getMaxTokens());

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", e.getCode());
        result.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(result);
    }

    /**
     * 处理工具执行异常
     */
    @ExceptionHandler(ToolExecutionException.class)
    public ResponseEntity<Map<String, Object>> handleToolExecutionException(ToolExecutionException e) {
        log.error("工具执行异常: tool={}, message={}", e.getToolName(), e.getMessage(), e);

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", e.getCode());
        result.put("message", e.getMessage());
        result.put("toolName", e.getToolName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 处理LLM调用异常
     */
    @ExceptionHandler(LlmCallException.class)
    public ResponseEntity<Map<String, Object>> handleLlmCallException(LlmCallException e) {
        log.error("LLM调用异常: model={}/{}, message={}",
                e.getModelType(), e.getModelName(), e.getMessage(), e);

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", e.getCode());
        result.put("message", e.getMessage());
        result.put("modelType", e.getModelType());
        result.put("modelName", e.getModelName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("参数验证失败: {}", errors);

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", ErrorCode.INVALID_PARAM.getCode());
        result.put("message", "参数验证失败: " + errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> handleBindException(BindException e) {
        String errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("参数绑定失败: {}", errors);

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", ErrorCode.INVALID_PARAM.getCode());
        result.put("message", "参数绑定失败: " + errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("参数异常: {}", e.getMessage());

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", ErrorCode.INVALID_PARAM.getCode());
        result.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", ErrorCode.INTERNAL_ERROR.getCode());
        result.put("message", "服务器内部错误: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", ErrorCode.INTERNAL_ERROR.getCode());
        result.put("message", "系统异常，请稍后重试");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 根据错误码获取HTTP状态码
     */
    private HttpStatus getHttpStatus(int code) {
        if (code >= 10001 && code <= 10999) {
            // Agent模块错误
            if (code == 10003 || code == 10007) {
                return HttpStatus.UNAUTHORIZED;
            }
            if (code == 10004) {
                return HttpStatus.PAYMENT_REQUIRED;
            }
            if (code == 10008) {
                return HttpStatus.TOO_MANY_REQUESTS;
            }
            return HttpStatus.BAD_REQUEST;
        }
        if (code >= 11001 && code <= 11999) {
            // 参数错误
            return HttpStatus.BAD_REQUEST;
        }
        if (code >= 12001 && code <= 12999) {
            // 业务错误
            return HttpStatus.UNPROCESSABLE_ENTITY;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}