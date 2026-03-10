package cn.healthcaredaas.datasphere.svc.integration.service.impl;

import cn.healthcaredaas.datasphere.svc.integration.entity.DataProject;
import cn.healthcaredaas.datasphere.svc.integration.mapper.DataProjectMapper;
import cn.healthcaredaas.datasphere.svc.integration.service.DataProjectService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 数据项目服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataProjectServiceImpl extends ServiceImpl<DataProjectMapper, DataProject>
        implements DataProjectService {

    @Override
    public IPage<DataProject> pageQuery(IPage<DataProject> page, DataProject params) {
        LambdaQueryWrapper<DataProject> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getProjectName())) {
            wrapper.like(DataProject::getProjectName, params.getProjectName());
        }

        if (StringUtils.isNotBlank(params.getProjectCode())) {
            wrapper.like(DataProject::getProjectCode, params.getProjectCode());
        }

        wrapper.orderByDesc(DataProject::getCreateTime);

        return baseMapper.selectPage(page, wrapper);
    }
}
