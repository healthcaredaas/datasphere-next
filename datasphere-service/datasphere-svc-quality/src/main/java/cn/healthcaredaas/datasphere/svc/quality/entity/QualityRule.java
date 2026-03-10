package cn.healthcaredaas.datasphere.svc.quality.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 质量规则实体
 *
 * @author chenpan
 */
@TableName(value = "dq_rule")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "质量规则")
public class QualityRule extends BaseEntity {

    @Schema(description = "规则编码")
    @TableField("rule_code")
    private String ruleCode;

    @Schema(description = "规则名称")
    @TableField("rule_name")
    private String ruleName;

    @Schema(description = "规则类型: COMPLETENESS/UNIQUENESS/FORMAT/VALUE_RANGE/CONSISTENCY/ACCURACY/CUSTOM")
    @TableField("rule_type")
    private String ruleType;

    @Schema(description = "数据源ID")
    @TableField("datasource_id")
    private String datasourceId;

    @Schema(description = "表名")
    @TableField("table_name")
    private String tableName;

    @Schema(description = "字段名")
    @TableField("column_name")
    private String columnName;

    @Schema(description = "规则表达式/SQL")
    @TableField("rule_expression")
    private String ruleExpression;

    @Schema(description = "错误提示")
    @TableField("error_message")
    private String errorMessage;

    @Schema(description = "规则描述")
    @TableField("description")
    private String description;

    @Schema(description = "状态: 0-禁用, 1-启用")
    @TableField("status")
    private Integer status;
}
