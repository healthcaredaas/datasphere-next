package cn.healthcaredaas.datasphere.hie.gateway.service.impl;

import cn.healthcaredaas.datasphere.hie.gateway.entity.HieService;
import cn.healthcaredaas.datasphere.hie.gateway.mapper.HieServiceMapper;
import cn.healthcaredaas.datasphere.hie.gateway.service.HieServiceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 交互服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HieServiceServiceImpl extends ServiceImpl<HieServiceMapper, HieService>
        implements HieServiceService {

    @Override
    public IPage<HieService> pageQuery(IPage<HieService> page, HieService params) {
        LambdaQueryWrapper<HieService> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getServiceName())) {
            wrapper.like(HieService::getServiceName, params.getServiceName());
        }

        if (StringUtils.isNotBlank(params.getServiceType())) {
            wrapper.eq(HieService::getServiceType, params.getServiceType());
        }

        wrapper.orderByDesc(HieService::getCreateTime);

        return baseMapper.selectPage(page, wrapper);
    }
}
