package cn.healthcaredaas.datasphere.svc.agent.entity;

import cn.healthcaredaas.datasphere.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Agent消息实体
 *
 * @author chenpan
 */
@TableName(value = "ai_agent_message")
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Agent消息")
public class AgentMessage extends BaseEntity {

    @Schema(description = "会话ID")
    @TableField("session_id")
    private String sessionId;

    @Schema(description = "消息角色: USER/ASSISTANT/SYSTEM/TOOL")
    @TableField("role")
    private String role;

    @Schema(description = "消息内容")
    @TableField("content")
    private String content;

    @Schema(description = "消息类型: TEXT/SQL/TABLE/CHART/ERROR")
    @TableField("content_type")
    private String contentType;

    @Schema(description = "工具调用记录(JSON)")
    @TableField("tool_calls")
    private String toolCalls;

    @Schema(description = "Token消耗(JSON)")
    @TableField("token_usage")
    private String tokenUsage;

    @Schema(description = "引用的知识ID列表(JSON)")
    @TableField("knowledge_refs")
    private String knowledgeRefs;

    @Schema(description = "父消息ID(用于消息引用)")
    @TableField("parent_message_id")
    private String parentMessageId;
}