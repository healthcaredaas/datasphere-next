package cn.healthcaredaas.datasphere.svc.agent.tools;

import lombok.Data;

/**
 * 工具执行上下文
 *
 * @author chenpan
 */
@Data
public class ToolContext {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 数据源ID(用于SQL执行)
     */
    private String datasourceId;

    public static ToolContext of(String sessionId, String userId, String tenantId) {
        ToolContext context = new ToolContext();
        context.setSessionId(sessionId);
        context.setUserId(userId);
        context.setTenantId(tenantId);
        return context;
    }
}