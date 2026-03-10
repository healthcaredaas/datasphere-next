package cn.healthcaredaas.datasphere.svc.integration.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Connector配置项定义实体
 *
 * @author chenpan
 */
@TableName(value = "di_connector_option")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Connector配置项定义")
public class ConnectorOption extends BaseEntity {

    @Schema(description = "Connector类型ID")
    @TableField("connector_type_id")
    private String connectorTypeId;

    @Schema(description = "配置项编码")
    @TableField("option_code")
    private String optionCode;

    @Schema(description = "配置项名称")
    @TableField("option_name")
    private String optionName;

    @Schema(description = "数据类型: STRING/INTEGER/BOOLEAN/ARRAY/OBJECT")
    @TableField("data_type")
    private String dataType;

    @Schema(description = "是否必填")
    @TableField("required")
    private Boolean required;

    @Schema(description = "默认值")
    @TableField("default_value")
    private String defaultValue;

    @Schema(description = "配置描述")
    @TableField("description")
    private String description;

    @Schema(description = "排序号")
    @TableField("sort_no")
    private Integer sortNo;

    @Schema(description = "状态: 0-禁用, 1-启用")
    @TableField("status")
    private Integer status;
}
