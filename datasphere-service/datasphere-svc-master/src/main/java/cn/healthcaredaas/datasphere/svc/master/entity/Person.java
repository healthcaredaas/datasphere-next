package cn.healthcaredaas.datasphere.svc.master.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

/**
 * 人员信息实体
 *
 * @author chenpan
 */
@TableName(value = "md_person")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "人员信息")
public class Person extends BaseEntity {

    @Schema(description = "人员编码")
    @TableField("person_code")
    private String personCode;

    @Schema(description = "姓名")
    @TableField("name")
    private String name;

    @Schema(description = "性别")
    @TableField("gender")
    private String gender;

    @Schema(description = "出生日期")
    @TableField("birth_date")
    private LocalDate birthDate;

    @Schema(description = "身份证号")
    @TableField("id_card")
    private String idCard;

    @Schema(description = "手机号")
    @TableField("mobile")
    private String mobile;

    @Schema(description = "邮箱")
    @TableField("email")
    private String email;

    @Schema(description = "机构ID")
    @TableField("org_id")
    private String orgId;

    @Schema(description = "部门ID")
    @TableField("dept_id")
    private String deptId;

    @Schema(description = "职务")
    @TableField("position")
    private String position;

    @Schema(description = "职称")
    @TableField("job_title")
    private String jobTitle;
}
