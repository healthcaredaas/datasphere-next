package cn.healthcaredaas.datasphere.svc.integration.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 数据作业实体
 *
 * @author chenpan
 */
@TableName(value = "di_data_job")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "数据作业")
public class DataJob extends BaseEntity {

    @Schema(description = "作业编码")
    @TableField("job_code")
    private String jobCode;

    @Schema(description = "作业名称")
    @TableField("job_name")
    private String jobName;

    @Schema(description = "管道ID")
    @TableField("pipeline_id")
    private String pipelineId;

    @Schema(description = "引擎类型: SeaTunnel/DataConnect")
    @TableField("engine_type")
    private String engineType;

    @Schema(description = "执行模式: BATCH/STREAMING")
    @TableField("execute_mode")
    private String executeMode;

    @Schema(description = "Cron表达式(定时调度)")
    @TableField("cron_expression")
    private String cronExpression;

    @Schema(description = "是否定时调度: 0-否, 1-是")
    @TableField("is_schedule")
    private Integer isSchedule;

    @Schema(description = "SeaTunnel配置文件内容")
    @TableField("config_content")
    private String configContent;

    @Schema(description = "运行参数(JSON)")
    @TableField("runtime_params")
    private String runtimeParams;

    @Schema(description = "状态: 0-草稿, 1-已发布, 2-运行中, 3-已停止")
    @TableField("status")
    private Integer status;

    @Schema(description = "最后运行时间")
    @TableField("last_run_time")
    private LocalDateTime lastRunTime;

    @Schema(description = "最后运行状态: 0-失败, 1-成功")
    @TableField("last_run_status")
    private Integer lastRunStatus;

    @Schema(description = "描述")
    @TableField("description")
    private String description;
}
