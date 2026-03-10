package cn.healthcaredaas.datasphere.svc.security.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 脱敏规则实体
 *
 * @author chenpan
 */
@TableName(value = "ds_mask_rule")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "脱敏规则")
public class MaskRule extends BaseEntity {

    @Schema(description = "规则名称")
    @TableField("rule_name")
    private String ruleName;

    @Schema(description = "数据源ID")
    @TableField("datasource_id")
    private String datasourceId;

    @Schema(description = "表名")
    @TableField("table_name")
    private String tableName;

    @Schema(description = "字段名")
    @TableField("column_name")
    private String columnName;

    @Schema(description = "脱敏算法: MASK_ALL/MASK_PARTIAL/HASH/REPLACE/RANDOM/NULLIFY")
    @TableField("algorithm")
    private String algorithm;

    @Schema(description = "算法参数(JSON)")
    @TableField("algorithm_params")
    private String algorithmParams;

    @Schema(description = "规则描述")
    @TableField("description")
    private String description;

    @Schema(description = "状态: 0-禁用, 1-启用")
    @TableField("status")
    private Integer status;
}
