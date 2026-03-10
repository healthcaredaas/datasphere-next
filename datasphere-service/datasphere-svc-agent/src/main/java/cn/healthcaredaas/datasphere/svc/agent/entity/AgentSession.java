package cn.healthcaredaas.datasphere.svc.agent.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Agent会话实体
 *
 * @author chenpan
 */
@TableName(value = "ai_agent_session")
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Agent会话")
public class AgentSession extends BaseEntity {

    @Schema(description = "会话标题")
    @TableField("title")
    private String title;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private String userId;

    @Schema(description = "租户ID")
    @TableField("tenant_id")
    private String tenantId;

    @Schema(description = "会话状态: ACTIVE/ARCHIVED/DELETED")
    @TableField("status")
    private String status;

    @Schema(description = "使用的模型ID")
    @TableField("model_id")
    private String modelId;

    @Schema(description = "会话上下文(JSON)")
    @TableField("context")
    private String context;

    @Schema(description = "消息数量")
    @TableField("message_count")
    private Integer messageCount;

    @Schema(description = "最后活跃时间")
    @TableField("last_active_time")
    private LocalDateTime lastActiveTime;
}