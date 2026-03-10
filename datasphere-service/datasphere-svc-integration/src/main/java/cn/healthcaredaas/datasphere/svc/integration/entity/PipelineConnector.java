package cn.healthcaredaas.datasphere.svc.integration.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 管道连接器实体
 *
 * @author chenpan
 */
@TableName(value = "di_data_pipeline_connector", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "管道连接器")
public class PipelineConnector extends BaseEntity {

    @Schema(description = "管道ID")
    @TableField("pipeline_id")
    private String pipelineId;

    @Schema(description = "连接器类型: SOURCE/SINK/TRANSFORM")
    @TableField("connector_type")
    private String connectorType;

    @Schema(description = "连接器名称")
    @TableField("connector_name")
    private String connectorName;

    @Schema(description = "插件类型")
    @TableField("plugin_type")
    private String pluginType;

    @Schema(description = "配置信息")
    @TableField(value = "config", typeHandler = JacksonTypeHandler.class)
    private JSONObject config;

    @Schema(description = "排序号")
    @TableField("order_no")
    private Integer orderNo;
}
