package cn.healthcaredaas.datasphere.svc.agent.service.impl;

import cn.healthcaredaas.datasphere.svc.agent.entity.TokenUsage;
import cn.healthcaredaas.datasphere.svc.agent.mapper.TokenUsageMapper;
import cn.healthcaredaas.datasphere.svc.agent.service.TokenUsageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Token用量服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenUsageServiceImpl extends ServiceImpl<TokenUsageMapper, TokenUsage>
        implements TokenUsageService {

    @Override
    public IPage<TokenUsage> pageQuery(IPage<TokenUsage> page, TokenUsage params) {
        LambdaQueryWrapper<TokenUsage> wrapper = new LambdaQueryWrapper<>();

        if (params.getSessionId() != null) {
            wrapper.eq(TokenUsage::getSessionId, params.getSessionId());
        }

        if (params.getUserId() != null) {
            wrapper.eq(TokenUsage::getUserId, params.getUserId());
        }

        if (params.getTenantId() != null) {
            wrapper.eq(TokenUsage::getTenantId, params.getTenantId());
        }

        if (params.getModelId() != null) {
            wrapper.eq(TokenUsage::getModelId, params.getModelId());
        }

        if (params.getUsageDate() != null) {
            wrapper.eq(TokenUsage::getUsageDate, params.getUsageDate());
        }

        wrapper.orderByDesc(TokenUsage::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public void recordUsage(String sessionId, String userId, String tenantId, String modelId,
                            int inputTokens, int outputTokens, BigDecimal costAmount) {
        TokenUsage usage = new TokenUsage();
        usage.setSessionId(sessionId);
        usage.setUserId(userId);
        usage.setTenantId(tenantId);
        usage.setModelId(modelId);
        usage.setInputTokens(inputTokens);
        usage.setOutputTokens(outputTokens);
        usage.setTotalTokens(inputTokens + outputTokens);
        usage.setCostAmount(costAmount);
        usage.setUsageDate(LocalDate.now());
        save(usage);
    }

    @Override
    public Map<String, Object> getUserDailyUsage(String userId, LocalDate date) {
        LambdaQueryWrapper<TokenUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TokenUsage::getUserId, userId)
                .eq(TokenUsage::getUsageDate, date);

        List<TokenUsage> usages = baseMapper.selectList(wrapper);

        int totalInput = usages.stream().mapToInt(TokenUsage::getInputTokens).sum();
        int totalOutput = usages.stream().mapToInt(TokenUsage::getOutputTokens).sum();
        int totalTokens = usages.stream().mapToInt(TokenUsage::getTotalTokens).sum();
        BigDecimal totalCost = usages.stream()
                .map(TokenUsage::getCostAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("date", date);
        result.put("inputTokens", totalInput);
        result.put("outputTokens", totalOutput);
        result.put("totalTokens", totalTokens);
        result.put("totalCost", totalCost);
        result.put("requestCount", usages.size());
        return result;
    }

    @Override
    public Map<String, Object> getTenantMonthlyUsage(String tenantId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        LambdaQueryWrapper<TokenUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TokenUsage::getTenantId, tenantId)
                .ge(TokenUsage::getUsageDate, startDate)
                .le(TokenUsage::getUsageDate, endDate);

        List<TokenUsage> usages = baseMapper.selectList(wrapper);

        int totalInput = usages.stream().mapToInt(TokenUsage::getInputTokens).sum();
        int totalOutput = usages.stream().mapToInt(TokenUsage::getOutputTokens).sum();
        int totalTokens = usages.stream().mapToInt(TokenUsage::getTotalTokens).sum();
        BigDecimal totalCost = usages.stream()
                .map(TokenUsage::getCostAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> result = new HashMap<>();
        result.put("tenantId", tenantId);
        result.put("year", year);
        result.put("month", month);
        result.put("inputTokens", totalInput);
        result.put("outputTokens", totalOutput);
        result.put("totalTokens", totalTokens);
        result.put("totalCost", totalCost);
        result.put("requestCount", usages.size());
        return result;
    }
}