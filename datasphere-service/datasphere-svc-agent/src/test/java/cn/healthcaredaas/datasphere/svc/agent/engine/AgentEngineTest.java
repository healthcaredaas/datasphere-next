package cn.healthcaredaas.datasphere.svc.agent.engine;

import cn.healthcaredaas.datasphere.svc.agent.entity.AgentMessage;
import cn.healthcaredaas.datasphere.svc.agent.entity.AgentSession;
import cn.healthcaredaas.datasphere.svc.agent.entity.ModelConfig;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapter;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapterFactory;
import cn.healthcaredaas.datasphere.svc.agent.service.AgentMessageService;
import cn.healthcaredaas.datasphere.svc.agent.service.AgentSessionService;
import cn.healthcaredaas.datasphere.svc.agent.service.AuditLogService;
import cn.healthcaredaas.datasphere.svc.agent.service.ModelConfigService;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolRegistry;
import com.alibaba.fastjson2.JSONObject;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * AgentEngine 单元测试
 *
 * @author chenpan
 */
@ExtendWith(MockitoExtension.class)
class AgentEngineTest {

    @Mock
    private LlmAdapterFactory llmAdapterFactory;

    @Mock
    private ToolRegistry toolRegistry;

    @Mock
    private AgentSessionService sessionService;

    @Mock
    private AgentMessageService messageService;

    @Mock
    private ModelConfigService modelConfigService;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private LlmAdapter llmAdapter;

    @InjectMocks
    private AgentEngine agentEngine;

    private AgentSession testSession;
    private ModelConfig testModelConfig;
    private AgentMessage testUserMessage;
    private AgentMessage testAssistantMessage;

    @BeforeEach
    void setUp() {
        // 准备测试会话
        testSession = new AgentSession();
        testSession.setId("session-001");
        testSession.setTitle("测试会话");
        testSession.setUserId("user-001");
        testSession.setTenantId("tenant-001");
        testSession.setModelId("model-001");
        testSession.setStatus("ACTIVE");

        // 准备测试模型配置
        testModelConfig = new ModelConfig();
        testModelConfig.setId("model-001");
        testModelConfig.setModelName("Claude 3.5 Sonnet");
        testModelConfig.setModelType("CLAUDE");
        testModelConfig.setStatus(1);

        // 准备测试消息
        testUserMessage = new AgentMessage();
        testUserMessage.setId("msg-001");
        testUserMessage.setSessionId("session-001");
        testUserMessage.setRole("USER");
        testUserMessage.setContent("查询患者信息");

        testAssistantMessage = new AgentMessage();
        testAssistantMessage.setId("msg-002");
        testAssistantMessage.setSessionId("session-001");
        testAssistantMessage.setRole("ASSISTANT");
        testAssistantMessage.setContent("好的，我来帮您查询患者信息。");
    }

    @Test
    @DisplayName("处理消息 - 成功返回响应")
    void testProcessMessage_Success() {
        // Given
        when(sessionService.getById("session-001")).thenReturn(testSession);
        when(modelConfigService.getById("model-001")).thenReturn(testModelConfig);
        when(llmAdapterFactory.getAdapter(any(ModelConfig.class))).thenReturn(llmAdapter);
        when(toolRegistry.getAllToolSchemas()).thenReturn(List.of());
        when(messageService.addUserMessage(anyString(), anyString())).thenReturn(testUserMessage);
        when(messageService.getRecentMessages(anyString(), anyInt())).thenReturn(List.of(testUserMessage));
        when(llmAdapter.chat(anyString(), anyString(), any(ModelConfig.class)))
                .thenReturn("好的，我来帮您查询患者信息。");
        when(messageService.addAssistantMessage(anyString(), anyString(), anyString(), any(), any()))
                .thenReturn(testAssistantMessage);

        // When
        AgentMessage result = agentEngine.processMessage("session-001", "查询患者信息", "user-001", "tenant-001");

        // Then
        assertNotNull(result);
        assertEquals("ASSISTANT", result.getRole());
        verify(messageService, times(1)).addUserMessage("session-001", "查询患者信息");
        verify(messageService, times(1)).addAssistantMessage(anyString(), anyString(), anyString(), any(), any());
        verify(sessionService, times(1)).updateActiveTime("session-001");
    }

    @Test
    @DisplayName("处理消息 - 会话不存在抛出异常")
    void testProcessMessage_SessionNotFound() {
        // Given
        when(sessionService.getById("session-001")).thenReturn(null);

        // When & Then
        assertThrows(RuntimeException.class, () ->
                agentEngine.processMessage("session-001", "测试消息", "user-001", "tenant-001"));
    }

