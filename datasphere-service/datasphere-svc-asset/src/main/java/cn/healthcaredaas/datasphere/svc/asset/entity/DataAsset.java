package cn.healthcaredaas.datasphere.svc.asset.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 数据资产实体
 *
 * @author chenpan
 */
@TableName(value = "da_asset")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "数据资产")
public class DataAsset extends BaseEntity {

    @Schema(description = "资产编码")
    @TableField("asset_code")
    private String assetCode;

    @Schema(description = "资产名称")
    @TableField("asset_name")
    private String assetName;

    @Schema(description = "资产类型: TABLE/API/REPORT")
    @TableField("asset_type")
    private String assetType;

    @Schema(description = "数据源ID")
    @TableField("datasource_id")
    private String datasourceId;

    @Schema(description = "数据库名")
    @TableField("database_name")
    private String databaseName;

    @Schema(description = "表名")
    @TableField("table_name")
    private String tableName;

    @Schema(description = "资产描述")
    @TableField("description")
    private String description;

    @Schema(description = "负责人")
    @TableField("owner")
    private String owner;

    @Schema(description = "访问次数")
    @TableField("access_count")
    private Long accessCount;

    @Schema(description = "状态: 0-草稿, 1-已发布, 2-已归档")
    @TableField("status")
    private Integer status;
}
