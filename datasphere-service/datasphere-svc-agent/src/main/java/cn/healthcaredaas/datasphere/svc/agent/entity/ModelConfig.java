package cn.healthcaredaas.datasphere.svc.agent.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模型配置实体
 *
 * @author chenpan
 */
@TableName(value = "ai_model_config")
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "模型配置")
public class ModelConfig extends BaseEntity {

    @Schema(description = "模型名称")
    @TableField("model_name")
    private String modelName;

    @Schema(description = "模型类型: CLAUDE/GPT/QWEN/LLAMA/LOCAL")
    @TableField("model_type")
    private String modelType;

    @Schema(description = "API端点")
    @TableField("api_endpoint")
    private String apiEndpoint;

    @Schema(description = "API密钥(加密存储)")
    @TableField("api_key")
    private String apiKey;

    @Schema(description = "模型参数配置(JSON)")
    @TableField("model_params")
    private String modelParams;

    @Schema(description = "支持的能力: CHAT/SQL/CODE/EMBEDDING")
    @TableField("capabilities")
    private String capabilities;

    @Schema(description = "优先级(用于模型路由)")
    @TableField("priority")
    private Integer priority;

    @Schema(description = "状态: 0-禁用, 1-启用")
    @TableField("status")
    private Integer status;

    @Schema(description = "租户ID")
    @TableField("tenant_id")
    private String tenantId;
}