package cn.healthcaredaas.datasphere.svc.master.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 组织机构实体
 *
 * @author chenpan
 */
@TableName(value = "md_organization")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "组织机构")
public class Organization extends BaseEntity {

    @Schema(description = "机构编码")
    @TableField("org_code")
    private String orgCode;

    @Schema(description = "机构名称")
    @TableField("org_name")
    private String orgName;

    @Schema(description = "上级机构ID")
    @TableField("parent_id")
    private String parentId;

    @Schema(description = "机构层级")
    @TableField("org_level")
    private Integer orgLevel;

    @Schema(description = "机构类型")
    @TableField("org_type")
    private String orgType;

    @Schema(description = "机构地址")
    @TableField("address")
    private String address;

    @Schema(description = "联系电话")
    @TableField("phone")
    private String phone;

    @Schema(description = "排序号")
    @TableField("sort_no")
    private Integer sortNo;
}
