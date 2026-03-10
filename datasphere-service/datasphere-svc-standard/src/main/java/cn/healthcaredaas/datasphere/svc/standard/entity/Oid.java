package cn.healthcaredaas.datasphere.svc.standard.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * OID对象标识符实体
 *
 * @author chenpan
 */
@TableName(value = "dn_oid")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "OID对象标识符")
public class Oid extends BaseEntity {

    @Schema(description = "OID编码")
    @TableField("oid_code")
    private String oidCode;

    @Schema(description = "OID名称")
    @TableField("oid_name")
    private String oidName;

    @Schema(description = "OID值")
    @TableField("oid_value")
    private String oidValue;

    @Schema(description = "父OID")
    @TableField("parent_oid")
    private String parentOid;

    @Schema(description = "OID描述")
    @TableField("description")
    private String description;

    @Schema(description = "标准来源")
    @TableField("source")
    private String source;
}
