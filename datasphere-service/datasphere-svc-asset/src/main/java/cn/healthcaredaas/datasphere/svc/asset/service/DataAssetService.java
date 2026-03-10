package cn.healthcaredaas.datasphere.svc.asset.service;

import cn.healthcaredaas.datasphere.svc.asset.entity.DataAsset;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 数据资产服务接口
 *
 * @author chenpan
 */
public interface DataAssetService extends IService<DataAsset> {

    IPage<DataAsset> pageQuery(IPage<DataAsset> page, DataAsset params);
}
