package cn.healthcaredaas.datasphere.svc.integration.service.impl;

import cn.healthcaredaas.datasphere.svc.integration.entity.DataJob;
import cn.healthcaredaas.datasphere.svc.integration.entity.DataJobExecute;
import cn.healthcaredaas.datasphere.svc.integration.entity.DataPipeline;
import cn.healthcaredaas.datasphere.svc.integration.entity.PipelineConnector;
import cn.healthcaredaas.datasphere.svc.integration.mapper.DataJobExecuteMapper;
import cn.healthcaredaas.datasphere.svc.integration.mapper.DataJobMapper;
import cn.healthcaredaas.datasphere.svc.integration.mapper.DataPipelineMapper;
import cn.healthcaredaas.datasphere.svc.integration.mapper.PipelineConnectorMapper;
import cn.healthcaredaas.datasphere.svc.integration.seatunnel.SeaTunnelConfigGenerator;
import cn.healthcaredaas.datasphere.svc.integration.service.DataJobService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据作业服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataJobServiceImpl extends ServiceImpl<DataJobMapper, DataJob>
        implements DataJobService {

    private final DataJobExecuteMapper dataJobExecuteMapper;
    private final DataPipelineMapper dataPipelineMapper;
    private final PipelineConnectorMapper pipelineConnectorMapper;
    private final SeaTunnelConfigGenerator configGenerator;

    @Override
    public IPage<DataJob> pageQuery(IPage<DataJob> page, DataJob params) {
        LambdaQueryWrapper<DataJob> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getJobName())) {
            wrapper.like(DataJob::getJobName, params.getJobName());
        }

        if (StringUtils.isNotBlank(params.getPipelineId())) {
            wrapper.eq(DataJob::getPipelineId, params.getPipelineId());
        }

        if (params.getStatus() != null) {
            wrapper.eq(DataJob::getStatus, params.getStatus());
        }

        wrapper.orderByDesc(DataJob::getCreateTime);

        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<DataJob> listByPipeline(String pipelineId) {
        return lambdaQuery()
                .eq(DataJob::getPipelineId, pipelineId)
                .orderByDesc(DataJob::getCreateTime)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishJob(String jobId) {
        DataJob job = getById(jobId);
        if (job == null) {
            throw new RuntimeException("作业不存在");
        }

        // 获取管道信息
        DataPipeline pipeline = dataPipelineMapper.selectById(job.getPipelineId());
        if (pipeline == null) {
            throw new RuntimeException("管道不存在");
        }

        // 获取连接器列表
        List<PipelineConnector> connectors = pipelineConnectorMapper.selectList(
                new LambdaQueryWrapper<PipelineConnector>()
                        .eq(PipelineConnector::getPipelineId, job.getPipelineId())
                        .orderByAsc(PipelineConnector::getOrderNo)
        );

        if (connectors.isEmpty()) {
            throw new RuntimeException("管道连接器为空");
        }

        // 生成SeaTunnel配置
        String configContent = configGenerator.generateZetaConfig(pipeline.getPipelineName(), connectors);
        job.setConfigContent(configContent);
        job.setStatus(1); // 已发布

        log.info("Published job {} with config:\n{}", jobId, configContent);
        return updateById(job);
    }

    @Override
    public DataJobExecute startJob(String jobId) {
        DataJob job = getById(jobId);
        if (job == null) {
            throw new RuntimeException("作业不存在");
        }

        if (job.getStatus() == 0) {
            throw new RuntimeException("作业未发布，请先发布");
        }

        // 创建执行记录
        DataJobExecute execute = new DataJobExecute();
        execute.setJobId(jobId);
        execute.setPipelineId(job.getPipelineId());
        execute.setStartTime(LocalDateTime.now());
        execute.setStatus(0); // 运行中
        execute.setTriggerType(0); // 手动触发
        dataJobExecuteMapper.insert(execute);

        // 更新作业状态
        job.setStatus(2); // 运行中
        job.setLastRunTime(LocalDateTime.now());
        updateById(job);

        log.info("Started job {}, execute record: {}", jobId, execute.getId());
        return execute;
    }

    @Override
    public boolean stopJob(String jobId) {
        DataJob job = getById(jobId);
        if (job == null) {
            return false;
        }

        // 更新作业状态为已停止
        job.setStatus(3);
        boolean updated = updateById(job);

        log.info("Stopped job {}", jobId);
        return updated;
    }

    @Override
    public String getJobConfig(String jobId) {
        DataJob job = getById(jobId);
        if (job == null) {
            throw new RuntimeException("作业不存在");
        }
        return job.getConfigContent();
    }

    @Override
    public boolean updateStatus(String jobId, Integer status) {
        DataJob job = new DataJob();
        job.setId(jobId);
        job.setStatus(status);
        return updateById(job);
    }
}
