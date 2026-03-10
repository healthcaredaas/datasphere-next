package cn.healthcaredaas.datasphere.svc.agent.exception;

/**
 * Agent异常基类
 *
 * @author chenpan
 */
public class AgentException extends RuntimeException {

    private final int code;

    public AgentException(String message) {
        super(message);
        this.code = 500;
    }

    public AgentException(int code, String message) {
        super(message);
        this.code = code;
    }

    public AgentException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}