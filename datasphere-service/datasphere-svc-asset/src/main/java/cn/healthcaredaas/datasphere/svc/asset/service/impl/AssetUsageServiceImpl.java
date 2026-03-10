package cn.healthcaredaas.datasphere.svc.asset.service.impl;

import cn.healthcaredaas.datasphere.svc.asset.entity.AssetUsage;
import cn.healthcaredaas.datasphere.svc.asset.mapper.AssetUsageMapper;
import cn.healthcaredaas.datasphere.svc.asset.service.AssetUsageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产使用记录服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetUsageServiceImpl extends ServiceImpl<AssetUsageMapper, AssetUsage>
        implements AssetUsageService {

    @Override
    public IPage<AssetUsage> pageQuery(IPage<AssetUsage> page, AssetUsage params) {
        LambdaQueryWrapper<AssetUsage> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getAssetId())) {
            wrapper.eq(AssetUsage::getAssetId, params.getAssetId());
        }

        if (StringUtils.isNotBlank(params.getUserId())) {
            wrapper.eq(AssetUsage::getUserId, params.getUserId());
        }

        if (StringUtils.isNotBlank(params.getOperationType())) {
            wrapper.eq(AssetUsage::getOperationType, params.getOperationType());
        }

        wrapper.orderByDesc(AssetUsage::getAccessTime);

        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public void recordAccess(AssetUsage usage) {
        usage.setAccessTime(LocalDateTime.now());
        save(usage);
    }

    @Override
    public List<Map<String, Object>> getHotAssets(LocalDateTime startTime, LocalDateTime endTime, int limit) {
        return baseMapper.selectTopAccessedAssets(startTime, endTime, limit);
    }

    @Override
    public Map<String, Object> getAssetAccessStats(String assetId, LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> stats = new HashMap<>();

        LambdaQueryWrapper<AssetUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetUsage::getAssetId, assetId);
        if (startTime != null) {
            wrapper.ge(AssetUsage::getAccessTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(AssetUsage::getAccessTime, endTime);
        }

        long totalAccess = count(wrapper);
        stats.put("totalAccess", totalAccess);

        // 按操作类型统计
        wrapper.select(AssetUsage::getOperationType);
        List<AssetUsage> usages = list(wrapper);
        Map<String, Long> typeStats = new HashMap<>();
        for (AssetUsage usage : usages) {
            typeStats.merge(usage.getOperationType(), 1L, Long::sum);
        }
        stats.put("typeStats", typeStats);

        return stats;
    }
}
