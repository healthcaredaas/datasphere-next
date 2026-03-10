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
 * 质量问题记录实体
 *
 * @author chenpan
 */
@TableName(value = "dq_issue")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "质量问题记录")
public class QualityIssue extends BaseEntity {

    @Schema(description = "任务ID")
    @TableField("task_id")
    private String taskId;

    @Schema(description = "规则ID")
    @TableField("rule_id")
    private String ruleId;

    @Schema(description = "数据源ID")
    @TableField("datasource_id")
    private String datasourceId;

    @Schema(description = "表名")
    @TableField("table_name")
    private String tableName;

    @Schema(description = "字段名")
    @TableField("column_name")
    private String columnName;

    @Schema(description = "规则类型")
    @TableField("rule_type")
    private String ruleType;

    @Schema(description = "主键值(JSON格式，用于定位错误数据)")
    @TableField("primary_key_value")
    private String primaryKeyValue;

    @Schema(description = "错误数据值")
    @TableField("error_value")
    private String errorValue;

    @Schema(description = "期望的值")
    @TableField("expected_value")
    private String expectedValue;

    @Schema(description = "错误描述")
    @TableField("error_message")
    private String errorMessage;

    @Schema(description = "处理状态: 0-未处理, 1-已忽略, 2-已修复")
    @TableField("handle_status")
    private Integer handleStatus;

    @Schema(description = "处理人")
    @TableField("handler")
    private String handler;

    @Schema(description = "处理时间")
    @TableField("handle_time")
    private LocalDateTime handleTime;

    @Schema(description = "处理备注")
    @TableField("handle_remark")
    private String handleRemark;
}
