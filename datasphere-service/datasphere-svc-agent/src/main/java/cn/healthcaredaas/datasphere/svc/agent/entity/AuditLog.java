package cn.healthcaredaas.datasphere.svc.agent.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Agent审计日志实体
 *
 * @author chenpan
 */
@TableName(value = "ai_agent_audit_log")
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Agent审计日志")
public class AuditLog extends BaseEntity {

    @Schema(description = "会话ID")
    @TableField("session_id")
    private String sessionId;

    @Schema(description = "消息ID")
    @TableField("message_id")
    private String messageId;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private String userId;

    @Schema(description = "租户ID")
    @TableField("tenant_id")
    private String tenantId;

    @Schema(description = "操作类型")
    @TableField("operation_type")
    private String operationType;

    @Schema(description = "操作描述")
    @TableField("operation_desc")
    private String operationDesc;

    @Schema(description = "请求内容")
    @TableField("request_content")
    private String requestContent;

    @Schema(description = "响应内容")
    @TableField("response_content")
    private String responseContent;

    @Schema(description = "使用的工具(JSON)")
    @TableField("tools_used")
    private String toolsUsed;

    @Schema(description = "执行时长(ms)")
    @TableField("execution_time")
    private Long executionTime;

    @Schema(description = "执行状态: SUCCESS/FAIL")
    @TableField("status")
    private String status;

    @Schema(description = "错误信息")
    @TableField("error_msg")
    private String errorMsg;

    @Schema(description = "访问IP")
    @TableField("access_ip")
    private String accessIp;

    @Schema(description = "访问时间")
    @TableField("access_time")
    private LocalDateTime accessTime;
}