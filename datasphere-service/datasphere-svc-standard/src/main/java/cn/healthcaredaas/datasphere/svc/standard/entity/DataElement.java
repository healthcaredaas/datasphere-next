package cn.healthcaredaas.datasphere.svc.standard.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 数据元实体
 *
 * @author chenpan
 */
@TableName(value = "dn_data_element")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "数据元")
public class DataElement extends BaseEntity {

    @Schema(description = "数据元编码")
    @TableField("element_code")
    private String elementCode;

    @Schema(description = "数据元名称")
    @TableField("element_name")
    private String elementName;

    @Schema(description = "数据元定义")
    @TableField("definition")
    private String definition;

    @Schema(description = "数据类型")
    @TableField("data_type")
    private String dataType;

    @Schema(description = "数据长度")
    @TableField("data_length")
    private Integer dataLength;

    @Schema(description = "允许值")
    @TableField("allowable_values")
    private String allowableValues;

    @Schema(description = "标准来源")
    @TableField("source")
    private String source;
}
