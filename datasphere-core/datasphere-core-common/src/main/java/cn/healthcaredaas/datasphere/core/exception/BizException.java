package cn.healthcaredaas.datasphere.core.exception;

import lombok.Getter;

/**
 * 业务异常
 *
 * @author chenpan
 */
@Getter
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    public BizException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }

    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.message = message;
    }

    /**
     * 不写入堆栈跟踪，提高性能
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
