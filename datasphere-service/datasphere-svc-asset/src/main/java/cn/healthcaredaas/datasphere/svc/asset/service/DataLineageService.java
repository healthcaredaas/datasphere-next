package cn.healthcaredaas.datasphere.svc.asset.service;

import cn.healthcaredaas.datasphere.svc.asset.entity.DataLineage;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 数据血缘服务接口
 *
 * @author chenpan
 */
public interface DataLineageService extends IService<DataLineage> {

    IPage<DataLineage> pageQuery(IPage<DataLineage> page, DataLineage params);

    /**
     * 获取资产的上游血缘
     */
    List<DataLineage> getUpstreamLineage(String assetId);

    /**
     * 获取资产的下游血缘
     */
    List<DataLineage> getDownstreamLineage(String assetId);

    /**
     * 获取资产的血缘图
     */
    Map<String, Object> getLineageGraph(String assetId);
}
