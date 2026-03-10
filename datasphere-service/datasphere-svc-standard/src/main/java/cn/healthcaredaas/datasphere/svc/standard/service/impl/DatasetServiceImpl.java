package cn.healthcaredaas.datasphere.svc.standard.service.impl;

import cn.healthcaredaas.datasphere.svc.standard.entity.Dataset;
import cn.healthcaredaas.datasphere.svc.standard.mapper.DatasetMapper;
import cn.healthcaredaas.datasphere.svc.standard.service.DatasetService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 数据集服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatasetServiceImpl extends ServiceImpl<DatasetMapper, Dataset>
        implements DatasetService {

    @Override
    public IPage<Dataset> pageQuery(IPage<Dataset> page, Dataset params) {
        LambdaQueryWrapper<Dataset> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getDatasetName())) {
            wrapper.like(Dataset::getDatasetName, params.getDatasetName());
        }

        if (StringUtils.isNotBlank(params.getDatasetType())) {
            wrapper.eq(Dataset::getDatasetType, params.getDatasetType());
        }

        wrapper.orderByDesc(Dataset::getCreateTime);

        return baseMapper.selectPage(page, wrapper);
    }
}
