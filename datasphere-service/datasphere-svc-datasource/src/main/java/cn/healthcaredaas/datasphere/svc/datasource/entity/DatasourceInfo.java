package cn.healthcaredaas.datasphere.svc.datasource.entity;

import cn.healthcaredaas.datasphere.core.annotation.EnableSelectOption;
import cn.healthcaredaas.datasphere.core.annotation.SelectInColumn;
import cn.healthcaredaas.datasphere.core.annotation.SelectLikeColumn;
import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import cn.healthcaredaas.datasphere.core.json.PasswordObjectDeserializer;
import cn.healthcaredaas.datasphere.core.json.PasswordObjectSerializer;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 数据源信息实体
 *
 * @author chenpan
 */
@TableName(value = "datasource_info", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "数据源信息")
@EnableSelectOption
public class DatasourceInfo extends BaseEntity {

    @Schema(description = "数据源名称")
    @TableField(value = "ds_name")
    @SelectLikeColumn(wildcardPosition = SelectLikeColumn.WildcardPosition.BOTH)
    private String dsName;

    @Schema(description = "描述信息")
    @TableField(value = "note")
    private String note;

    @NotNull(message = "数据源类型不能为空")
    @Schema(description = "数据源类型")
    @TableField(value = "ds_type")
    private String dsType;

    @Schema(description = "数据源类型列表（查询用）")
    @SelectInColumn(column = "dsType")
    @TableField(exist = false)
    private List<String> dsTypes;

    @NotNull(message = "数据源配置信息不能为空")
    @Schema(description = "数据源配置")
    @TableField(value = "ds_config", typeHandler = JacksonTypeHandler.class)
    @JsonSerialize(using = PasswordObjectSerializer.class)
    @JsonDeserialize(using = PasswordObjectDeserializer.class)
    private JSONObject dsConfig;

    @Schema(description = "环境配置")
    @TableField(value = "env_profile")
    private String envProfile;
}
