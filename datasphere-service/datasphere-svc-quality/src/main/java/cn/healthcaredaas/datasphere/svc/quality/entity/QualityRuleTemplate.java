package cn.healthcaredaas.datasphere.svc.quality.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 质量规则模板实体
 *
 * @author chenpan
 */
@TableName(value = "dq_rule_template")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "质量规则模板")
public class QualityRuleTemplate extends BaseEntity {

    @Schema(description = "模板编码")
    @TableField("template_code")
    private String templateCode;

    @Schema(description = "模板名称")
    @TableField("template_name")
    private String templateName;

    @Schema(description = "规则类型: COMPLETENESS/UNIQUENESS/FORMAT/VALUE_RANGE/CONSISTENCY/ACCURACY/CUSTOM")
    @TableField("rule_type")
    private String ruleType;

    @Schema(description = "数据库类型: MySQL/Oracle/PostgreSQL/SQLServer/DM/通用")
    @TableField("db_type")
    private String dbType;

    @Schema(description = "规则表达式模板")
    @TableField("expression_template")
    private String expressionTemplate;

    @Schema(description = "参数定义(JSON): [{name, type, required, description}]")
    @TableField("param_definition")
    private String paramDefinition;

    @Schema(description = "错误提示模板")
    @TableField("error_message_template")
    private String errorMessageTemplate;

    @Schema(description = "描述")
    @TableField("description")
    private String description;

    @Schema(description = "排序号")
    @TableField("sort_no")
    private Integer sortNo;

    @Schema(description = "状态: 0-禁用, 1-启用")
    @TableField("status")
    private Integer status;
}
