package cn.healthcaredaas.datasphere.svc.agent.service;

import cn.healthcaredaas.datasphere.svc.agent.entity.AgentSession;
import cn.healthcaredaas.datasphere.svc.agent.mapper.AgentSessionMapper;
import cn.healthcaredaas.datasphere.svc.agent.service.impl.AgentSessionServiceImpl;
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
 * AgentSessionService 单元测试
 *
 * @author chenpan
 */
@ExtendWith(MockitoExtension.class)
class AgentSessionServiceTest {

    @Mock
    private AgentSessionMapper sessionMapper;

    @InjectMocks
    private AgentSessionServiceImpl sessionService;

    private AgentSession testSession;

    @BeforeEach
    void setUp() {
        testSession = new AgentSession();
        testSession.setId("test-session-001");
        testSession.setTitle("测试会话");
        testSession.setUserId("user-001");
        testSession.setTenantId("tenant-001");
        testSession.setStatus("ACTIVE");
        testSession.setModelId("model-001");
        testSession.setMessageCount(0);
    }

    @Test
    @DisplayName("创建会话 - 成功")
    void testCreateSession() {
        // Given
        when(sessionMapper.insert(any(AgentSession.class))).thenReturn(1);

        // When
        AgentSession result = sessionService.createSession(
                "新会话", "model-001", "user-001", "tenant-001"
        );

        // Then
        assertNotNull(result);
        assertEquals("新会话", result.getTitle());
        assertEquals("user-001", result.getUserId());
        assertEquals("ACTIVE", result.getStatus());
        verify(sessionMapper, times(1)).insert(any(AgentSession.class));
    }

    @Test
    @DisplayName("根据用户ID查询会话列表")
    void testListByUserId() {
        // Given
        when(sessionMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testSession));

        // When
        List<AgentSession> result = sessionService.listByUserId("user-001");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("user-001", result.get(0).getUserId());
    }

    @Test
    @DisplayName("分页查询会话")
    void testPageQuery() {
        // Given
        Page<AgentSession> page = new Page<>(1, 10);
        when(sessionMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        AgentSession params = new AgentSession();
        params.setUserId("user-001");
        params.setStatus("ACTIVE");

        // When
        IPage<AgentSession> result = sessionService.pageQuery(page, params);

        // Then
        assertNotNull(result);
        verify(sessionMapper, times(1)).selectPage(any(IPage.class), any());
    }

    @Test
    @DisplayName("更新会话活跃时间")
    void testUpdateActiveTime() {
        // Given
        when(sessionMapper.updateById(any(AgentSession.class))).thenReturn(1);

        // When
        sessionService.updateActiveTime("test-session-001");

        // Then
        verify(sessionMapper, times(1)).updateById(any(AgentSession.class));
    }

    @Test
    @DisplayName("归档会话")
    void testArchiveSession() {
        // Given
        when(sessionMapper.updateById(any(AgentSession.class))).thenReturn(1);

        // When
        sessionService.archiveSession("test-session-001");

        // Then
        verify(sessionMapper, times(1)).updateById(any(AgentSession.class));
    }

    @Test
    @DisplayName("增加消息计数")
    void testIncrementMessageCount() {
        // Given
        when(sessionMapper.selectById("test-session-001")).thenReturn(testSession);
        when(sessionMapper.updateById(any(AgentSession.class))).thenReturn(1);

        // When
        sessionService.incrementMessageCount("test-session-001");

        // Then
        assertEquals(1, testSession.getMessageCount());
        verify(sessionMapper, times(1)).updateById(any(AgentSession.class));
    }
}