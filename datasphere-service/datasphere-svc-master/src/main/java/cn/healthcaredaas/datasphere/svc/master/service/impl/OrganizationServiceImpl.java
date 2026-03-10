package cn.healthcaredaas.datasphere.svc.master.service.impl;

import cn.healthcaredaas.datasphere.svc.master.entity.Organization;
import cn.healthcaredaas.datasphere.svc.master.mapper.OrganizationMapper;
import cn.healthcaredaas.datasphere.svc.master.service.OrganizationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 组织机构服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper, Organization>
        implements OrganizationService {

    @Override
    public IPage<Organization> pageQuery(IPage<Organization> page, Organization params) {
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getOrgName())) {
            wrapper.like(Organization::getOrgName, params.getOrgName());
        }

        if (StringUtils.isNotBlank(params.getOrgCode())) {
            wrapper.like(Organization::getOrgCode, params.getOrgCode());
        }

        wrapper.orderByAsc(Organization::getSortNo);

        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Organization> listTree() {
        return lambdaQuery()
                .orderByAsc(Organization::getOrgLevel, Organization::getSortNo)
                .list();
    }
}
