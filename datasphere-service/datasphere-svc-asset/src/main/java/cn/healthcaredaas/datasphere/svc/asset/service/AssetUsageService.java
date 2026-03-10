package cn.healthcaredaas.datasphere.svc.asset.service;

import cn.healthcaredaas.datasphere.svc.asset.entity.AssetUsage;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 资产使用记录服务接口
 *
 * @author chenpan
 */
public interface AssetUsageService extends IService<AssetUsage> {

    IPage<AssetUsage> pageQuery(IPage<AssetUsage> page, AssetUsage params);

    /**
     * 记录资产访问
     */
    void recordAccess(AssetUsage usage);

    /**
     * 获取热门资产
     */
    List<Map<String, Object>> getHotAssets(LocalDateTime startTime, LocalDateTime endTime, int limit);

    /**
     * 获取资产访问统计
     */
    Map<String, Object> getAssetAccessStats(String assetId, LocalDateTime startTime, LocalDateTime endTime);
}
