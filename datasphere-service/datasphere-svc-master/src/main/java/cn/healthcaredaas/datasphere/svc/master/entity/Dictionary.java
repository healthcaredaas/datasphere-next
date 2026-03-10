package cn.healthcaredaas.datasphere.svc.master.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 标准字典实体
 *
 * @author chenpan
 */
@TableName(value = "md_dictionary")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "标准字典")
public class Dictionary extends BaseEntity {

    @Schema(description = "字典编码")
    @TableField("dict_code")
    private String dictCode;

    @Schema(description = "字典名称")
    @TableField("dict_name")
    private String dictName;

    @Schema(description = "字典类型")
    @TableField("dict_type")
    private String dictType;

    @Schema(description = "描述")
    @TableField("description")
    private String description;

    @Schema(description = "状态: 0-禁用, 1-启用")
    @TableField("status")
    private Integer status;
}
