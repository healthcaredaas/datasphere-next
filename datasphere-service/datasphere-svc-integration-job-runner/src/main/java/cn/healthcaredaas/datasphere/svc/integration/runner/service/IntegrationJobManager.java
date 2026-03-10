package cn.healthcaredaas.datasphere.svc.integration.runner.service;

import cn.healthcaredaas.datasphere.api.integration.dto.DataJobDTO;
import cn.healthcaredaas.datasphere.api.integration.dto.DataJobExecuteDTO;
import cn.healthcaredaas.datasphere.api.integration.feign.IntegrationJobClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 集成作业管理服务
 * <p>
 * 通过Feign调用主服务，管理作业配置和执行状态
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IntegrationJobManager {

    private final IntegrationJobClient jobClient;

    /**
     * 获取作业配置
     *
     * @param jobId 作业ID
     * @return 配置内容
     */
    public String getJobConfig(String jobId) {
        try {
            return jobClient.getJobConfig(jobId);
        } catch (Exception e) {
            log.error("Failed to get job config, jobId: {}", jobId, e);
            throw new RuntimeException("获取作业配置失败", e);
        }
    }

    /**
     * 创建执行记录
     *
     * @param jobId 作业ID
     * @return 执行记录ID
     */
    public String createExecuteRecord(String jobId) {
        try {
            DataJobExecuteDTO execute = jobClient.createExecuteRecord(jobId);
            return execute != null ? execute.getId() : null;
        } catch (Exception e) {
            log.error("Failed to create execute record, jobId: {}", jobId, e);
            throw new RuntimeException("创建执行记录失败", e);
        }
    }

    /**
     * 更新执行状态为成功
     *
     * @param executeId 执行记录ID
     * @param readRows  读取行数
     * @param writeRows 写入行数
     */
    public void updateExecuteSuccess(String executeId, Long readRows, Long writeRows) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("readRows", readRows);
            params.put("writeRows", writeRows);
            jobClient.onJobSuccess(executeId, params);
            log.info("Updated execute status to success, executeId: {}", executeId);
        } catch (Exception e) {
            log.error("Failed to update execute success status, executeId: {}", executeId, e);
        }
    }

    /**
     * 更新执行状态为失败
     *
     * @param executeId 执行记录ID
     * @param errorMsg  错误信息
     */
    public void updateExecuteFailed(String executeId, String errorMsg) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("errorMsg", errorMsg);
            jobClient.onJobFailed(executeId, params);
            log.info("Updated execute status to failed, executeId: {}", executeId);
        } catch (Exception e) {
            log.error("Failed to update execute failed status, executeId: {}", executeId, e);
        }
    }

    /**
     * 更新作业状态
     *
     * @param jobId  作业ID
     * @param status 状态
     */
    public void updateJobStatus(String jobId, Integer status) {
        try {
            Map<String, Integer> params = new HashMap<>();
            params.put("status", status);
            jobClient.updateJobStatus(jobId, params);
            log.info("Updated job status, jobId: {}, status: {}", jobId, status);
        } catch (Exception e) {
            log.error("Failed to update job status, jobId: {}", jobId, e);
        }
    }
}
