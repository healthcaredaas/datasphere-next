package cn.healthcaredaas.datasphere.svc.security.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 访问审计日志实体
 *
 * @author chenpan
 */
@TableName(value = "ds_access_log")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "访问审计日志")
public class AccessLog extends BaseEntity {

    @Schema(description = "用户ID")
    @TableField("user_id")
    private String userId;

    @Schema(description = "用户名")
    @TableField("user_name")
    private String userName;

    @Schema(description = "操作类型: SELECT/INSERT/UPDATE/DELETE/EXPORT/LOGIN/LOGOUT")
    @TableField("operation_type")
    private String operationType;

    @Schema(description = "操作对象: 表名/资源路径")
    @TableField("operation_target")
    private String operationTarget;

    @Schema(description = "操作描述")
    @TableField("operation_desc")
    private String operationDesc;

    @Schema(description = "执行SQL/请求内容")
    @TableField("request_content")
    private String requestContent;

    @Schema(description = "影响行数")
    @TableField("affected_rows")
    private Long affectedRows;

    @Schema(description = "访问IP")
    @TableField("access_ip")
    private String accessIp;

    @Schema(description = "用户代理")
    @TableField("user_agent")
    private String userAgent;

    @Schema(description = "是否敏感操作: 0-否, 1-是")
    @TableField("is_sensitive")
    private Integer isSensitive;

    @Schema(description = "执行结果: SUCCESS/FAIL")
    @TableField("result")
    private String result;

    @Schema(description = "错误信息")
    @TableField("error_msg")
    private String errorMsg;

    @Schema(description = "执行时长(ms)")
    @TableField("execution_time")
    private Long executionTime;

    @Schema(description = "访问时间")
    @TableField("access_time")
    private LocalDateTime accessTime;
}
