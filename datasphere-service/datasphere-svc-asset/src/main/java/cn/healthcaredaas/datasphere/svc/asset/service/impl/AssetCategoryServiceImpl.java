package cn.healthcaredaas.datasphere.svc.asset.service.impl;

import cn.healthcaredaas.datasphere.svc.asset.entity.AssetCategory;
import cn.healthcaredaas.datasphere.svc.asset.mapper.AssetCategoryMapper;
import cn.healthcaredaas.datasphere.svc.asset.service.AssetCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资产分类服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetCategoryServiceImpl extends ServiceImpl<AssetCategoryMapper, AssetCategory>
        implements AssetCategoryService {

    @Override
    public IPage<AssetCategory> pageQuery(IPage<AssetCategory> page, AssetCategory params) {
        LambdaQueryWrapper<AssetCategory> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getCategoryName())) {
            wrapper.like(AssetCategory::getCategoryName, params.getCategoryName());
        }

        if (params.getStatus() != null) {
            wrapper.eq(AssetCategory::getStatus, params.getStatus());
        }

        wrapper.orderByAsc(AssetCategory::getLevel, AssetCategory::getSortNo);

        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<AssetCategory> getCategoryTree() {
        LambdaQueryWrapper<AssetCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetCategory::getStatus, 1);
        wrapper.orderByAsc(AssetCategory::getLevel, AssetCategory::getSortNo);
        List<AssetCategory> allCategories = baseMapper.selectList(wrapper);

        // 构建树形结构
        return buildTree(allCategories, "0");
    }

    @Override
    public List<AssetCategory> getChildren(String parentId) {
        LambdaQueryWrapper<AssetCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetCategory::getParentId, parentId);
        wrapper.eq(AssetCategory::getStatus, 1);
        wrapper.orderByAsc(AssetCategory::getSortNo);
        return baseMapper.selectList(wrapper);
    }

    private List<AssetCategory> buildTree(List<AssetCategory> categories, String parentId) {
        List<AssetCategory> tree = new ArrayList<>();
        for (AssetCategory category : categories) {
            if (parentId.equals(category.getParentId())) {
                tree.add(category);
            }
        }
        return tree;
    }
}
