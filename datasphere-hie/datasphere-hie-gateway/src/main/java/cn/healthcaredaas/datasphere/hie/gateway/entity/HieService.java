package cn.healthcaredaas.datasphere.hie.gateway.entity;

import cn.healthcaredaas.data.cloud.data.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 交互服务实体
 *
 * @author chenpan
 */
@TableName(value = "hie_service")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "交互服务")
public class HieService extends BaseEntity {

    @Schema(description = "服务编码")
    @TableField("service_code")
    private String serviceCode;

    @Schema(description = "服务名称")
    @TableField("service_name")
    private String serviceName;

    @Schema(description = "服务类型: MQ/HTTP/SOAP/DS/EX")
    @TableField("service_type")
    private String serviceType;

    @Schema(description = "服务端点")
    @TableField("endpoint")
    private String endpoint;

    @Schema(description = "消息格式: HL7/XML/JSON")
    @TableField("message_format")
    private String messageFormat;

    @Schema(description = "描述")
    @TableField("description")
    private String description;

    @Schema(description = "状态: 0-禁用, 1-启用")
    @TableField("status")
    private Integer status;
}
