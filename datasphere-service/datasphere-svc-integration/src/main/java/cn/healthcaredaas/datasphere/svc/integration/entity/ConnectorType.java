package cn.healthcaredaas.datasphere.svc.integration.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * SeaTunnel Connector类型实体
 *
 * @author chenpan
 */
@TableName(value = "di_connector_type")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "SeaTunnel Connector类型")
public class ConnectorType extends BaseEntity {

    @Schema(description = "Connector编码")
    @TableField("connector_code")
    private String connectorCode;

    @Schema(description = "Connector名称")
    @TableField("connector_name")
    private String connectorName;

    @Schema(description = "Connector类型: SOURCE/SINK/TRANSFORM")
    @TableField("connector_type")
    private String connectorType;

    @Schema(description = "支持的引擎: Zeta/Flink/Spark")
    @TableField("support_engine")
    private String supportEngine;

    @Schema(description = "Driver类名")
    @TableField("driver_class")
    private String driverClass;

    @Schema(description = "描述")
    @TableField("description")
    private String description;

    @Schema(description = "图标")
    @TableField("icon")
    private String icon;

    @Schema(description = "状态: 0-禁用, 1-启用")
    @TableField("status")
    private Integer status;
}
