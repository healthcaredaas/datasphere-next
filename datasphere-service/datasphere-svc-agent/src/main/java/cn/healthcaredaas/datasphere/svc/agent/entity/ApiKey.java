package cn.healthcaredaas.datasphere.svc.agent.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * API密钥实体
 *
 * @author chenpan
 */
@TableName(value = "ai_api_key")
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API密钥")
public class ApiKey extends BaseEntity {

    @Schema(description = "密钥名称")
    @TableField("key_name")
    private String keyName;

    @Schema(description = "API密钥(加密)")
    @TableField("api_key")
    private String apiKey;

    @Schema(description = "所属用户ID")
    @TableField("user_id")
    private String userId;

    @Schema(description = "租户ID")
    @TableField("tenant_id")
    private String tenantId;

    @Schema(description = "授权权限(JSON)")
    @TableField("permissions")
    private String permissions;

    @Schema(description = "速率限制(次/分钟)")
    @TableField("rate_limit")
    private Integer rateLimit;

    @Schema(description = "过期时间")
    @TableField("expired_at")
    private LocalDateTime expiredAt;

    @Schema(description = "最后使用时间")
    @TableField("last_used_at")
    private LocalDateTime lastUsedAt;

    @Schema(description = "状态: 0-禁用, 1-启用")
    @TableField("status")
    private Integer status;
}