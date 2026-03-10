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
 * 数据作业日志实体
 *
 * @author chenpan
 */
@TableName(value = "di_data_job_log")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "数据作业日志")
public class DataJobLog extends BaseEntity {

    public DataJobLog() {
        this.setSortBy("createTime");
    }

    @Schema(description = "作业ID")
    @TableField("job_id")
    private String jobId;

    @Schema(description = "管道ID")
    @TableField("pipeline_id")
    private String pipelineId;

    @Schema(description = "管道名称")
    @TableField("pipeline_name")
    private String pipelineName;

    @Schema(description = "引擎类型")
    @TableField("engine_type")
    private String engineType;

    @Schema(description = "启动时间")
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

    @Schema(description = "状态: 0-运行中, 1-成功, 2-失败")
    @TableField("status")
    private Integer status;

    @Schema(description = "错误信息")
    @TableField("error_msg")
    private String errorMsg;
}
