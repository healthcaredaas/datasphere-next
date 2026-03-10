package cn.healthcaredaas.datasphere.svc.agent.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工具定义实体
 *
 * @author chenpan
 */
@TableName(value = "ai_tool_definition")
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "工具定义")
public class ToolDefinition extends BaseEntity {

    @Schema(description = "工具名称")
    @TableField("tool_name")
    private String toolName;

    @Schema(description = "工具类型")
    @TableField("tool_type")
    private String toolType;

    @Schema(description = "工具描述")
    @TableField("description")
    private String description;

    @Schema(description = "输入参数Schema(JSON)")
    @TableField("input_schema")
    private String inputSchema;

    @Schema(description = "输出参数Schema(JSON)")
    @TableField("output_schema")
    private String outputSchema;

    @Schema(description = "执行器类名")
    @TableField("executor_class")
    private String executorClass;

    @Schema(description = "所需权限码")
    @TableField("permission_code")
    private String permissionCode;

    @Schema(description = "状态: 0-禁用, 1-启用")
    @TableField("status")
    private Integer status;
}