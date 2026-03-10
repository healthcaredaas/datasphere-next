package cn.healthcaredaas.datasphere.svc.datasource.entity;

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
 * 数据源分类实体
 *
 * @author chenpan
 */
@TableName(value = "datasource_classify")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "数据源分类")
@EnableSelectOption
@LogicUnique(columns = {"classifyCode"}, message = "分类【{classifyCode}】已存在!")
public class DatasourceClassify extends BaseEntity {

    public DatasourceClassify() {
    }

    /**
     * 类型编码
     */
    @Schema(description = "类型编码")
    @TableField("classify_code")
    @SelectLikeColumn(wildcardPosition = SelectLikeColumn.WildcardPosition.BOTH)
    private String classifyCode;

    /**
     * 类型名称
     */
    @Schema(description = "类型名称")
    @TableField("classify_name")
    @SelectLikeColumn(wildcardPosition = SelectLikeColumn.WildcardPosition.BOTH)
    private String classifyName;

    /**
     * 序号
     */
    @Schema(description = "序号")
    @TableField("order_no")
    private Integer orderNo;
}
