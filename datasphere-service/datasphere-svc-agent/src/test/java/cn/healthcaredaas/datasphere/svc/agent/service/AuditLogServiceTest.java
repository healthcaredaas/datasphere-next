package cn.healthcaredaas.datasphere.svc.agent.service;

import cn.healthcaredaas.datasphere.svc.agent.entity.AuditLog;
import cn.healthcaredaas.datasphere.svc.agent.mapper.AuditLogMapper;
import cn.healthcaredaas.datasphere.svc.agent.service.impl.AuditLogServiceImpl;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AuditLogService 单元测试
 *
 * @author chenpan
 */
@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogMapper auditLogMapper;

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    private AuditLog testAuditLog;

    @BeforeEach
    void setUp() {
        testAuditLog = new AuditLog();
        testAuditLog.setId("log-001");
        testAuditLog.setSessionId("session-001");
        testAuditLog.setMessageId("msg-001");
        testAuditLog.setUserId("user-001");
        testAuditLog.setTenantId("tenant-001");
        testAuditLog.setOperationType("CHAT");
        testAuditLog.setRequestContent("查询患者信息");
        testAuditLog.setResponseContent("好的，我来帮您查询患者信息。");
        testAuditLog.setExecutionTime(1500L);
        testAuditLog.setStatus("SUCCESS");
    }

    @Test
    @DisplayName("分页查询审计日志")
    void testPageQuery() {
        // Given
        Page<AuditLog> page = new Page<>(1, 10);
        when(auditLogMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        AuditLog params = new AuditLog();
        params.setUserId("user-001");
        params.setOperationType("CHAT");
        params.setStatus("SUCCESS");

        // When
        IPage<AuditLog> result = auditLogService.pageQuery(page, params);

        // Then
        assertNotNull(result);
        verify(auditLogMapper, times(1)).selectPage(any(IPage.class), any());
    }

    @Test
    @DisplayName("分页查询 - 按会话ID过滤")
    void testPageQuery_BySessionId() {
        // Given
        Page<AuditLog> page = new Page<>(1, 10);
        when(auditLogMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        AuditLog params = new AuditLog();
        params.setSessionId("session-001");

        // When
        IPage<AuditLog> result = auditLogService.pageQuery(page, params);

        // Then
        assertNotNull(result);
        verify(auditLogMapper, times(1)).selectPage(any(IPage.class), any());
    }

    @Test
    @DisplayName("记录操作日志 - 成功操作")
    void testLogOperation_Success() {
        // Given
        when(auditLogMapper.insert(any(AuditLog.class))).thenReturn(1);

        // When
        auditLogService.logOperation(
                "session-001", "msg-001", "user-001", "tenant-001",
                "CHAT", "用户对话", "查询患者信息", "好的，我来帮您查询。",
                null, 1500L, "SUCCESS", null, "127.0.0.1"
        );

        // Then
        verify(auditLogMapper, times(1)).insert(any(AuditLog.class));
    }

    @Test
    @DisplayName("记录操作日志 - 失败操作")
    void testLogOperation_Failure() {
        // Given
        when(auditLogMapper.insert(any(AuditLog.class))).thenReturn(1);

        // When
        auditLogService.logOperation(
                "session-001", "msg-001", "user-001", "tenant-001",
                "CHAT", "用户对话", "查询患者信息", null,
                null, 500L, "FAIL", "连接超时", "127.0.0.1"
        );

        // Then
        verify(auditLogMapper, times(1)).insert(any(AuditLog.class));
    }

    @Test
    @DisplayName("记录操作日志 - 带工具调用")
    void testLogOperation_WithTools() {
        // Given
        when(auditLogMapper.insert(any(AuditLog.class))).thenReturn(1);

        List<JSONObject> toolsUsed = List.of(
                JSONObject.of("name", "sql_generator", "params", JSONObject.of("query", "患者信息")),
                JSONObject.of("name", "sql_executor", "params", JSONObject.of("sql", "SELECT * FROM patient"))
        );

        // When
        auditLogService.logOperation(
                "session-001", "msg-001", "user-001", "tenant-001",
                "TOOL_CALL", "工具调用", "查询患者信息", "查询完成",
                JSONObject.toJSONString(toolsUsed), 2000L, "SUCCESS", null, "127.0.0.1"
        );

        // Then
        verify(auditLogMapper, times(1)).insert(any(AuditLog.class));
    }

    @Test
    @DisplayName("简化日志记录方法")
    void testLog_SimplifiedMethod() {
        // Given
        when(auditLogMapper.insert(any(AuditLog.class))).thenReturn(1);

        List<JSONObject> toolsUsed = List.of(
                JSONObject.of("name", "sql_generator")
        );

        // When
        auditLogService.log(
                "session-001", "msg-001", "user-001", "tenant-001",
                "CHAT", "查询患者信息", "查询完成",
                toolsUsed, 1500L, "SUCCESS", null
        );

        // Then
        verify(auditLogMapper, times(1)).insert(any(AuditLog.class));
    }
}