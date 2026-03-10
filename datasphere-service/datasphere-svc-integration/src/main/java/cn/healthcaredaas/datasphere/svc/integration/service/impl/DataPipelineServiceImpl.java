package cn.healthcaredaas.datasphere.svc.integration.service.impl;

import cn.healthcaredaas.datasphere.svc.integration.entity.DataPipeline;
import cn.healthcaredaas.datasphere.svc.integration.mapper.DataPipelineMapper;
import cn.healthcaredaas.datasphere.svc.integration.service.DataPipelineService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据管道服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataPipelineServiceImpl extends ServiceImpl<DataPipelineMapper, DataPipeline>
        implements DataPipelineService {

    @Override
    public IPage<DataPipeline> pageQuery(IPage<DataPipeline> page, DataPipeline params) {
        LambdaQueryWrapper<DataPipeline> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getPipelineName())) {
            wrapper.like(DataPipeline::getPipelineName, params.getPipelineName());
        }

        if (StringUtils.isNotBlank(params.getProjectId())) {
            wrapper.eq(DataPipeline::getProjectId, params.getProjectId());
        }

        if (StringUtils.isNotBlank(params.getEngineType())) {
            wrapper.eq(DataPipeline::getEngineType, params.getEngineType());
        }

        if (params.getStatus() != null) {
            wrapper.eq(DataPipeline::getStatus, params.getStatus());
        }

        wrapper.orderByDesc(DataPipeline::getCreateTime);

        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<DataPipeline> listByProject(String projectId) {
        return lambdaQuery()
                .eq(DataPipeline::getProjectId, projectId)
                .orderByDesc(DataPipeline::getCreateTime)
                .list();
    }
}
