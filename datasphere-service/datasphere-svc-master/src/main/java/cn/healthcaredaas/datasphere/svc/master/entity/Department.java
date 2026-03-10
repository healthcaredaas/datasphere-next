package cn.healthcaredaas.datasphere.svc.master.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 科室实体
 *
 * @author chenpan
 */
@TableName(value = "md_department")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "科室")
public class Department extends BaseEntity {

    @Schema(description = "科室编码")
    @TableField("dept_code")
    private String deptCode;

    @Schema(description = "科室名称")
    @TableField("dept_name")
    private String deptName;

    @Schema(description = "上级科室ID")
    @TableField("parent_id")
    private String parentId;

    @Schema(description = "所属机构ID")
    @TableField("org_id")
    private String orgId;

    @Schema(description = "科室类型")
    @TableField("dept_type")
    private String deptType;

    @Schema(description = "科室分类")
    @TableField("category")
    private String category;

    @Schema(description = "负责人ID")
    @TableField("leader_id")
    private String leaderId;

    @Schema(description = "排序号")
    @TableField("sort_no")
    private Integer sortNo;
}
