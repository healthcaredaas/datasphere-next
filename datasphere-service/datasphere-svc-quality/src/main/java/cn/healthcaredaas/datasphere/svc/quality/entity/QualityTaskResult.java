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
 * 质量任务执行结果实体
 *
 * @author chenpan
 */
@TableName(value = "dq_task_result")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "质量任务执行结果")
public class QualityTaskResult extends BaseEntity {

    @Schema(description = "任务ID")
    @TableField("task_id")
    private String taskId;

    @Schema(description = "规则ID")
    @TableField("rule_id")
    private String ruleId;

    @Schema(description = "开始时间")
    @TableField("start_time")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @TableField("end_time")
    private LocalDateTime endTime;

    @Schema(description = "检测总行数")
    @TableField("total_rows")
    private Long totalRows;

    @Schema(description = "错误行数")
    @TableField("error_rows")
    private Long errorRows;

    @Schema(description = "正确行数")
    @TableField("success_rows")
    private Long successRows;

    @Schema(description = "错误率")
    @TableField("error_rate")
    private BigDecimal errorRate;

    @Schema(description = "质量得分")
    @TableField("quality_score")
    private BigDecimal qualityScore;

    @Schema(description = "执行状态: 0-运行中, 1-成功, 2-失败")
    @TableField("status")
    private Integer status;

    @Schema(description = "错误信息")
    @TableField("error_message")
    private String errorMessage;

    @Schema(description = "执行耗时(ms)")
    @TableField("duration")
    private Long duration;
}
