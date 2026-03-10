package cn.healthcaredaas.datasphere.api.integration.feign;

import cn.healthcaredaas.datasphere.api.integration.dto.DataJobDTO;
import cn.healthcaredaas.datasphere.api.integration.dto.DataJobExecuteDTO;
import cn.healthcaredaas.datasphere.api.integration.dto.PipelineConnectorDTO;
import cn.healthcaredaas.datasphere.api.integration.result.JobExecuteResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 集成作业Feign客户端
 * <p>
 * 供Job Runner服务调用，获取作业配置和更新执行状态
 *
 * @author chenpan
 */
@FeignClient(name = "datasphere-svc-integration", path = "/api/v1/integration")
public interface IntegrationJobClient {

    /**
     * 获取作业信息
     *
     * @param jobId 作业ID
     * @return 作业信息
     */
    @GetMapping("/jobs/{jobId}")
    DataJobDTO getJob(@PathVariable("jobId") String jobId);

    /**
     * 获取作业配置内容
     *
     * @param jobId 作业ID
     * @return 配置内容
     */
    @GetMapping("/jobs/{jobId}/config")
    String getJobConfig(@PathVariable("jobId") String jobId);

    /**
     * 获取管道连接器列表
     *
     * @param pipelineId 管道ID
     * @return 连接器列表
     */
    @GetMapping("/pipeline-connectors/by-pipeline/{pipelineId}")
    List<PipelineConnectorDTO> listConnectorsByPipeline(@PathVariable("pipelineId") String pipelineId);

    /**
     * 创建执行记录
     *
     * @param jobId 作业ID
     * @return 执行记录
     */
    @PostMapping("/jobs/{jobId}/execute")
    DataJobExecuteDTO createExecuteRecord(@PathVariable("jobId") String jobId);

    /**
     * 更新执行记录状态
     *
     * @param executeId 执行记录ID
     * @param params    更新参数
     * @return 结果
     */
    @PostMapping("/job-executes/{executeId}/status")
    JobExecuteResult updateExecuteStatus(@PathVariable("executeId") String executeId,
                                          @RequestBody Map<String, Object> params);

    /**
     * 作业执行成功回调
     *
     * @param executeId 执行记录ID
     * @param params    参数
     * @return 结果
     */
    @PostMapping("/job-callback/success/{executeId}")
    Map<String, Object> onJobSuccess(@PathVariable("executeId") String executeId,
                                     @RequestBody Map<String, Object> params);

    /**
     * 作业执行失败回调
     *
     * @param executeId 执行记录ID
     * @param params    参数
     * @return 结果
     */
    @PostMapping("/job-callback/failed/{executeId}")
    Map<String, Object> onJobFailed(@PathVariable("executeId") String executeId,
                                    @RequestBody Map<String, String> params);

    /**
     * 更新作业状态
     *
     * @param jobId  作业ID
     * @param params 参数
     * @return 结果
     */
    @PostMapping("/jobs/{jobId}/status")
    Map<String, Object> updateJobStatus(@PathVariable("jobId") String jobId,
                                        @RequestBody Map<String, Integer> params);
}
