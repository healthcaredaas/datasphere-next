package cn.healthcaredaas.datasphere.svc.agent.llm;

import cn.healthcaredaas.datasphere.svc.agent.entity.ModelConfig;
import cn.healthcaredaas.datasphere.svc.agent.llm.adapter.ClaudeAdapter;
import cn.healthcaredaas.datasphere.svc.agent.llm.adapter.OpenAiAdapter;
import cn.healthcaredaas.datasphere.svc.agent.llm.adapter.QwenAdapter;
import cn.healthcaredaas.datasphere.svc.agent.llm.adapter.LocalModelAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * LLM适配器工厂测试
 *
 * @author chenpan
 */
@ExtendWith(MockitoExtension.class)
class LlmAdapterFactoryTest {

    @Mock
    private ClaudeAdapter claudeAdapter;

    @Mock
    private OpenAiAdapter openAiAdapter;

    @Mock
    private QwenAdapter qwenAdapter;

    @Mock
    private LocalModelAdapter localModelAdapter;

    @InjectMocks
    private LlmAdapterFactory factory;

    private ModelConfig claudeConfig;
    private ModelConfig gptConfig;
    private ModelConfig qwenConfig;
    private ModelConfig localConfig;

    @BeforeEach
    void setUp() {
        // 配置Mock返回值
        when(claudeAdapter.getModelType()).thenReturn("CLAUDE");
        when(openAiAdapter.getModelType()).thenReturn("GPT");
        when(qwenAdapter.getModelType()).thenReturn("QWEN");
        when(localModelAdapter.getModelType()).thenReturn("LOCAL");

        // 手动初始化factory的adapterMap
        factory = new LlmAdapterFactory(List.of(claudeAdapter, openAiAdapter, qwenAdapter, localModelAdapter));
        factory.init();

        // 准备测试配置
        claudeConfig = new ModelConfig();
        claudeConfig.setModelType("CLAUDE");
        claudeConfig.setModelName("claude-3-5-sonnet");

        gptConfig = new ModelConfig();
        gptConfig.setModelType("GPT");
        gptConfig.setModelName("gpt-4o");

        qwenConfig = new ModelConfig();
        qwenConfig.setModelType("QWEN");
        qwenConfig.setModelName("qwen-max");

        localConfig = new ModelConfig();
        localConfig.setModelType("LOCAL");
        localConfig.setModelName("llama3");
    }

    @Test
    @DisplayName("初始化 - 注册所有适配器")
    void testInit_RegistersAllAdapters() {
        // When
        List<String> supportedTypes = factory.getSupportedModelTypes();

        // Then
        assertEquals(4, supportedTypes.size());
        assertTrue(supportedTypes.contains("CLAUDE"));
        assertTrue(supportedTypes.contains("GPT"));
        assertTrue(supportedTypes.contains("QWEN"));
        assertTrue(supportedTypes.contains("LOCAL"));
    }

    @Test
    @DisplayName("获取适配器 - Claude类型")
    void testGetAdapter_Claude() {
        // When
        LlmAdapter adapter = factory.getAdapter(claudeConfig);

        // Then
        assertNotNull(adapter);
        assertEquals("CLAUDE", adapter.getModelType());
        assertSame(claudeAdapter, adapter);
    }

    @Test
    @DisplayName("获取适配器 - GPT类型")
    void testGetAdapter_GPT() {
        // When
        LlmAdapter adapter = factory.getAdapter(gptConfig);

        // Then
        assertNotNull(adapter);
        assertEquals("GPT", adapter.getModelType());
        assertSame(openAiAdapter, adapter);
    }

    @Test
    @DisplayName("获取适配器 - Qwen类型")
    void testGetAdapter_Qwen() {
        // When
        LlmAdapter adapter = factory.getAdapter(qwenConfig);

        // Then
        assertNotNull(adapter);
        assertEquals("QWEN", adapter.getModelType());
        assertSame(qwenAdapter, adapter);
    }

    @Test
    @DisplayName("获取适配器 - Local类型")
    void testGetAdapter_Local() {
        // When
        LlmAdapter adapter = factory.getAdapter(localConfig);

        // Then
        assertNotNull(adapter);
        assertEquals("LOCAL", adapter.getModelType());
        assertSame(localModelAdapter, adapter);
    }

    @Test
    @DisplayName("获取适配器 - 不支持的类型抛出异常")
    void testGetAdapter_UnsupportedType() {
        // Given
        ModelConfig unknownConfig = new ModelConfig();
        unknownConfig.setModelType("UNKNOWN");

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                factory.getAdapter(unknownConfig));
    }

    @Test
    @DisplayName("通过类型字符串获取适配器")
    void testGetAdapterByType() {
        // When
        LlmAdapter adapter = factory.getAdapter("CLAUDE");

        // Then
        assertNotNull(adapter);
        assertEquals("CLAUDE", adapter.getModelType());
    }

    @Test
    @DisplayName("通过不存在的类型字符串获取适配器 - 抛出异常")
    void testGetAdapterByType_NotFound() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                factory.getAdapter("UNKNOWN"));
    }
}