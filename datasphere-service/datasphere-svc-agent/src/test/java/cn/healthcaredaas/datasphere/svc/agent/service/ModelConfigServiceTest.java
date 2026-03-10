package cn.healthcaredaas.datasphere.svc.agent.service;

import cn.healthcaredaas.datasphere.svc.agent.entity.ModelConfig;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapter;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapterFactory;
import cn.healthcaredaas.datasphere.svc.agent.mapper.ModelConfigMapper;
import cn.healthcaredaas.datasphere.svc.agent.service.impl.ModelConfigServiceImpl;
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
 * ModelConfigService 单元测试
 *
 * @author chenpan
 */
@ExtendWith(MockitoExtension.class)
class ModelConfigServiceTest {

    @Mock
    private ModelConfigMapper modelConfigMapper;

    @Mock
    private LlmAdapterFactory llmAdapterFactory;

    @InjectMocks
    private ModelConfigServiceImpl modelConfigService;

    private ModelConfig testModelConfig;

    @BeforeEach
    void setUp() {
        testModelConfig = new ModelConfig();
        testModelConfig.setId("model-001");
        testModelConfig.setModelName("Claude 3.5 Sonnet");
        testModelConfig.setModelType("CLAUDE");
        testModelConfig.setApiEndpoint("https://api.anthropic.com");
        testModelConfig.setStatus(1);
        testModelConfig.setPriority(100);
    }

    @Test
    @DisplayName("分页查询模型配置")
    void testPageQuery() {
        // Given
        Page<ModelConfig> page = new Page<>(1, 10);
        when(modelConfigMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        ModelConfig params = new ModelConfig();
        params.setModelType("CLAUDE");
        params.setStatus(1);

        // When
        IPage<ModelConfig> result = modelConfigService.pageQuery(page, params);

        // Then
        assertNotNull(result);
        verify(modelConfigMapper, times(1)).selectPage(any(IPage.class), any());
    }

    @Test
    @DisplayName("获取启用的模型列表")
    void testListEnabled() {
        // Given
        when(modelConfigMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testModelConfig));

        // When
        List<ModelConfig> result = modelConfigService.listEnabled();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getStatus());
    }

    @Test
    @DisplayName("根据类型获取模型")
    void testGetByType() {
        // Given
        when(modelConfigMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(testModelConfig);

        // When
        ModelConfig result = modelConfigService.getByType("CLAUDE");

        // Then
        assertNotNull(result);
        assertEquals("CLAUDE", result.getModelType());
    }

    @Test
    @DisplayName("根据类型获取模型 - 不存在")
    void testGetByType_NotFound() {
        // Given
        when(modelConfigMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(null);

        // When
        ModelConfig result = modelConfigService.getByType("UNKNOWN");

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("获取默认模型")
    void testGetDefaultModel() {
        // Given
        when(modelConfigMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(testModelConfig);

        // When
        ModelConfig result = modelConfigService.getDefaultModel();

        // Then
        assertNotNull(result);
        assertEquals(100, result.getPriority());
    }

    @Test
    @DisplayName("测试模型连接 - 成功")
    void testTestConnection_Success() {
        // Given
        LlmAdapter mockAdapter = mock(LlmAdapter.class);

        when(modelConfigMapper.selectById("model-001")).thenReturn(testModelConfig);
        when(llmAdapterFactory.getAdapter(any(ModelConfig.class))).thenReturn(mockAdapter);
        when(mockAdapter.testConnection()).thenReturn(true);

        // When
        boolean result = modelConfigService.testConnection("model-001");

        // Then
        assertTrue(result);
        verify(llmAdapterFactory, times(1)).getAdapter(any(ModelConfig.class));
        verify(mockAdapter, times(1)).testConnection();
    }

    @Test
    @DisplayName("测试模型连接 - 模型不存在")
    void testTestConnection_ModelNotFound() {
        // Given
        when(modelConfigMapper.selectById("unknown-model")).thenReturn(null);

        // When
        boolean result = modelConfigService.testConnection("unknown-model");

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("测试模型连接 - 模型已禁用")
    void testTestConnection_ModelDisabled() {
        // Given
        testModelConfig.setStatus(0);
        when(modelConfigMapper.selectById("model-001")).thenReturn(testModelConfig);

        // When
        boolean result = modelConfigService.testConnection("model-001");

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("测试模型连接 - 连接异常")
    void testTestConnection_Exception() {
        // Given
        when(modelConfigMapper.selectById("model-001")).thenReturn(testModelConfig);
        when(llmAdapterFactory.getAdapter(any(ModelConfig.class)))
                .thenThrow(new RuntimeException("连接失败"));

        // When
        boolean result = modelConfigService.testConnection("model-001");

        // Then
        assertFalse(result);
    }
}