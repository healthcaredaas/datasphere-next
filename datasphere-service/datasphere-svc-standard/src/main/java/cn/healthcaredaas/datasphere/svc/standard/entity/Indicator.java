package cn.healthcaredaas.datasphere.svc.standard.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 指标实体
 *
 * @author chenpan
 */
@TableName(value = "dn_indicator")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "指标")
public class Indicator extends BaseEntity {

    @Schema(description = "指标编码")
    @TableField("indicator_code")
    private String indicatorCode;

    @Schema(description = "指标名称")
    @TableField("indicator_name")
    private String indicatorName;

    @Schema(description = "指标定义")
    @TableField("definition")
    private String definition;

    @Schema(description = "计算口径")
    @TableField("caliber")
    private String caliber;

    @Schema(description = "统计周期")
    @TableField("stat_period")
    private String statPeriod;

    @Schema(description = "数据源ID")
    @TableField("datasource_id")
    private String datasourceId;

    @Schema(description = "指标SQL")
    @TableField("indicator_sql")
    private String indicatorSql;

    @Schema(description = "单位")
    @TableField("unit")
    private String unit;

    @Schema(description = "状态: 0-草稿, 1-已发布")
    @TableField("status")
    private Integer status;
}
