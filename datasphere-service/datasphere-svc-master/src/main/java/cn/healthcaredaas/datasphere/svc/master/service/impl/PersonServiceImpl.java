package cn.healthcaredaas.datasphere.svc.master.service.impl;

import cn.healthcaredaas.datasphere.svc.master.entity.Person;
import cn.healthcaredaas.datasphere.svc.master.mapper.PersonMapper;
import cn.healthcaredaas.datasphere.svc.master.service.PersonService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 人员信息服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonServiceImpl extends ServiceImpl<PersonMapper, Person>
        implements PersonService {

    @Override
    public IPage<Person> pageQuery(IPage<Person> page, Person params) {
        LambdaQueryWrapper<Person> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getName())) {
            wrapper.like(Person::getName, params.getName());
        }

        if (StringUtils.isNotBlank(params.getOrgId())) {
            wrapper.eq(Person::getOrgId, params.getOrgId());
        }

        wrapper.orderByDesc(Person::getCreateTime);

        return baseMapper.selectPage(page, wrapper);
    }
}
