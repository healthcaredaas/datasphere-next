package cn.healthcaredaas.datasphere.svc.agent.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Token用量统计实体
 *
 * @author chenpan
 */
@TableName(value = "ai_token_usage")
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Token用量统计")
public class TokenUsage extends BaseEntity {

    @Schema(description = "会话ID")
    @TableField("session_id")
    private String sessionId;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private String userId;

    @Schema(description = "租户ID")
    @TableField("tenant_id")
    private String tenantId;

    @Schema(description = "模型ID")
    @TableField("model_id")
    private String modelId;

    @Schema(description = "输入Token数")
    @TableField("input_tokens")
    private Integer inputTokens;

    @Schema(description = "输出Token数")
    @TableField("output_tokens")
    private Integer outputTokens;

    @Schema(description = "总Token数")
    @TableField("total_tokens")
    private Integer totalTokens;

    @Schema(description = "费用金额")
    @TableField("cost_amount")
    private BigDecimal costAmount;

    @Schema(description = "使用日期")
    @TableField("usage_date")
    private LocalDate usageDate;
}