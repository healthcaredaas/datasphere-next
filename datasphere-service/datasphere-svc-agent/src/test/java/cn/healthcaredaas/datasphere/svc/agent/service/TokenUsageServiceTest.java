package cn.healthcaredaas.datasphere.svc.agent.service;

import cn.healthcaredaas.datasphere.svc.agent.entity.TokenUsage;
import cn.healthcaredaas.datasphere.svc.agent.mapper.TokenUsageMapper;
import cn.healthcaredaas.datasphere.svc.agent.service.impl.TokenUsageServiceImpl;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TokenUsageService 单元测试
 *
 * @author chenpan
 */
@ExtendWith(MockitoExtension.class)
class TokenUsageServiceTest {

    @Mock
    private TokenUsageMapper tokenUsageMapper;

    @InjectMocks
    private TokenUsageServiceImpl tokenUsageService;

    private TokenUsage testTokenUsage;

    @BeforeEach
    void setUp() {
        testTokenUsage = new TokenUsage();
        testTokenUsage.setId("usage-001");
        testTokenUsage.setSessionId("session-001");
        testTokenUsage.setUserId("user-001");
        testTokenUsage.setTenantId("tenant-001");
        testTokenUsage.setModelId("model-001");
        testTokenUsage.setInputTokens(100);
        testTokenUsage.setOutputTokens(200);
        testTokenUsage.setTotalTokens(300);
        testTokenUsage.setCostAmount(new BigDecimal("0.0050"));
        testTokenUsage.setUsageDate(LocalDate.now());
    }

    @Test
    @DisplayName("分页查询Token用量")
    void testPageQuery() {
        // Given
        Page<TokenUsage> page = new Page<>(1, 10);
        when(tokenUsageMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        TokenUsage params = new TokenUsage();
        params.setUserId("user-001");
        params.setTenantId("tenant-001");

        // When
        IPage<TokenUsage> result = tokenUsageService.pageQuery(page, params);

        // Then
        assertNotNull(result);
        verify(tokenUsageMapper, times(1)).selectPage(any(IPage.class), any());
    }

    @Test
    @DisplayName("记录Token用量")
    void testRecordUsage() {
        // Given
        when(tokenUsageMapper.insert(any(TokenUsage.class))).thenReturn(1);

        // When
        tokenUsageService.recordUsage(
                "session-001", "user-001", "tenant-001", "model-001",
                100, 200, new BigDecimal("0.0050")
        );

        // Then
        verify(tokenUsageMapper, times(1)).insert(any(TokenUsage.class));
    }

    @Test
    @DisplayName("获取用户日用量统计")
    void testGetUserDailyUsage() {
        // Given
        LocalDate today = LocalDate.now();
        TokenUsage usage1 = new TokenUsage();
        usage1.setInputTokens(100);
        usage1.setOutputTokens(200);
        usage1.setTotalTokens(300);
        usage1.setCostAmount(new BigDecimal("0.0050"));

        TokenUsage usage2 = new TokenUsage();
        usage2.setInputTokens(50);
        usage2.setOutputTokens(100);
        usage2.setTotalTokens(150);
        usage2.setCostAmount(new BigDecimal("0.0025"));

        when(tokenUsageMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(usage1, usage2));

        // When
        Map<String, Object> result = tokenUsageService.getUserDailyUsage("user-001", today);

        // Then
        assertNotNull(result);
        assertEquals("user-001", result.get("userId"));
        assertEquals(150, result.get("inputTokens"));
        assertEquals(300, result.get("outputTokens"));
        assertEquals(450, result.get("totalTokens"));
        assertEquals(2, result.get("requestCount"));
    }

    @Test
    @DisplayName("获取用户日用量统计 - 无数据")
    void testGetUserDailyUsage_NoData() {
        // Given
        LocalDate today = LocalDate.now();
        when(tokenUsageMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of());

        // When
        Map<String, Object> result = tokenUsageService.getUserDailyUsage("user-001", today);

        // Then
        assertNotNull(result);
        assertEquals(0, result.get("inputTokens"));
        assertEquals(0, result.get("outputTokens"));
        assertEquals(0, result.get("totalTokens"));
        assertEquals(0, result.get("requestCount"));
    }

    @Test
    @DisplayName("获取租户月用量统计")
    void testGetTenantMonthlyUsage() {
        // Given
        TokenUsage usage1 = new TokenUsage();
        usage1.setInputTokens(1000);
        usage1.setOutputTokens(2000);
        usage1.setTotalTokens(3000);
        usage1.setCostAmount(new BigDecimal("0.0500"));

        TokenUsage usage2 = new TokenUsage();
        usage2.setInputTokens(500);
        usage2.setOutputTokens(1000);
        usage2.setTotalTokens(1500);
        usage2.setCostAmount(new BigDecimal("0.0250"));

        when(tokenUsageMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(usage1, usage2));

        // When
        Map<String, Object> result = tokenUsageService.getTenantMonthlyUsage("tenant-001", 2026, 3);

        // Then
        assertNotNull(result);
        assertEquals("tenant-001", result.get("tenantId"));
        assertEquals(2026, result.get("year"));
        assertEquals(3, result.get("month"));
        assertEquals(1500, result.get("inputTokens"));
        assertEquals(3000, result.get("outputTokens"));
        assertEquals(4500, result.get("totalTokens"));
        assertEquals(2, result.get("requestCount"));
    }

    @Test
    @DisplayName("获取租户月用量统计 - 无数据")
    void testGetTenantMonthlyUsage_NoData() {
        // Given
        when(tokenUsageMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of());

        // When
        Map<String, Object> result = tokenUsageService.getTenantMonthlyUsage("tenant-001", 2026, 3);

        // Then
        assertNotNull(result);
        assertEquals(0, result.get("inputTokens"));
        assertEquals(0, result.get("totalTokens"));
        assertEquals(BigDecimal.ZERO, result.get("totalCost"));
        assertEquals(0, result.get("requestCount"));
    }
}