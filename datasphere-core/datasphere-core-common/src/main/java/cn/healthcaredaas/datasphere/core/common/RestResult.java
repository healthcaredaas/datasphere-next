package cn.healthcaredaas.datasphere.core.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一API响应结果
 *
 * @author chenpan
 * @param <T> 数据类型
 */
@Data
@Schema(description = "统一响应结果")
public class RestResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否成功
     */
    @Schema(description = "是否成功", example = "true")
    private boolean success;

    /**
     * 响应码
     */
    @Schema(description = "响应码", example = "200")
    private Integer code;

    /**
     * 响应消息
     */
    @Schema(description = "响应消息", example = "操作成功")
    private String message;

    /**
     * 响应数据
     */
    @Schema(description = "响应数据")
    private T data;

    /**
     * 时间戳
     */
    @Schema(description = "时间戳")
    private LocalDateTime timestamp;

    /**
     * 请求路径
     */
    @Schema(description = "请求路径")
    private String path;

    public RestResult() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 成功响应
     */
    public static <T> RestResult<T> success() {
        RestResult<T> result = new RestResult<>();
        result.setSuccess(true);
        result.setCode(200);
        result.setMessage("操作成功");
        return result;
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> RestResult<T> success(T data) {
        RestResult<T> result = success();
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（带消息和数据）
     */
    public static <T> RestResult<T> success(String message, T data) {
        RestResult<T> result = success();
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败响应
     */
    public static <T> RestResult<T> error() {
        RestResult<T> result = new RestResult<>();
        result.setSuccess(false);
        result.setCode(500);
        result.setMessage("操作失败");
        return result;
    }

    /**
     * 失败响应（带消息）
     */
    public static <T> RestResult<T> error(String message) {
        RestResult<T> result = error();
        result.setMessage(message);
        return result;
    }

    /**
     * 失败响应（带错误码和消息）
     */
    public static <T> RestResult<T> error(Integer code, String message) {
        RestResult<T> result = error();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 失败响应（带错误码、消息和数据）
     */
    public static <T> RestResult<T> error(Integer code, String message, T data) {
        RestResult<T> result = error(code, message);
        result.setData(data);
        return result;
    }

    /**
     * 根据布尔值返回成功或失败
     */
    public static <T> RestResult<T> status(boolean success) {
        return success ? success() : error();
    }
}
