package cn.healthcaredaas.datasphere.core.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 * 所有业务实体的基类
 *
 * @author chenpan
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 创建人
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 更新次数（乐观锁）
     */
    @Version
    @TableField(value = "update_count", fill = FieldFill.INSERT)
    private Integer updateCount;

    /**
     * 删除标志（逻辑删除）
     */
    @TableLogic
    @TableField(value = "delete_flag", fill = FieldFill.INSERT)
    private String deleteFlag;

    /**
     * 删除时间
     */
    @TableField(value = "delete_time")
    private LocalDateTime deleteTime;

    /**
     * 排序字段（非数据库字段，用于查询）
     */
    @TableField(exist = false)
    private transient String sortBy;

    /**
     * 排序方式：asc/desc（非数据库字段，用于查询）
     */
    @TableField(exist = false)
    private transient String sortOrder;

    /**
     * 设置排序字段
     *
     * @param fields 排序字段
     */
    public void setSortBy(String... fields) {
        if (fields != null && fields.length > 0) {
            this.sortBy = String.join(",", fields);
        }
    }

    /**
     * 设置排序方式
     *
     * @param sortOrder 排序方式：asc/desc
     */
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