    @Test
    @DisplayName("处理消息 - 使用默认模型")
    void testProcessMessage_UseDefaultModel() {
        // Given
        testSession.setModelId(null); // 会话没有指定模型
        when(sessionService.getById("session-001")).thenReturn(testSession);
        when(modelConfigService.getById(null)).thenReturn(null);
        when(modelConfigService.getDefaultModel()).thenReturn(testModelConfig);
        when(llmAdapterFactory.getAdapter(any(ModelConfig.class))).thenReturn(llmAdapter);
        when(toolRegistry.getAllToolSchemas()).thenReturn(List.of());
        when(messageService.addUserMessage(anyString(), anyString())).thenReturn(testUserMessage);
        when(messageService.getRecentMessages(anyString(), anyInt())).thenReturn(List.of());
        when(llmAdapter.chat(anyString(), anyString(), any(ModelConfig.class)))
                .thenReturn("测试响应");
        when(messageService.addAssistantMessage(anyString(), anyString(), anyString(), any(), any()))
                .thenReturn(testAssistantMessage);

        // When
        AgentMessage result = agentEngine.processMessage("session-001", "测试", "user-001", "tenant-001");

        // Then
        assertNotNull(result);
        verify(modelConfigService, times(1)).getDefaultModel();
    }

    @Test
    @DisplayName("处理消息 - LLM调用异常处理")
    void testProcessMessage_LlmException() {
        // Given
        when(sessionService.getById("session-001")).thenReturn(testSession);
        when(modelConfigService.getById("model-001")).thenReturn(testModelConfig);
        when(llmAdapterFactory.getAdapter(any(ModelConfig.class))).thenReturn(llmAdapter);
        when(toolRegistry.getAllToolSchemas()).thenReturn(List.of());
        when(messageService.addUserMessage(anyString(), anyString())).thenReturn(testUserMessage);
        when(messageService.getRecentMessages(anyString(), anyInt())).thenReturn(List.of());
        when(llmAdapter.chat(anyString(), anyString(), any(ModelConfig.class)))
                .thenThrow(new RuntimeException("LLM调用失败"));
        when(messageService.addAssistantMessage(anyString(), anyString(), anyString(), any(), any()))
                .thenReturn(testAssistantMessage);

        // When
        AgentMessage result = agentEngine.processMessage("session-001", "测试", "user-001", "tenant-001");

        // Then
        assertNotNull(result);
        verify(messageService, times(1)).addAssistantMessage(
                anyString(), contains("错误"), eq("ERROR"), any(), any());
    }

    @Test
    @DisplayName("流式处理消息 - 成功")
    void testProcessMessageStream_Success() {
        // Given
        when(sessionService.getById("session-001")).thenReturn(testSession);
        when(modelConfigService.getById("model-001")).thenReturn(testModelConfig);
        when(llmAdapterFactory.getAdapter(any(ModelConfig.class))).thenReturn(llmAdapter);
        when(toolRegistry.getAllToolSchemas()).thenReturn(List.of());
        when(messageService.addUserMessage(anyString(), anyString())).thenReturn(testUserMessage);
        when(messageService.getRecentMessages(anyString(), anyInt())).thenReturn(List.of());

        // 模拟流式回调
        doAnswer(invocation -> {
            LlmAdapter.StreamCallback callback = invocation.getArgument(3);
            callback.onToken("测");
            callback.onToken("试");
            callback.onComplete("测试");
            return null;
        }).when(llmAdapter).chatStream(anyString(), anyString(), any(ModelConfig.class), any());

        when(messageService.addAssistantMessage(anyString(), anyString(), anyString(), any(), any()))
                .thenReturn(testAssistantMessage);

        // 用于收集回调结果
        StringBuilder collected = new StringBuilder();
        LlmAdapter.StreamCallback testCallback = new LlmAdapter.StreamCallback() {
            @Override
            public void onToken(String token) {
                collected.append(token);
            }

            @Override
            public void onComplete(String fullResponse) {
            }

            @Override
            public void onError(Throwable error) {
            }
        };

        // When
        agentEngine.processMessageStream("session-001", "测试", "user-001", "tenant-001", testCallback);

        // Then
        assertEquals("测试", collected.toString());
        verify(sessionService, times(1)).updateActiveTime("session-001");
    }

    @Test
    @DisplayName("流式处理消息 - 会话不存在")
    void testProcessMessageStream_SessionNotFound() {
        // Given
        when(sessionService.getById("session-001")).thenReturn(null);

        LlmAdapter.StreamCallback testCallback = mock(LlmAdapter.StreamCallback.class);

        // When
        agentEngine.processMessageStream("session-001", "测试", "user-001", "tenant-001", testCallback);

        // Then
        verify(testCallback, times(1)).onError(any(RuntimeException.class));
    }
}