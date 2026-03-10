package cn.healthcaredaas.datasphere.svc.asset.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 资产标签实体
 *
 * @author chenpan
 */
@TableName(value = "da_asset_tag")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "资产标签")
public class AssetTag extends BaseEntity {

    @Schema(description = "标签名称")
    @TableField("tag_name")
    private String tagName;

    @Schema(description = "标签颜色")
    @TableField("tag_color")
    private String tagColor;

    @Schema(description = "标签描述")
    @TableField("description")
    private String description;

    @Schema(description = "使用次数")
    @TableField("usage_count")
    private Integer usageCount;

    @Schema(description = "状态: 0-禁用, 1-启用")
    @TableField("status")
    private Integer status;
}
