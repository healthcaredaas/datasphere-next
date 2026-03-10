package cn.healthcaredaas.datasphere.svc.agent.service;

import cn.healthcaredaas.datasphere.svc.agent.entity.AgentMessage;
import cn.healthcaredaas.datasphere.svc.agent.mapper.AgentMessageMapper;
import cn.healthcaredaas.datasphere.svc.agent.service.impl.AgentMessageServiceImpl;
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
 * AgentMessageService 单元测试
 *
 * @author chenpan
 */
@ExtendWith(MockitoExtension.class)
class AgentMessageServiceTest {

    @Mock
    private AgentMessageMapper messageMapper;

    @Mock
    private AgentSessionService sessionService;

    @InjectMocks
    private AgentMessageServiceImpl messageService;

    private AgentMessage testUserMessage;
    private AgentMessage testAssistantMessage;

    @BeforeEach
    void setUp() {
        testUserMessage = new AgentMessage();
        testUserMessage.setId("msg-001");
        testUserMessage.setSessionId("session-001");
        testUserMessage.setRole("USER");
        testUserMessage.setContent("查询患者信息");
        testUserMessage.setContentType("TEXT");

        testAssistantMessage = new AgentMessage();
        testAssistantMessage.setId("msg-002");
        testAssistantMessage.setSessionId("session-001");
        testAssistantMessage.setRole("ASSISTANT");
        testAssistantMessage.setContent("好的，我来帮您查询患者信息。");
        testAssistantMessage.setContentType("TEXT");
    }

    @Test
    @DisplayName("分页查询消息")
    void testPageQuery() {
        // Given
        Page<AgentMessage> page = new Page<>(1, 10);
        when(messageMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        AgentMessage params = new AgentMessage();
        params.setSessionId("session-001");
        params.setRole("USER");

        // When
        IPage<AgentMessage> result = messageService.pageQuery(page, params);

        // Then
        assertNotNull(result);
        verify(messageMapper, times(1)).selectPage(any(IPage.class), any());
    }

    @Test
    @DisplayName("根据会话ID查询消息列表")
    void testListBySessionId() {
        // Given
        when(messageMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testUserMessage, testAssistantMessage));

        // When
        List<AgentMessage> result = messageService.listBySessionId("session-001");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("根据会话ID查询消息列表 - 空结果")
    void testListBySessionId_EmptyResult() {
        // Given
        when(messageMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of());

        // When
        List<AgentMessage> result = messageService.listBySessionId("session-001");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("获取会话最近的N条消息")
    void testGetRecentMessages() {
        // Given
        when(messageMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testAssistantMessage, testUserMessage));

        // When
        List<AgentMessage> result = messageService.getRecentMessages("session-001", 10);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("添加用户消息")
    void testAddUserMessage() {
        // Given
        when(messageMapper.insert(any(AgentMessage.class))).thenReturn(1);
        doNothing().when(sessionService).incrementMessageCount(anyString());

        // When
        AgentMessage result = messageService.addUserMessage("session-001", "测试消息");

        // Then
        assertNotNull(result);
        assertEquals("session-001", result.getSessionId());
        assertEquals("USER", result.getRole());
        assertEquals("测试消息", result.getContent());
        assertEquals("TEXT", result.getContentType());
        verify(messageMapper, times(1)).insert(any(AgentMessage.class));
        verify(sessionService, times(1)).incrementMessageCount("session-001");
    }

    @Test
    @DisplayName("添加助手消息 - 普通文本")
    void testAddAssistantMessage_Text() {
        // Given
        when(messageMapper.insert(any(AgentMessage.class))).thenReturn(1);
        doNothing().when(sessionService).incrementMessageCount(anyString());

        // When
        AgentMessage result = messageService.addAssistantMessage(
                "session-001", "这是助手的回复", "TEXT", null, null
        );

        // Then
        assertNotNull(result);
        assertEquals("ASSISTANT", result.getRole());
        assertEquals("这是助手的回复", result.getContent());
        assertEquals("TEXT", result.getContentType());
        verify(sessionService, times(1)).incrementMessageCount("session-001");
    }

    @Test
    @DisplayName("添加助手消息 - SQL结果")
    void testAddAssistantMessage_SqlResult() {
        // Given
        when(messageMapper.insert(any(AgentMessage.class))).thenReturn(1);
        doNothing().when(sessionService).incrementMessageCount(anyString());

        // When
        AgentMessage result = messageService.addAssistantMessage(
                "session-001",
                "SELECT * FROM patient LIMIT 10",
                "SQL",
                null,
                "{\"input\": 50, \"output\": 100}"
        );

        // Then
        assertNotNull(result);
        assertEquals("ASSISTANT", result.getRole());
        assertEquals("SQL", result.getContentType());
    }

    @Test
    @DisplayName("添加助手消息 - 带工具调用")
    void testAddAssistantMessage_WithToolCalls() {
        // Given
        when(messageMapper.insert(any(AgentMessage.class))).thenReturn(1);
        doNothing().when(sessionService).incrementMessageCount(anyString());

        String toolCalls = "[{\"name\": \"sql_generator\", \"params\": {\"query\": \"患者信息\"}}]";

        // When
        AgentMessage result = messageService.addAssistantMessage(
                "session-001",
                "已生成SQL查询",
                "TEXT",
                toolCalls,
                "{\"input\": 100, \"output\": 50}"
        );

        // Then
        assertNotNull(result);
        assertEquals(toolCalls, result.getToolCalls());
    }

    @Test
    @DisplayName("添加助手消息 - 错误消息")
    void testAddAssistantMessage_Error() {
        // Given
        when(messageMapper.insert(any(AgentMessage.class))).thenReturn(1);
        doNothing().when(sessionService).incrementMessageCount(anyString());

        // When
        AgentMessage result = messageService.addAssistantMessage(
                "session-001",
                "执行出错：连接超时",
                "ERROR",
                null,
                null
        );

        // Then
        assertNotNull(result);
        assertEquals("ERROR", result.getContentType());
    }
}