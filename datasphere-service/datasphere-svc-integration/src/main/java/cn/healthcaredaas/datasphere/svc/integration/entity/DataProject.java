package cn.healthcaredaas.datasphere.svc.integration.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 数据项目实体
 *
 * @author chenpan
 */
@TableName(value = "di_data_project")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "数据项目")
public class DataProject extends BaseEntity {

    public DataProject() {
    }

    /**
     * 项目编码
     */
    @Schema(description = "项目编码")
    @TableField("project_code")
    private String projectCode;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    @TableField("project_name")
    private String projectName;

    /**
     * 项目描述
     */
    @Schema(description = "项目描述")
    @TableField("description")
    private String description;

    /**
     * 项目状态
     */
    @Schema(description = "项目状态: 0-草稿, 1-已发布, 2-已归档")
    @TableField("status")
    private Integer status;

    /**
     * 负责人
     */
    @Schema(description = "负责人")
    @TableField("owner")
    private String owner;
}
