package cn.healthcaredaas.datasphere.svc.agent.service;

import cn.healthcaredaas.datasphere.svc.agent.entity.ApiKey;
import cn.healthcaredaas.datasphere.svc.agent.mapper.ApiKeyMapper;
import cn.healthcaredaas.datasphere.svc.agent.service.impl.ApiKeyServiceImpl;
import com.alibaba.fastjson2.JSON;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ApiKeyService 单元测试
 *
 * @author chenpan
 */
@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @Mock
    private ApiKeyMapper apiKeyMapper;

    @InjectMocks
    private ApiKeyServiceImpl apiKeyService;

    private ApiKey testApiKey;

    @BeforeEach
    void setUp() {
        testApiKey = new ApiKey();
        testApiKey.setId("key-001");
        testApiKey.setKeyName("测试密钥");
        testApiKey.setApiKey("sk-tenant-001-abcdefghijklmnopqrstuvwxyz123456");
        testApiKey.setUserId("user-001");
        testApiKey.setTenantId("tenant-001");
        testApiKey.setPermissions(JSON.toJSONString(List.of("sql.generate", "sql.execute")));
        testApiKey.setRateLimit(100);
        testApiKey.setStatus(1);
    }

    @Test
    @DisplayName("分页查询API密钥")
    void testPageQuery() {
        // Given
        Page<ApiKey> page = new Page<>(1, 10);
        when(apiKeyMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        ApiKey params = new ApiKey();
        params.setUserId("user-001");
        params.setStatus(1);

        // When
        IPage<ApiKey> result = apiKeyService.pageQuery(page, params);

        // Then
        assertNotNull(result);
        verify(apiKeyMapper, times(1)).selectPage(any(IPage.class), any());
    }

    @Test
    @DisplayName("根据API密钥查询")
    void testGetByApiKey() {
        // Given
        when(apiKeyMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(testApiKey);

        // When
        ApiKey result = apiKeyService.getByApiKey("sk-tenant-001-abcdefghijklmnopqrstuvwxyz123456");

        // Then
        assertNotNull(result);
        assertEquals("user-001", result.getUserId());
    }

    @Test
    @DisplayName("生成新的API密钥")
    void testGenerateApiKey() {
        // Given
        when(apiKeyMapper.insert(any(ApiKey.class))).thenReturn(1);

        // When
        ApiKey result = apiKeyService.generateApiKey(
                "新密钥", "user-001", "tenant-001",
                List.of("sql.generate", "sql.execute"), 100
        );

        // Then
        assertNotNull(result);
        assertEquals("新密钥", result.getKeyName());
        assertTrue(result.getApiKey().startsWith("sk-tenant-001-"));
        assertEquals(100, result.getRateLimit());
        verify(apiKeyMapper, times(1)).insert(any(ApiKey.class));
    }

    @Test
    @DisplayName("验证API密钥 - 有效")
    void testValidateApiKey_Valid() {
        // Given
        when(apiKeyMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(testApiKey);

        // When
        boolean valid = apiKeyService.validateApiKey(
                "sk-tenant-001-abcdefghijklmnopqrstuvwxyz123456",
                "sql.generate"
        );

        // Then
        assertTrue(valid);
    }

    @Test
    @DisplayName("验证API密钥 - 密钥不存在")
    void testValidateApiKey_NotFound() {
        // Given
        when(apiKeyMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(null);

        // When
        boolean valid = apiKeyService.validateApiKey("invalid-key", "sql.generate");

        // Then
        assertFalse(valid);
    }

    @Test
    @DisplayName("验证API密钥 - 已过期")
    void testValidateApiKey_Expired() {
        // Given
        testApiKey.setExpiredAt(LocalDateTime.now().minusDays(1));
        when(apiKeyMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(testApiKey);

        // When
        boolean valid = apiKeyService.validateApiKey(
                "sk-tenant-001-abcdefghijklmnopqrstuvwxyz123456",
                "sql.generate"
        );

        // Then
        assertFalse(valid);
    }

    @Test
    @DisplayName("验证API密钥 - 权限不足")
    void testValidateApiKey_NoPermission() {
        // Given
        when(apiKeyMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(testApiKey);

        // When
        boolean valid = apiKeyService.validateApiKey(
                "sk-tenant-001-abcdefghijklmnopqrstuvwxyz123456",
                "admin.manage"
        );

        // Then
        assertFalse(valid);
    }

    @Test
    @DisplayName("验证API密钥 - 通配符权限")
    void testValidateApiKey_WildcardPermission() {
        // Given
        testApiKey.setPermissions(JSON.toJSONString(List.of("*")));
        when(apiKeyMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(testApiKey);

        // When
        boolean valid = apiKeyService.validateApiKey(
                "sk-tenant-001-abcdefghijklmnopqrstuvwxyz123456",
                "any.permission"
        );

        // Then
        assertTrue(valid);
    }

    @Test
    @DisplayName("更新最后使用时间")
    void testUpdateLastUsedTime() {
        // Given
        when(apiKeyMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(testApiKey);
        when(apiKeyMapper.updateById(any(ApiKey.class))).thenReturn(1);

        // When
        apiKeyService.updateLastUsedTime("sk-tenant-001-abcdefghijklmnopqrstuvwxyz123456");

        // Then
        assertNotNull(testApiKey.getLastUsedAt());
        verify(apiKeyMapper, times(1)).updateById(any(ApiKey.class));
    }

    @Test
    @DisplayName("吊销API密钥")
    void testRevokeApiKey() {
        // Given
        when(apiKeyMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(testApiKey);
        when(apiKeyMapper.updateById(any(ApiKey.class))).thenReturn(1);

        // When
        apiKeyService.revokeApiKey("sk-tenant-001-abcdefghijklmnopqrstuvwxyz123456");

        // Then
        assertEquals(0, testApiKey.getStatus());
        verify(apiKeyMapper, times(1)).updateById(any(ApiKey.class));
    }
}