package cn.healthcaredaas.datasphere.svc.integration.runner.processor;

import cn.healthcaredaas.datasphere.svc.integration.runner.service.IntegrationJobManager;
import cn.healthcaredaas.datasphere.svc.integration.runner.service.SeaTunnelClientService;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

/**
 * SeaTunnel批处理作业处理器 (PowerJob)
 *
 * @author chenpan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SeaTunnelBatchProcessor implements BasicProcessor {

    private final SeaTunnelClientService seaTunnelClientService;
    private final IntegrationJobManager jobManager;

    @Override
    public ProcessResult process(TaskContext context) {
        String jobParams = context.getJobParams();
        long instanceId = context.getInstanceId();

        log.info("[SeaTunnel Batch] Start processing job, instanceId: {}, params length: {}",
                instanceId, jobParams != null ? jobParams.length() : 0);

        if (jobParams == null || jobParams.trim().isEmpty()) {
            log.error("[SeaTunnel Batch] Job params is empty");
            return new ProcessResult(false, "Job params is empty");
        }

        // 解析参数
        String jobId;
        String executeId;

        try {
            JSONObject params = JSONObject.parseObject(jobParams);
            jobId = params.getString("jobId");
            executeId = params.getString("executeId");

            if (jobId == null) {
                log.error("[SeaTunnel Batch] Missing required param: jobId");
                return new ProcessResult(false, "Missing required param: jobId");
            }
        } catch (Exception e) {
            log.error("[SeaTunnel Batch] Failed to parse job params", e);
            return new ProcessResult(false, "Invalid job params: " + e.getMessage());
        }

        log.info("[SeaTunnel Batch] Processing job, jobId: {}, executeId: {}", jobId, executeId);

        try {
            // 使用SeaTunnel Client执行作业
            // 如果提供了executeId，则使用它；否则执行作业但不更新执行记录
            boolean success;
            if (executeId != null && !executeId.isEmpty()) {
                success = seaTunnelClientService.executeJob(jobId, executeId);
            } else {
                // 仅执行，不更新执行记录
                success = seaTunnelClientService.executeJobWithoutCallback(jobId);
            }

            if (success) {
                log.info("[SeaTunnel Batch] Job completed successfully, jobId: {}", jobId);
                return new ProcessResult(true, "Job executed successfully");
            } else {
                log.error("[SeaTunnel Batch] Job execution failed, jobId: {}", jobId);
                return new ProcessResult(false, "Job execution failed");
            }
        } catch (Exception e) {
            log.error("[SeaTunnel Batch] Job execution error, jobId: {}", jobId, e);
            return new ProcessResult(false, "Job execution error: " + e.getMessage());
        }
    }
}
