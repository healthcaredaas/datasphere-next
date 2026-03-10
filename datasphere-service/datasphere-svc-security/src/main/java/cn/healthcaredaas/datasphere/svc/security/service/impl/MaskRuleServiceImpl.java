package cn.healthcaredaas.datasphere.svc.security.service.impl;

import cn.healthcaredaas.datasphere.svc.security.entity.MaskRule;
import cn.healthcaredaas.datasphere.svc.security.mapper.MaskRuleMapper;
import cn.healthcaredaas.datasphere.svc.security.service.MaskRuleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 脱敏规则服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MaskRuleServiceImpl extends ServiceImpl<MaskRuleMapper, MaskRule>
        implements MaskRuleService {

    @Override
    public IPage<MaskRule> pageQuery(IPage<MaskRule> page, MaskRule params) {
        LambdaQueryWrapper<MaskRule> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getRuleName())) {
            wrapper.like(MaskRule::getRuleName, params.getRuleName());
        }

        if (params.getStatus() != null) {
            wrapper.eq(MaskRule::getStatus, params.getStatus());
        }

        wrapper.orderByDesc(MaskRule::getCreateTime);

        return baseMapper.selectPage(page, wrapper);
    }
}
