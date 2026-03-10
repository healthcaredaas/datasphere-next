package cn.healthcaredaas.datasphere.svc.agent.service;

import cn.healthcaredaas.datasphere.svc.agent.entity.AuditLog;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 审计日志服务接口
 *
 * @author chenpan
 */
public interface AuditLogService extends IService<AuditLog> {

    /**
     * 分页查询日志
     */
    IPage<AuditLog> pageQuery(IPage<AuditLog> page, AuditLog params);

    /**
     * 记录操作日志
     */
    void logOperation(String sessionId, String messageId, String userId, String tenantId,
                      String operationType, String operationDesc, String requestContent,
                      String responseContent, String toolsUsed, Long executionTime,
                      String status, String errorMsg, String accessIp);

    /**
     * 简化的日志记录方法
     */
    default void log(String sessionId, String messageId, String userId, String tenantId,
                     String operationType, String requestContent, String responseContent,
                     List<JSONObject> toolsUsed, Long executionTime, String status, String errorMsg) {
        String toolsJson = toolsUsed != null ? JSONObject.toJSONString(toolsUsed) : null;
        logOperation(sessionId, messageId, userId, tenantId, operationType, null,
                requestContent, responseContent, toolsJson, executionTime, status, errorMsg, null);
    }
}