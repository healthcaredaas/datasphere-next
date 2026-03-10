package cn.healthcaredaas.datasphere.svc.agent.service;

import cn.healthcaredaas.datasphere.svc.agent.entity.TokenUsage;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Token用量服务接口
 *
 * @author chenpan
 */
public interface TokenUsageService extends IService<TokenUsage> {

    /**
     * 分页查询用量
     */
    IPage<TokenUsage> pageQuery(IPage<TokenUsage> page, TokenUsage params);

    /**
     * 记录Token使用
     */
    void recordUsage(String sessionId, String userId, String tenantId, String modelId,
                     int inputTokens, int outputTokens, BigDecimal costAmount);

    /**
     * 获取用户日用量统计
     */
    Map<String, Object> getUserDailyUsage(String userId, LocalDate date);

    /**
     * 获取租户月用量统计
     */
    Map<String, Object> getTenantMonthlyUsage(String tenantId, int year, int month);
}