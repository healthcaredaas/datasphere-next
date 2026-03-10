package cn.healthcaredaas.datasphere.svc.metadata.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 元数据项实体
 *
 * @author chenpan
 */
@TableName(value = "meta_item")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "元数据项")
public class MetaItem extends BaseEntity {

    @Schema(description = "元数据编码")
    @TableField("item_code")
    private String itemCode;

    @Schema(description = "元数据名称")
    @TableField("item_name")
    private String itemName;

    @Schema(description = "模型ID")
    @TableField("model_id")
    private String modelId;

    @Schema(description = "数据源ID")
    @TableField("datasource_id")
    private String datasourceId;

    @Schema(description = "数据库名")
    @TableField("database_name")
    private String databaseName;

    @Schema(description = "表名")
    @TableField("table_name")
    private String tableName;

    @Schema(description = "列名")
    @TableField("column_name")
    private String columnName;

    @Schema(description = "数据类型")
    @TableField("data_type")
    private String dataType;

    @Schema(description = "描述")
    @TableField("description")
    private String description;
}
