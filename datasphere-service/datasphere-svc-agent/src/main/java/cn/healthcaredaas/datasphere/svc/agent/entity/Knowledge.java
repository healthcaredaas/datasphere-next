package cn.healthcaredaas.datasphere.svc.agent.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 知识库实体
 *
 * @author chenpan
 */
@TableName(value = "ai_knowledge")
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "知识库")
public class Knowledge extends BaseEntity {

    @Schema(description = "知识类型: STANDARD/TEMPLATE/FAQ/METADATA")
    @TableField("knowledge_type")
    private String knowledgeType;

    @Schema(description = "知识标题")
    @TableField("title")
    private String title;

    @Schema(description = "知识内容")
    @TableField("content")
    private String content;

    @Schema(description = "向量ID")
    @TableField("vector_id")
    private String vectorId;

    @Schema(description = "标签列表(JSON)")
    @TableField("tags")
    private String tags;

    @Schema(description = "来源")
    @TableField("source")
    private String source;

    @Schema(description = "有效期开始")
    @TableField("valid_from")
    private LocalDateTime validFrom;

    @Schema(description = "有效期结束")
    @TableField("valid_to")
    private LocalDateTime validTo;

    @Schema(description = "租户ID(空为公共知识)")
    @TableField("tenant_id")
    private String tenantId;
}