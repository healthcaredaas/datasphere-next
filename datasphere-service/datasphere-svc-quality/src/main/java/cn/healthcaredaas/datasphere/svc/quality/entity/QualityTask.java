package cn.healthcaredaas.datasphere.svc.quality.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 质量检测任务实体
 *
 * @author chenpan
 */
@TableName(value = "dq_task")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "质量检测任务")
public class QualityTask extends BaseEntity {

    @Schema(description = "任务名称")
    @TableField("task_name")
    private String taskName;

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

    @Schema(description = "状态: 0-运行中, 1-成功, 2-失败")
    @TableField("status")
    private Integer status;
}
