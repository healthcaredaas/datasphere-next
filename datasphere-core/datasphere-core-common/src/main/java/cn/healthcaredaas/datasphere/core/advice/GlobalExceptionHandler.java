package cn.healthcaredaas.datasphere.core.advice;

import cn.healthcaredaas.datasphere.core.common.RestResult;
import cn.healthcaredaas.datasphere.core.exception.BizException;
import cn.healthcaredaas.datasphere.core.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author chenpan
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK)
    public RestResult<Void> handleBizException(BizException e, HttpServletRequest request) {
        log.warn("[BizException] Path: {}, Code: {}, Message: {}",
                request.getRequestURI(), e.getCode(), e.getMessage());
        return RestResult.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常 - MethodArgumentNotValidException
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResult<Map<String, String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("[Validation Error] Path: {}", request.getRequestURI());

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Map<String, String> errors = fieldErrors.stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ?
                                fieldError.getDefaultMessage() : "校验失败",
                        (existing, replacement) -> existing
                ));

        return RestResult.error(ErrorCode.PARAM_ERROR.getCode(), "参数校验失败", errors);
    }

    /**
     * 参数绑定异常 - BindException
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResult<Void> handleBindException(BindException e, HttpServletRequest request) {
        log.warn("[Bind Error] Path: {}, Message: {}", request.getRequestURI(), e.getMessage());
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return RestResult.error(ErrorCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResult<Void> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("[Missing Param] Path: {}, Param: {}", request.getRequestURI(), e.getParameterName());
        return RestResult.error(ErrorCode.PARAM_ERROR.getCode(),
                "缺少必要参数: " + e.getParameterName());
    }

    /**
     * 参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResult<Void> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("[Type Mismatch] Path: {}, Name: {}, Value: {}",
                request.getRequestURI(), e.getName(), e.getValue());
        return RestResult.error(ErrorCode.PARAM_ERROR.getCode(),
                String.format("参数[%s]类型错误，当前值: %s", e.getName(), e.getValue()));
    }

    /**
     * 请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public RestResult<Void> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("[Method Not Allowed] Path: {}, Method: {}",
                request.getRequestURI(), e.getMethod());
        return RestResult.error(ErrorCode.METHOD_NOT_ALLOWED.getCode(),
                "不支持的请求方法: " + e.getMethod());
    }

    /**
     * 资源不存在异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RestResult<Void> handleNoHandlerFoundException(
            NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("[Not Found] Path: {}", request.getRequestURI());
        return RestResult.error(ErrorCode.NOT_FOUND.getCode(),
                "请求路径不存在: " + request.getRequestURI());
    }

    /**
     * 其他所有异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResult<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("[System Error] Path: {}, Error: {}", request.getRequestURI(), e.getMessage(), e);
        return RestResult.error(ErrorCode.ERROR.getCode(),
                "系统繁忙，请稍后重试");
    }
}
