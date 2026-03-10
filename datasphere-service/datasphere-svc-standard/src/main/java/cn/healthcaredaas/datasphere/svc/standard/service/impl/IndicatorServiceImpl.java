package cn.healthcaredaas.datasphere.svc.standard.service.impl;

import cn.healthcaredaas.datasphere.svc.standard.entity.Indicator;
import cn.healthcaredaas.datasphere.svc.standard.mapper.IndicatorMapper;
import cn.healthcaredaas.datasphere.svc.standard.service.IndicatorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 指标服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IndicatorServiceImpl extends ServiceImpl<IndicatorMapper, Indicator>
        implements IndicatorService {

    @Override
    public IPage<Indicator> pageQuery(IPage<Indicator> page, Indicator params) {
        LambdaQueryWrapper<Indicator> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getIndicatorName())) {
            wrapper.like(Indicator::getIndicatorName, params.getIndicatorName());
        }

        wrapper.orderByDesc(Indicator::getCreateTime);

        return baseMapper.selectPage(page, wrapper);
    }
}
