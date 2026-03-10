package cn.healthcaredaas.datasphere.svc.datasource.entity;

import cn.healthcaredaas.datasphere.core.enums.EnableStatusEnum;
import cn.healthcaredaas.datasphere.core.annotation.EnableSelectOption;
import cn.healthcaredaas.datasphere.core.annotation.LogicUnique;
import cn.healthcaredaas.datasphere.core.annotation.SelectLikeColumn;
import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 数据源类型实体
 *
 * @author chenpan
 */
@TableName(value = "datasource_type")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "数据源类型")
@EnableSelectOption
@LogicUnique(columns = {"dataType"}, message = "类型【{dataType}】已存在!")
public class DatasourceType extends BaseEntity {

    public DatasourceType() {
    }

    /**
     * 数据源类型 如Mysql, Oracle, Hive
     */
    @Schema(description = "数据源类型编码")
    @TableField("data_type")
    @SelectLikeColumn(wildcardPosition = SelectLikeColumn.WildcardPosition.BOTH)
    private String dataType;

    /**
     * 数据源类型名称
     */
    @Schema(description = "数据源类型名称")
    @TableField("data_type_name")
    @SelectLikeColumn(wildcardPosition = SelectLikeColumn.WildcardPosition.BOTH)
    private String dataTypeName;

    /**
     * 数据源分类编码
     */
    @Schema(description = "数据源分类编码")
    @TableField("classify_code")
    private String classifyCode;

    @Schema(description = "数据源驱动")
    @TableField("driver")
    private String driver;

    @Schema(description = "数据源URL模板")
    @TableField("url")
    private String url;

    /**
     * 数据源分类主键id
     */
    @Schema(description = "数据源分类主键id")
    @TableField("classify_id")
    private String classifyId;

    /**
     * 数据源logo图片
     */
    @Schema(description = "数据源logo图片")
    @TableField("img")
    private String img;

    /**
     * 序号
     */
    @Schema(description = "序号")
    @TableField("order_no")
    private Integer orderNo;

    @Schema(description = "启用状态")
    @TableField("status")
    private EnableStatusEnum status;
}
