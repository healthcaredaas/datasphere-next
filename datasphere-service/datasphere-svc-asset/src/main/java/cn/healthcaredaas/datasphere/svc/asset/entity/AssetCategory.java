package cn.healthcaredaas.datasphere.svc.asset.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 资产分类实体
 *
 * @author chenpan
 */
@TableName(value = "da_asset_category")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "资产分类")
public class AssetCategory extends BaseEntity {

    @Schema(description = "分类编码")
    @TableField("category_code")
    private String categoryCode;

    @Schema(description = "分类名称")
    @TableField("category_name")
    private String categoryName;

    @Schema(description = "父分类ID")
    @TableField("parent_id")
    private String parentId;

    @Schema(description = "层级路径")
    @TableField("path")
    private String path;

    @Schema(description = "层级深度")
    @TableField("level")
    private Integer level;

    @Schema(description = "排序号")
    @TableField("sort_no")
    private Integer sortNo;

    @Schema(description = "分类描述")
    @TableField("description")
    private String description;

    @Schema(description = "状态: 0-禁用, 1-启用")
    @TableField("status")
    private Integer status;
}
