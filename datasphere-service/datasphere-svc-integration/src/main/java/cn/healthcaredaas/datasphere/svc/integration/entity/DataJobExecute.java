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
 * 数据作业执行记录实体
 *
 * @author chenpan
 */
@TableName(value = "di_data_job_execute")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "数据作业执行记录")
public class DataJobExecute extends BaseEntity {

    @Schema(description = "作业ID")
    @TableField("job_id")
    private String jobId;

    @Schema(description = "管道ID")
    @TableField("pipeline_id")
    private String pipelineId;

    @Schema(description = "执行ID(引擎返回)")
    @TableField("execute_id")
    private String executeId;

    @Schema(description = "开始时间")
    @TableField("start_time")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @TableField("end_time")
    private LocalDateTime endTime;

    @Schema(description = "读取行数")
    @TableField("read_rows")
    private Long readRows;

    @Schema(description = "写入行数")
    @TableField("write_rows")
    private Long writeRows;

    @Schema(description = "错误行数")
    @TableField("error_rows")
    private Long errorRows;

    @Schema(description = "执行耗时(ms)")
    @TableField("duration")
    private Long duration;

    @Schema(description = "状态: 0-运行中, 1-成功, 2-失败, 3-取消")
    @TableField("status")
    private Integer status;

    @Schema(description = "错误信息")
    @TableField("error_msg")
    private String errorMsg;

    @Schema(description = "执行日志")
    @TableField("execute_log")
    private String executeLog;

    @Schema(description = "是否定时触发: 0-手动, 1-定时")
    @TableField("trigger_type")
    private Integer triggerType;
}
