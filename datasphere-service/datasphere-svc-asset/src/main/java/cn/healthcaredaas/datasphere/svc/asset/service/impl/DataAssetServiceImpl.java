package cn.healthcaredaas.datasphere.svc.asset.service.impl;

import cn.healthcaredaas.datasphere.svc.asset.entity.DataAsset;
import cn.healthcaredaas.datasphere.svc.asset.mapper.DataAssetMapper;
import cn.healthcaredaas.datasphere.svc.asset.service.DataAssetService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 数据资产服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataAssetServiceImpl extends ServiceImpl<DataAssetMapper, DataAsset>
        implements DataAssetService {

    @Override
    public IPage<DataAsset> pageQuery(IPage<DataAsset> page, DataAsset params) {
        LambdaQueryWrapper<DataAsset> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getAssetName())) {
            wrapper.like(DataAsset::getAssetName, params.getAssetName());
        }

        if (StringUtils.isNotBlank(params.getAssetType())) {
            wrapper.eq(DataAsset::getAssetType, params.getAssetType());
        }

        wrapper.orderByDesc(DataAsset::getCreateTime);

        return baseMapper.selectPage(page, wrapper);
    }
}
