package cn.healthcaredaas.datasphere.svc.integration.service.impl;

import cn.healthcaredaas.datasphere.svc.integration.entity.DataJobExecute;
import cn.healthcaredaas.datasphere.svc.integration.mapper.DataJobExecuteMapper;
import cn.healthcaredaas.datasphere.svc.integration.service.DataJobExecuteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据作业执行记录服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataJobExecuteServiceImpl extends ServiceImpl<DataJobExecuteMapper, DataJobExecute>
        implements DataJobExecuteService {

    @Override
    public IPage<DataJobExecute> pageQuery(IPage<DataJobExecute> page, DataJobExecute params) {
        LambdaQueryWrapper<DataJobExecute> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getJobId())) {
            wrapper.eq(DataJobExecute::getJobId, params.getJobId());
        }

        if (params.getStatus() != null) {
            wrapper.eq(DataJobExecute::getStatus, params.getStatus());
        }

        wrapper.orderByDesc(DataJobExecute::getCreateTime);

        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<DataJobExecute> listByJob(String jobId) {
        return lambdaQuery()
                .eq(DataJobExecute::getJobId, jobId)
                .orderByDesc(DataJobExecute::getCreateTime)
                .list();
    }

    @Override
    public DataJobExecute getLatestExecute(String jobId) {
        return lambdaQuery()
                .eq(DataJobExecute::getJobId, jobId)
                .orderByDesc(DataJobExecute::getCreateTime)
                .last("LIMIT 1")
                .one();
    }
}
