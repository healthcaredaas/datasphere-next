package cn.healthcaredaas.datasphere.svc.agent.service.impl;

import cn.healthcaredaas.datasphere.svc.agent.entity.AuditLog;
import cn.healthcaredaas.datasphere.svc.agent.mapper.AuditLogMapper;
import cn.healthcaredaas.datasphere.svc.agent.service.AuditLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 审计日志服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl extends ServiceImpl<AuditLogMapper, AuditLog>
        implements AuditLogService {

    @Override
    public IPage<AuditLog> pageQuery(IPage<AuditLog> page, AuditLog params) {
        LambdaQueryWrapper<AuditLog> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getSessionId())) {
            wrapper.eq(AuditLog::getSessionId, params.getSessionId());
        }

        if (StringUtils.isNotBlank(params.getUserId())) {
            wrapper.eq(AuditLog::getUserId, params.getUserId());
        }

        if (StringUtils.isNotBlank(params.getTenantId())) {
            wrapper.eq(AuditLog::getTenantId, params.getTenantId());
        }

        if (StringUtils.isNotBlank(params.getOperationType())) {
            wrapper.eq(AuditLog::getOperationType, params.getOperationType());
        }

        if (StringUtils.isNotBlank(params.getStatus())) {
            wrapper.eq(AuditLog::getStatus, params.getStatus());
        }

        wrapper.orderByDesc(AuditLog::getAccessTime);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    @Async
    public void logOperation(String sessionId, String messageId, String userId, String tenantId,
                             String operationType, String operationDesc, String requestContent,
                             String responseContent, String toolsUsed, Long executionTime,
                             String status, String errorMsg, String accessIp) {
        AuditLog log = new AuditLog();
        log.setSessionId(sessionId);
        log.setMessageId(messageId);
        log.setUserId(userId);
        log.setTenantId(tenantId);
        log.setOperationType(operationType);
        log.setOperationDesc(operationDesc);
        log.setRequestContent(requestContent);
        log.setResponseContent(responseContent);
        log.setToolsUsed(toolsUsed);
        log.setExecutionTime(executionTime);
        log.setStatus(status);
        log.setErrorMsg(errorMsg);
        log.setAccessIp(accessIp);
        log.setAccessTime(LocalDateTime.now());
        save(log);
    }
}