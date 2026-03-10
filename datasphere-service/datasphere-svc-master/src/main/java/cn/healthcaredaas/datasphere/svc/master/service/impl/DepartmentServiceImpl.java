package cn.healthcaredaas.datasphere.svc.master.service.impl;

import cn.healthcaredaas.datasphere.svc.master.entity.Department;
import cn.healthcaredaas.datasphere.svc.master.mapper.DepartmentMapper;
import cn.healthcaredaas.datasphere.svc.master.service.DepartmentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 科室服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department>
        implements DepartmentService {

    @Override
    public IPage<Department> pageQuery(IPage<Department> page, Department params) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getDeptName())) {
            wrapper.like(Department::getDeptName, params.getDeptName());
        }

        if (StringUtils.isNotBlank(params.getOrgId())) {
            wrapper.eq(Department::getOrgId, params.getOrgId());
        }

        wrapper.orderByAsc(Department::getSortNo);

        return baseMapper.selectPage(page, wrapper);
    }
}
