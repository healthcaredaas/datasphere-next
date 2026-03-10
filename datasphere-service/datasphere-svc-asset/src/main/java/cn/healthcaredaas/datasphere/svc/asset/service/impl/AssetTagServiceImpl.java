package cn.healthcaredaas.datasphere.svc.asset.service.impl;

import cn.healthcaredaas.datasphere.svc.asset.entity.AssetTag;
import cn.healthcaredaas.datasphere.svc.asset.mapper.AssetTagMapper;
import cn.healthcaredaas.datasphere.svc.asset.service.AssetTagService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 资产标签服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetTagServiceImpl extends ServiceImpl<AssetTagMapper, AssetTag>
        implements AssetTagService {

    @Override
    public IPage<AssetTag> pageQuery(IPage<AssetTag> page, AssetTag params) {
        LambdaQueryWrapper<AssetTag> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getTagName())) {
            wrapper.like(AssetTag::getTagName, params.getTagName());
        }

        if (params.getStatus() != null) {
            wrapper.eq(AssetTag::getStatus, params.getStatus());
        }

        wrapper.orderByDesc(AssetTag::getUsageCount);

        return baseMapper.selectPage(page, wrapper);
    }
}
