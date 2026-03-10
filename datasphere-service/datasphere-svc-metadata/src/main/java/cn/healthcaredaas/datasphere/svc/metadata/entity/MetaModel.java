package cn.healthcaredaas.datasphere.svc.metadata.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 元模型实体
 *
 * @author chenpan
 */
@TableName(value = "meta_model")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "元模型")
public class MetaModel extends BaseEntity {

    @Schema(description = "模型编码")
    @TableField("model_code")
    private String modelCode;

    @Schema(description = "模型名称")
    @TableField("model_name")
    private String modelName;

    @Schema(description = "模型类型: TABLE/COLUMN/OTHER")
    @TableField("model_type")
    private String modelType;

    @Schema(description = "父模型ID")
    @TableField("parent_id")
    private String parentId;

    @Schema(description = "描述")
    @TableField("description")
    private String description;
}
