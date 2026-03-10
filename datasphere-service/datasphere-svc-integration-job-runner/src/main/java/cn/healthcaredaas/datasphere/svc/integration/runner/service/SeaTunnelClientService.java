package cn.healthcaredaas.datasphere.svc.integration.runner.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.seatunnel.common.config.Common;
import org.apache.seatunnel.common.config.DeployMode;
import org.apache.seatunnel.engine.client.SeaTunnelClient;
import org.apache.seatunnel.engine.client.job.ClientJobExecutionEnvironment;
import org.apache.seatunnel.engine.client.job.ClientJobProxy;
import org.apache.seatunnel.engine.common.config.ConfigProvider;
import org.apache.seatunnel.engine.common.config.JobConfig;
import org.apache.seatunnel.engine.common.config.SeaTunnelConfig;
import org.apache.seatunnel.engine.common.config.YamlSeaTunnelConfigBuilder;
import org.apache.seatunnel.engine.core.job.JobResult;
import org.apache.seatunnel.engine.core.job.JobStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.apache.seatunnel.core.starter.utils.FileUtils.checkConfigExist;

/**
 * SeaTunnel Client 服务
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeaTunnelClientService {

    private final IntegrationJobManager jobManager;
    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        executorService = Executors.newCachedThreadPool();
        log.info("SeaTunnelClientService initialized");
    }

    @PreDestroy
    public void destroy() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    /**
     * 执行SeaTunnel作业（从主服务获取配置）
     *
     * @param jobId     作业ID
     * @param executeId 执行记录ID
     * @return 是否成功
     */
    public boolean executeJob(String jobId, String executeId) {
        // 从主服务获取配置
        String configContent = jobManager.getJobConfig(jobId);
        if (configContent == null || configContent.isEmpty()) {
            log.error("[SeaTunnel Client] Job config not found, jobId: {}", jobId);
            jobManager.updateExecuteFailed(executeId, "Job config not found");
            return false;
        }

        return executeJobWithConfig(jobId, executeId, configContent);
    }

    /**
     * 执行SeaTunnel作业（使用提供的配置）
     *
     * @param jobId         作业ID
     * @param executeId     执行记录ID
     * @param configContent SeaTunnel配置内容
     * @return 是否成功
     */
    public boolean executeJobWithConfig(String jobId, String executeId, String configContent) {
        log.info("[SeaTunnel Client] Executing job, jobId: {}, executeId: {}", jobId, executeId);

        // 创建临时配置文件
        String configFileName = "seatunnel_job_" + jobId + "_" + UUID.randomUUID().toString().substring(0, 8) + ".conf";
        File configFile = new File(System.getProperty("java.io.tmpdir"), configFileName);

        try {
            // 写入配置文件
            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write(configContent);
            }
            log.info("[SeaTunnel Client] Config file created: {}", configFile.getAbsolutePath());

            // 设置部署模式为CLIENT
            Common.setDeployMode(DeployMode.CLIENT);

            // 创建SeaTunnel配置
            SeaTunnelConfig seaTunnelConfig = new YamlSeaTunnelConfigBuilder().build();

            // 创建作业配置
            JobConfig jobConfig = new JobConfig();
            jobConfig.setName("seatunnel-job-" + jobId);

            // 创建Client配置
            org.apache.seatunnel.engine.common.config.ConfigProvider.locateAndGetClientConfig();
            com.hazelcast.client.config.ClientConfig clientConfig = ConfigProvider.locateAndGetClientConfig();

            // 创建SeaTunnel客户端
            try (SeaTunnelClient seaTunnelClient = new SeaTunnelClient(clientConfig)) {
                // 检查配置文件
                checkConfigExist(Path.of(configFile.getAbsolutePath()));

                // 创建执行环境
                ClientJobExecutionEnvironment jobExecutionEnv =
                        seaTunnelClient.createExecutionContext(configFile.getAbsolutePath(), jobConfig, seaTunnelConfig);

                // 提交并执行作业
                ClientJobProxy clientJobProxy = jobExecutionEnv.execute();
                Long engineJobId = clientJobProxy.getJobId();

                log.info("[SeaTunnel Client] Job submitted, jobId: {}, engineJobId: {}", jobId, engineJobId);

                // 异步等待作业完成
                CompletableFuture<JobResult> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return clientJobProxy.waitForJobCompleteV2();
                    } catch (Exception e) {
                        log.error("[SeaTunnel Client] Job execution error, engineJobId: {}", engineJobId, e);
                        throw new RuntimeException(e);
                    }
                }, executorService);

                // 等待作业完成
                JobResult jobResult = future.get();
                JobStatus status = jobResult.getStatus();

                log.info("[SeaTunnel Client] Job completed, jobId: {}, engineJobId: {}, status: {}",
                        jobId, engineJobId, status);

                // 获取作业指标
                long readRows = 0;
                long writeRows = 0;
                try {
                    var metrics = seaTunnelClient.getJobMetricsSummary(engineJobId);
                    if (metrics != null) {
                        readRows = metrics.getSourceRecords();
                        writeRows = metrics.getSinkRecords();
                    }
                } catch (Exception e) {
                    log.warn("Failed to get job metrics, engineJobId: {}", engineJobId);
                }

                // 更新执行状态
                boolean success = status == JobStatus.FINISHED;
                if (success) {
                    jobManager.updateExecuteSuccess(executeId, readRows, writeRows);
                } else {
                    jobManager.updateExecuteFailed(executeId, "Job status: " + status);
                }

                // 更新作业状态为已完成
                jobManager.updateJobStatus(jobId, 3);

                return success;
            }

        } catch (Exception e) {
            log.error("[SeaTunnel Client] Job execution failed, jobId: {}, executeId: {}", jobId, executeId, e);
            // 更新执行状态为失败
            jobManager.updateExecuteFailed(executeId, e.getMessage());
            // 更新作业状态
            jobManager.updateJobStatus(jobId, 3);
            return false;
        } finally {
            // 清理临时文件
            if (configFile.exists()) {
                boolean deleted = configFile.delete();
                log.info("[SeaTunnel Client] Config file deleted: {}, success: {}", configFile.getAbsolutePath(), deleted);
            }
        }
    }

    /**
     * 执行SeaTunnel作业（无回调，仅执行）
     *
     * @param jobId 作业ID
     * @return 是否成功
     */
    public boolean executeJobWithoutCallback(String jobId) {
        // 从主服务获取配置
        String configContent = jobManager.getJobConfig(jobId);
        if (configContent == null || configContent.isEmpty()) {
            log.error("[SeaTunnel Client] Job config not found, jobId: {}", jobId);
            return false;
        }

        return executeJobInternal(jobId, configContent);
    }

    /**
     * 内部执行方法
     */
    private boolean executeJobInternal(String jobId, String configContent) {
        log.info("[SeaTunnel Client] Executing job internally, jobId: {}", jobId);

        // 创建临时配置文件
        String configFileName = "seatunnel_job_" + jobId + "_" + UUID.randomUUID().toString().substring(0, 8) + ".conf";
        File configFile = new File(System.getProperty("java.io.tmpdir"), configFileName);

        try {
            // 写入配置文件
            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write(configContent);
            }
            log.info("[SeaTunnel Client] Config file created: {}", configFile.getAbsolutePath());

            // 设置部署模式为CLIENT
            Common.setDeployMode(DeployMode.CLIENT);

            // 创建SeaTunnel配置
            SeaTunnelConfig seaTunnelConfig = new YamlSeaTunnelConfigBuilder().build();

            // 创建作业配置
            JobConfig jobConfig = new JobConfig();
            jobConfig.setName("seatunnel-job-" + jobId);

            // 创建Client配置
            org.apache.seatunnel.engine.common.config.ConfigProvider.locateAndGetClientConfig();
            com.hazelcast.client.config.ClientConfig clientConfig = ConfigProvider.locateAndGetClientConfig();

            // 创建SeaTunnel客户端
            try (SeaTunnelClient seaTunnelClient = new SeaTunnelClient(clientConfig)) {
                // 检查配置文件
                checkConfigExist(Path.of(configFile.getAbsolutePath()));

                // 创建执行环境
                ClientJobExecutionEnvironment jobExecutionEnv =
                        seaTunnelClient.createExecutionContext(configFile.getAbsolutePath(), jobConfig, seaTunnelConfig);

                // 提交并执行作业
                ClientJobProxy clientJobProxy = jobExecutionEnv.execute();
                Long engineJobId = clientJobProxy.getJobId();

                log.info("[SeaTunnel Client] Job submitted, jobId: {}, engineJobId: {}", jobId, engineJobId);

                // 异步等待作业完成
                CompletableFuture<JobResult> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return clientJobProxy.waitForJobCompleteV2();
                    } catch (Exception e) {
                        log.error("[SeaTunnel Client] Job execution error, engineJobId: {}", engineJobId, e);
                        throw new RuntimeException(e);
                    }
                }, executorService);

                // 等待作业完成
                JobResult jobResult = future.get();
                JobStatus status = jobResult.getStatus();

                log.info("[SeaTunnel Client] Job completed, jobId: {}, engineJobId: {}, status: {}",
                        jobId, engineJobId, status);

                return status == JobStatus.FINISHED;
            }

        } catch (Exception e) {
            log.error("[SeaTunnel Client] Job execution failed, jobId: {}", jobId, e);
            return false;
        } finally {
            // 清理临时文件
            if (configFile.exists()) {
                boolean deleted = configFile.delete();
                log.info("[SeaTunnel Client] Config file deleted: {}, success: {}", configFile.getAbsolutePath(), deleted);
            }
        }
    }
}
