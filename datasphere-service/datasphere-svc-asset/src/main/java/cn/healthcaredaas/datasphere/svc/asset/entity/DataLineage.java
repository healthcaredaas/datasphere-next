package cn.healthcaredaas.datasphere.svc.asset.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 数据血缘实体
 *
 * @author chenpan
 */
@TableName(value = "da_lineage")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "数据血缘")
public class DataLineage extends BaseEntity {

    @Schema(description = "资产ID")
    @TableField("asset_id")
    private String assetId;

    @Schema(description = "资产类型: TABLE/FIELD")
    @TableField("asset_type")
    private String assetType;

    @Schema(description = "资产名称")
    @TableField("asset_name")
    private String assetName;

    @Schema(description = "上游资产ID")
    @TableField("upstream_asset_id")
    private String upstreamAssetId;

    @Schema(description = "上游资产名称")
    @TableField("upstream_asset_name")
    private String upstreamAssetName;

    @Schema(description = "下游资产ID")
    @TableField("downstream_asset_id")
    private String downstreamAssetId;

    @Schema(description = "下游资产名称")
    @TableField("downstream_asset_name")
    private String downstreamAssetName;

    @Schema(description = "血缘关系类型: LINEAGE/DEPENDENCY/IMPACT")
    @TableField("relation_type")
    private String relationType;

    @Schema(description = "关系描述")
    @TableField("relation_desc")
    private String relationDesc;

    @Schema(description = "转换逻辑/SQL")
    @TableField("transform_logic")
    private String transformLogic;

    @Schema(description = "最后解析时间")
    @TableField("last_parse_time")
    private LocalDateTime lastParseTime;
}
