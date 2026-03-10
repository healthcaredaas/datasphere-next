package cn.healthcaredaas.datasphere.svc.quality.controller;

import cn.healthcaredaas.datasphere.svc.quality.entity.QualityIssue;
import cn.healthcaredaas.datasphere.svc.quality.entity.QualityReport;
import cn.healthcaredaas.datasphere.svc.quality.entity.QualityRuleTemplate;
import cn.healthcaredaas.datasphere.svc.quality.entity.QualityTask;
import cn.healthcaredaas.datasphere.svc.quality.mapper.QualityIssueMapper;
import cn.healthcaredaas.datasphere.svc.quality.mapper.QualityReportMapper;
import cn.healthcaredaas.datasphere.svc.quality.mapper.QualityRuleTemplateMapper;
import cn.healthcaredaas.datasphere.svc.quality.service.QualityTaskSchedulerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 质量检测任务控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/quality/tasks")
@RequiredArgsConstructor
@Tag(name = "质量检测任务", description = "质量检测任务管理")
public class QualityTaskController {

    private final QualityTaskSchedulerService taskSchedulerService;
    private final QualityIssueMapper issueMapper;
    private final QualityReportMapper reportMapper;
    private final QualityRuleTemplateMapper templateMapper;

    @Operation(summary = "创建检测任务")
    @PostMapping("/create/{ruleId}")
    public QualityTask createTask(@PathVariable("ruleId") String ruleId) {
        return taskSchedulerService.createTask(ruleId);
    }

    @Operation(summary = "执行检测任务")
    @PostMapping("/execute/{taskId}")
    public void executeTask(@PathVariable("taskId") String taskId) {
        taskSchedulerService.executeTask(taskId);
    }

    @Operation(summary = "批量执行规则检测")
    @PostMapping("/batch-execute")
    public void batchExecute(@RequestBody List<String> ruleIds) {
        taskSchedulerService.batchExecuteRules(ruleIds);
    }

    @Operation(summary = "查询问题记录")
    @GetMapping("/issues")
    public List<QualityIssue> listIssues(
            @Parameter(description = "任务ID") @RequestParam(required = false) String taskId,
            @Parameter(description = "处理状态") @RequestParam(required = false) Integer handleStatus) {
        LambdaQueryWrapper<QualityIssue> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(taskId)) {
            wrapper.eq(QualityIssue::getTaskId, taskId);
        }
        if (handleStatus != null) {
            wrapper.eq(QualityIssue::getHandleStatus, handleStatus);
        }
        wrapper.orderByDesc(QualityIssue::getCreateTime);
        return issueMapper.selectList(wrapper);
    }

    @Operation(summary = "处理问题")
    @PostMapping("/issues/{issueId}/handle")
    public void handleIssue(
            @PathVariable("issueId") String issueId,
            @RequestParam Integer handleStatus,
            @RequestParam String handler,
            @RequestParam String remark) {
        QualityIssue issue = issueMapper.selectById(issueId);
        if (issue != null) {
            issue.setHandleStatus(handleStatus);
            issue.setHandler(handler);
            issue.setHandleRemark(remark);
            issueMapper.updateById(issue);
        }
    }

    @Operation(summary = "查询检测报告")
    @GetMapping("/reports")
    public List<QualityReport> listReports(
            @Parameter(description = "规则ID") @RequestParam(required = false) String ruleId) {
        LambdaQueryWrapper<QualityReport> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(ruleId)) {
            wrapper.eq(QualityReport::getRuleId, ruleId);
        }
        wrapper.orderByDesc(QualityReport::getReportDate);
        return reportMapper.selectList(wrapper);
    }

    @Operation(summary = "获取报告详情")
    @GetMapping("/reports/{reportId}")
    public QualityReport getReport(@PathVariable("reportId") String reportId) {
        return reportMapper.selectById(reportId);
    }

    @Operation(summary = "查询规则模板")
    @GetMapping("/templates")
    public List<QualityRuleTemplate> listTemplates(
            @Parameter(description = "规则类型") @RequestParam(required = false) String ruleType) {
        LambdaQueryWrapper<QualityRuleTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QualityRuleTemplate::getStatus, 1); // 只查启用的
        if (StringUtils.isNotBlank(ruleType)) {
            wrapper.eq(QualityRuleTemplate::getRuleType, ruleType);
        }
        wrapper.orderByAsc(QualityRuleTemplate::getSortNo);
        return templateMapper.selectList(wrapper);
    }
}
