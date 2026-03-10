package cn.healthcaredaas.datasphere.svc.integration.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 数据管道实体
 *
 * @author chenpan
 */
@TableName(value = "di_data_pipeline")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "数据管道")
public class DataPipeline extends BaseEntity {

    public DataPipeline() {
        this.setSortBy("createTime");
    }

    @Schema(description = "管道编码")
    @TableField("pipeline_code")
    private String pipelineCode;

    @Schema(description = "管道名称")
    @TableField("pipeline_name")
    private String pipelineName;

    @Schema(description = "项目ID")
    @TableField("project_id")
    private String projectId;

    @Schema(description = "源数据源ID")
    @TableField("source_ds_id")
    private String sourceDsId;

    @Schema(description = "目标数据源ID")
    @TableField("target_ds_id")
    private String targetDsId;

    @Schema(description = "集成引擎类型")
    @TableField("engine_type")
    private String engineType;

    @Schema(description = "cron表达式")
    @TableField("cron_expression")
    private String cronExpression;

    @Schema(description = "描述")
    @TableField("description")
    private String description;

    @Schema(description = "状态: 0-草稿, 1-已发布, 2-运行中, 3-已停止")
    @TableField("status")
    private Integer status;
}
