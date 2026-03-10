package cn.healthcaredaas.datasphere.svc.integration.controller;

import cn.healthcaredaas.datasphere.api.integration.dto.DataJobDTO;
import cn.healthcaredaas.datasphere.svc.integration.entity.DataJob;
import cn.healthcaredaas.datasphere.svc.integration.entity.DataJobExecute;
import cn.healthcaredaas.datasphere.svc.integration.service.DataJobService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据作业控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/integration/jobs")
@RequiredArgsConstructor
@Tag(name = "数据作业管理", description = "数据作业管理相关接口(SeaTunnel)")
public class DataJobController {

    private final DataJobService dataJobService;

    @Operation(summary = "分页查询数据作业列表")
    @GetMapping
    public IPage<DataJob> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            DataJob params) {
        return dataJobService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "根据管道查询作业列表")
    @GetMapping("/by-pipeline/{pipelineId}")
    public List<DataJob> listByPipeline(@PathVariable("pipelineId") String pipelineId) {
        return dataJobService.listByPipeline(pipelineId);
    }

    @Operation(summary = "获取作业详情")
    @GetMapping("/{id}")
    public DataJob getById(@PathVariable("id") String id) {
        return dataJobService.getById(id);
    }

    @Operation(summary = "新增数据作业")
    @PostMapping
    public DataJob save(@RequestBody @Validated DataJob dataJob) {
        dataJobService.save(dataJob);
        return dataJob;
    }

    @Operation(summary = "更新数据作业")
    @PutMapping("/{id}")
    public DataJob update(@PathVariable("id") String id, @RequestBody @Validated DataJob dataJob) {
        dataJob.setId(id);
        dataJobService.updateById(dataJob);
        return dataJobService.getById(id);
    }

    @Operation(summary = "删除数据作业")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        dataJobService.removeById(id);
    }

    @Operation(summary = "发布作业(生成SeaTunnel配置)")
    @PostMapping("/{id}/publish")
    public boolean publish(@PathVariable("id") String id) {
        return dataJobService.publishJob(id);
    }

    @Operation(summary = "启动作业")
    @PostMapping("/{id}/start")
    public DataJobExecute start(@PathVariable("id") String id) {
        return dataJobService.startJob(id);
    }

    @Operation(summary = "停止作业")
    @PostMapping("/{id}/stop")
    public boolean stop(@PathVariable("id") String id) {
        return dataJobService.stopJob(id);
    }

    @Operation(summary = "获取作业配置内容")
    @GetMapping("/{id}/config")
    public String getConfig(@PathVariable("id") String id) {
        return dataJobService.getJobConfig(id);
    }

    @Operation(summary = "创建执行记录")
    @PostMapping("/{id}/execute")
    public DataJobExecute createExecuteRecord(@PathVariable("id") String id) {
        return dataJobService.startJob(id);
    }

    @Operation(summary = "更新作业状态")
    @PostMapping("/{id}/status")
    public Map<String, Object> updateJobStatus(@PathVariable("id") String id,
                                               @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        boolean success = dataJobService.updateStatus(id, status);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return result;
    }
}
