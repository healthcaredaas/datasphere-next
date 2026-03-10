package cn.healthcaredaas.datasphere.svc.standard.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 数据集实体
 *
 * @author chenpan
 */
@TableName(value = "dn_dataset")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "数据集")
public class Dataset extends BaseEntity {

    @Schema(description = "数据集编码")
    @TableField("dataset_code")
    private String datasetCode;

    @Schema(description = "数据集名称")
    @TableField("dataset_name")
    private String datasetName;

    @Schema(description = "数据集类型: SQL/API")
    @TableField("dataset_type")
    private String datasetType;

    @Schema(description = "数据源ID")
    @TableField("datasource_id")
    private String datasourceId;

    @Schema(description = "SQL语句")
    @TableField("sql_content")
    private String sqlContent;

    @Schema(description = "描述")
    @TableField("description")
    private String description;

    @Schema(description = "状态: 0-草稿, 1-已发布")
    @TableField("status")
    private Integer status;
}
