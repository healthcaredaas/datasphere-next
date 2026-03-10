package cn.healthcaredaas.datasphere.svc.asset.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 资产使用记录实体
 *
 * @author chenpan
 */
@TableName(value = "da_asset_usage")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "资产使用记录")
public class AssetUsage extends BaseEntity {

    @Schema(description = "资产ID")
    @TableField("asset_id")
    private String assetId;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private String userId;

    @Schema(description = "用户名")
    @TableField("user_name")
    private String userName;

    @Schema(description = "操作类型: VIEW/DOWNLOAD/API_CALL")
    @TableField("operation_type")
    private String operationType;

    @Schema(description = "操作描述")
    @TableField("operation_desc")
    private String operationDesc;

    @Schema(description = "访问IP")
    @TableField("access_ip")
    private String accessIp;

    @Schema(description = "请求参数")
    @TableField("request_params")
    private String requestParams;

    @Schema(description = "响应结果")
    @TableField("response_result")
    private String responseResult;

    @Schema(description = "执行时长(ms)")
    @TableField("execution_time")
    private Long executionTime;

    @Schema(description = "访问时间")
    @TableField("access_time")
    private LocalDateTime accessTime;
}
