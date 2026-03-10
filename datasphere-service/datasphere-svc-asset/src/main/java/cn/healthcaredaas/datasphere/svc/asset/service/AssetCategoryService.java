package cn.healthcaredaas.datasphere.svc.asset.service;

import cn.healthcaredaas.datasphere.svc.asset.entity.AssetCategory;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 资产分类服务接口
 *
 * @author chenpan
 */
public interface AssetCategoryService extends IService<AssetCategory> {

    IPage<AssetCategory> pageQuery(IPage<AssetCategory> page, AssetCategory params);

    /**
     * 获取分类树
     */
    List<AssetCategory> getCategoryTree();

    /**
     * 获取子分类
     */
    List<AssetCategory> getChildren(String parentId);
}
