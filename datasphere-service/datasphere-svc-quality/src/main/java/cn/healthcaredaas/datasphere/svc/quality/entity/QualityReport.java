package cn.healthcaredaas.datasphere.svc.quality.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 质量报告实体
 *
 * @author chenpan
 */
@TableName(value = "dq_report")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "质量报告")
public class QualityReport extends BaseEntity {

    @Schema(description = "任务ID")
    @TableField("task_id")
    private String taskId;

    @Schema(description = "规则ID")
    @TableField("rule_id")
    private String ruleId;

    @Schema(description = "报告名称")
    @TableField("report_name")
    private String reportName;

    @Schema(description = "报告日期")
    @TableField("report_date")
    private LocalDateTime reportDate;

    @Schema(description = "检测总行数")
    @TableField("total_rows")
    private Long totalRows;

    @Schema(description = "错误行数")
    @TableField("error_rows")
    private Long errorRows;

    @Schema(description = "质量评分")
    @TableField("score")
    private BigDecimal score;

    @Schema(description = "质量等级: A/B/C/D/F")
    @TableField("grade")
    private String grade;

    @Schema(description = "状态: 0-失败, 1-成功")
    @TableField("status")
    private Integer status;

    @Schema(description = "报告内容")
    @TableField("report_content")
    private String reportContent;
}
