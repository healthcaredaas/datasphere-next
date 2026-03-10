package cn.healthcaredaas.datasphere.svc.integration.service.impl;

import cn.healthcaredaas.datasphere.svc.integration.entity.DataJob;
import cn.healthcaredaas.datasphere.svc.integration.entity.DataJobExecute;
import cn.healthcaredaas.datasphere.svc.integration.mapper.DataJobExecuteMapper;
import cn.healthcaredaas.datasphere.svc.integration.mapper.DataJobMapper;
import cn.healthcaredaas.datasphere.svc.integration.service.JobExecuteCallbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 作业执行回调服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobExecuteCallbackServiceImpl implements JobExecuteCallbackService {

    private final DataJobExecuteMapper dataJobExecuteMapper;
    private final DataJobMapper dataJobMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onJobSuccess(String executeId, Long readRows, Long writeRows) {
        log.info("Job execute success callback, executeId: {}, readRows: {}, writeRows: {}",
                executeId, readRows, writeRows);

        DataJobExecute execute = dataJobExecuteMapper.selectById(executeId);
        if (execute == null) {
            log.warn("Execute record not found: {}", executeId);
            return;
        }

        // 更新执行记录
        execute.setStatus(1); // 成功
        execute.setEndTime(LocalDateTime.now());
        execute.setReadRows(readRows != null ? readRows : 0);
        execute.setWriteRows(writeRows != null ? writeRows : 0);
        execute.setDuration(calculateDuration(execute));

        dataJobExecuteMapper.updateById(execute);

        // 更新作业状态
        updateJobStatusToCompleted(execute.getJobId(), 1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onJobFailed(String executeId, String errorMsg) {
        log.error("Job execute failed callback, executeId: {}, error: {}", executeId, errorMsg);

        DataJobExecute execute = dataJobExecuteMapper.selectById(executeId);
        if (execute == null) {
            log.warn("Execute record not found: {}", executeId);
            return;
        }

        // 更新执行记录
        execute.setStatus(2); // 失败
        execute.setEndTime(LocalDateTime.now());
        execute.setErrorMsg(errorMsg);
        execute.setDuration(calculateDuration(execute));

        dataJobExecuteMapper.updateById(execute);

        // 更新作业状态
        updateJobStatusToCompleted(execute.getJobId(), 2);
    }

    @Override
    public void updateProgress(String executeId, Long readRows, Long writeRows) {
        DataJobExecute execute = new DataJobExecute();
        execute.setId(executeId);
        execute.setReadRows(readRows != null ? readRows : 0);
        execute.setWriteRows(writeRows != null ? writeRows : 0);

        dataJobExecuteMapper.updateById(execute);
    }

    @Override
    public String getJobConfig(String jobId) {
        DataJob job = dataJobMapper.selectById(jobId);
        if (job == null) {
            return null;
        }
        return job.getConfigContent();
    }

    @Override
    public void updateJobStatus(String jobId, Integer status) {
        DataJob job = new DataJob();
        job.setId(jobId);
        job.setStatus(status);
        dataJobMapper.updateById(job);
    }

    /**
     * 计算执行耗时
     */
    private Long calculateDuration(DataJobExecute execute) {
        if (execute.getStartTime() == null || execute.getEndTime() == null) {
            return 0L;
        }
        return java.time.Duration.between(execute.getStartTime(), execute.getEndTime()).toMillis();
    }

    /**
     * 更新作业状态为已完成
     */
    private void updateJobStatusToCompleted(String jobId, Integer lastRunStatus) {
        DataJob job = new DataJob();
        job.setId(jobId);
        job.setStatus(3); // 已停止/已完成
        job.setLastRunStatus(lastRunStatus);
        dataJobMapper.updateById(job);
    }
}
