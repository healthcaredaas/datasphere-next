package cn.healthcaredaas.datasphere.svc.integration.controller;

import cn.healthcaredaas.datasphere.svc.integration.entity.DataJobExecute;
import cn.healthcaredaas.datasphere.svc.integration.service.DataJobExecuteService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据作业执行记录控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/integration/job-executes")
@RequiredArgsConstructor
@Tag(name = "数据作业执行记录管理", description = "数据作业执行记录管理相关接口")
public class DataJobExecuteController {

    private final DataJobExecuteService dataJobExecuteService;

    @Operation(summary = "分页查询作业执行记录")
    @GetMapping
    public IPage<DataJobExecute> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            DataJobExecute params) {
        return dataJobExecuteService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "根据作业查询执行记录")
    @GetMapping("/by-job/{jobId}")
    public List<DataJobExecute> listByJob(@PathVariable("jobId") String jobId) {
        return dataJobExecuteService.listByJob(jobId);
    }

    @Operation(summary = "获取最新执行记录")
    @GetMapping("/latest/{jobId}")
    public DataJobExecute getLatest(@PathVariable("jobId") String jobId) {
        return dataJobExecuteService.getLatestExecute(jobId);
    }

    @Operation(summary = "获取执行记录详情")
    @GetMapping("/{id}")
    public DataJobExecute getById(@PathVariable("id") String id) {
        return dataJobExecuteService.getById(id);
    }
}
