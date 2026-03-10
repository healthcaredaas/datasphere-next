package cn.healthcaredaas.datasphere.svc.asset.service;

import cn.healthcaredaas.datasphere.svc.asset.entity.AssetTag;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 资产标签服务接口
 *
 * @author chenpan
 */
public interface AssetTagService extends IService<AssetTag> {

    IPage<AssetTag> pageQuery(IPage<AssetTag> page, AssetTag params);
}
