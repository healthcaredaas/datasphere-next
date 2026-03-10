package cn.healthcaredaas.datasphere.svc.integration.controller;

import cn.healthcaredaas.datasphere.svc.integration.service.JobExecuteCallbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 作业执行回调控制器
 * <p>
 * 供JobRunner服务调用，更新作业执行状态
 *
 * @author chenpan
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/integration/job-callback")
@RequiredArgsConstructor
@Tag(name = "作业执行回调", description = "作业执行状态回调接口")
public class JobExecuteCallbackController {

    private final JobExecuteCallbackService callbackService;

    @Operation(summary = "作业执行成功回调")
    @PostMapping("/success/{executeId}")
    public Map<String, Object> onJobSuccess(
            @PathVariable("executeId") String executeId,
            @RequestBody Map<String, Object> params) {

        Long readRows = params.get("readRows") != null ? Long.valueOf(params.get("readRows").toString()) : 0L;
        Long writeRows = params.get("writeRows") != null ? Long.valueOf(params.get("writeRows").toString()) : 0L;

        log.info("Job execute success callback, executeId: {}, readRows: {}, writeRows: {}",
                executeId, readRows, writeRows);

        callbackService.onJobSuccess(executeId, readRows, writeRows);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    @Operation(summary = "作业执行失败回调")
    @PostMapping("/failed/{executeId}")
    public Map<String, Object> onJobFailed(
            @PathVariable("executeId") String executeId,
            @RequestBody Map<String, String> params) {

        String errorMsg = params.get("errorMsg");
        log.info("Job execute failed callback, executeId: {}, error: {}", executeId, errorMsg);

        callbackService.onJobFailed(executeId, errorMsg);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    @Operation(summary = "更新作业执行进度")
    @PostMapping("/progress/{executeId}")
    public Map<String, Object> updateProgress(
            @PathVariable("executeId") String executeId,
            @RequestBody Map<String, Object> params) {

        Long readRows = params.get("readRows") != null ? Long.valueOf(params.get("readRows").toString()) : 0L;
        Long writeRows = params.get("writeRows") != null ? Long.valueOf(params.get("writeRows").toString()) : 0L;

        callbackService.updateProgress(executeId, readRows, writeRows);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    @Operation(summary = "获取作业配置")
    @GetMapping("/config/{jobId}")
    public Map<String, Object> getJobConfig(@PathVariable("jobId") String jobId) {
        String config = callbackService.getJobConfig(jobId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", config);
        return result;
    }

    @Operation(summary = "更新作业状态")
    @PostMapping("/status/{jobId}")
    public Map<String, Object> updateJobStatus(
            @PathVariable("jobId") String jobId,
            @RequestBody Map<String, Integer> params) {

        Integer status = params.get("status");
        callbackService.updateJobStatus(jobId, status);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }
}
