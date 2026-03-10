package cn.healthcaredaas.datasphere.svc.security.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 敏感字段识别实体
 *
 * @author chenpan
 */
@TableName(value = "ds_sensitive_field")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "敏感字段识别")
public class SensitiveField extends BaseEntity {

    @Schema(description = "数据源ID")
    @TableField("datasource_id")
    private String datasourceId;

    @Schema(description = "数据库名")
    @TableField("database_name")
    private String databaseName;

    @Schema(description = "表名")
    @TableField("table_name")
    private String tableName;

    @Schema(description = "字段名")
    @TableField("column_name")
    private String columnName;

    @Schema(description = "字段类型")
    @TableField("column_type")
    private String columnType;

    @Schema(description = "敏感类型: NAME/PHONE/EMAIL/ID_CARD/BANK_CARD/ADDRESS/MEDICAL_RECORD")
    @TableField("sensitive_type")
    private String sensitiveType;

    @Schema(description = "敏感级别: HIGH/MEDIUM/LOW")
    @TableField("sensitive_level")
    private String sensitiveLevel;

    @Schema(description = "识别方式: AUTO/MANUAL")
    @TableField("identify_type")
    private String identifyType;

    @Schema(description = "识别规则ID")
    @TableField("rule_id")
    private String ruleId;

    @Schema(description = "样本数据")
    @TableField("sample_data")
    private String sampleData;

    @Schema(description = "匹配度(%)")
    @TableField("match_rate")
    private Integer matchRate;

    @Schema(description = "最后识别时间")
    @TableField("last_identify_time")
    private LocalDateTime lastIdentifyTime;

    @Schema(description = "状态: 0-未处理, 1-已确认, 2-已忽略")
    @TableField("status")
    private Integer status;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;
}
