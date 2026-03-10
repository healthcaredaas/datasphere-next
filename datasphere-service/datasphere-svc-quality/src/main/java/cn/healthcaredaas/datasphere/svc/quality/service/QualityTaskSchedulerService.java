package cn.healthcaredaas.datasphere.svc.quality.service;

import cn.healthcaredaas.datasphere.svc.quality.entity.QualityIssue;
import cn.healthcaredaas.datasphere.svc.quality.entity.QualityReport;
import cn.healthcaredaas.datasphere.svc.quality.entity.QualityRule;
import cn.healthcaredaas.datasphere.svc.quality.entity.QualityTask;
import cn.healthcaredaas.datasphere.svc.quality.engine.QualityCheckEngine;
import cn.healthcaredaas.datasphere.svc.quality.engine.RuleExecuteResult;
import cn.healthcaredaas.datasphere.svc.quality.mapper.QualityIssueMapper;
import cn.healthcaredaas.datasphere.svc.quality.mapper.QualityReportMapper;
import cn.healthcaredaas.datasphere.svc.quality.mapper.QualityRuleMapper;
import cn.healthcaredaas.datasphere.svc.quality.mapper.QualityTaskMapper;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 质量检测任务调度服务
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QualityTaskSchedulerService {

    private final QualityTaskMapper taskMapper;
    private final QualityRuleMapper ruleMapper;
    private final QualityIssueMapper issueMapper;
    private final QualityReportMapper reportMapper;
    private final QualityCheckEngine checkEngine;

    /**
     * 创建检测任务
     *
     * @param ruleId 规则ID
     * @return 任务
     */
    @Transactional(rollbackFor = Exception.class)
    public QualityTask createTask(String ruleId) {
        QualityRule rule = ruleMapper.selectById(ruleId);
        if (rule == null) {
            throw new RuntimeException("规则不存在");
        }

        QualityTask task = new QualityTask();
        task.setRuleId(ruleId);
        task.setTaskName("检测任务-" + rule.getRuleName() + "-" + System.currentTimeMillis());
        task.setStartTime(LocalDateTime.now());
        task.setStatus(0); // 运行中

        taskMapper.insert(task);
        log.info("Created quality check task: {}", task.getId());

        return task;
    }

    /**
     * 执行检测任务
     *
     * @param taskId 任务ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void executeTask(String taskId) {
        QualityTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        QualityRule rule = ruleMapper.selectById(task.getRuleId());
        if (rule == null) {
            throw new RuntimeException("规则不存在");
        }

        log.info("Executing quality check task: {}, rule: {}", taskId, rule.getRuleName());

        // 执行检测
        RuleExecuteResult result = checkEngine.executeRule(rule);

        // 更新任务结果
        task.setEndTime(LocalDateTime.now());
        task.setStatus(result.isSuccess() ? 1 : 2); // 1-成功, 2-失败
        task.setTotalRows(result.getTotalRows());
        task.setErrorRows(result.getErrorRows());
        taskMapper.updateById(task);

        // 保存错误详情
        if (result.isSuccess() && result.getErrorRows() > 0 && result.getErrorDetails() != null) {
            saveErrorDetails(taskId, rule, result.getErrorDetails());
        }

        // 生成报告
        generateReport(task, rule, result);

        log.info("Quality check task completed: {}, success: {}, errorRows: {}",
                taskId, result.isSuccess(), result.getErrorRows());
    }

    /**
     * 保存错误详情
     */
    private void saveErrorDetails(String taskId, QualityRule rule,
                                   List<RuleExecuteResult.ErrorDetail> details) {
        for (RuleExecuteResult.ErrorDetail detail : details) {
            QualityIssue issue = new QualityIssue();
            issue.setTaskId(taskId);
            issue.setRuleId(rule.getId());
            issue.setDatasourceId(rule.getDatasourceId());
            issue.setTableName(rule.getTableName());
            issue.setColumnName(rule.getColumnName());
            issue.setRuleType(rule.getRuleType());
            issue.setPrimaryKeyValue(detail.getPrimaryKeyValue());
            issue.setErrorValue(detail.getErrorValue());
            issue.setExpectedValue(detail.getExpectedValue());
            issue.setErrorMessage(detail.getMessage());
            issue.setHandleStatus(0); // 未处理

            issueMapper.insert(issue);
        }
    }

    /**
     * 生成检测报告
     */
    private void generateReport(QualityTask task, QualityRule rule, RuleExecuteResult result) {
        QualityReport report = new QualityReport();
        report.setTaskId(task.getId());
        report.setRuleId(rule.getId());
        report.setReportName("质量检测报告-" + rule.getRuleName());
        report.setReportDate(LocalDateTime.now());
        report.setTotalRows(result.getTotalRows());
        report.setErrorRows(result.getErrorRows());

        // 计算评分(错误率越低，得分越高)
        double score = 100.0;
        if (result.getTotalRows() > 0 && result.getErrorRows() > 0) {
            score = Math.max(0, 100 - (result.getErrorRows() * 100.0 / result.getTotalRows()));
        }
        report.setScore(BigDecimal.valueOf(score));

        // 设置等级
        String grade;
        if (score >= 95) {
            grade = "A";
        } else if (score >= 85) {
            grade = "B";
        } else if (score >= 70) {
            grade = "C";
        } else if (score >= 60) {
            grade = "D";
        } else {
            grade = "F";
        }
        report.setGrade(grade);

        report.setStatus(result.isSuccess() ? 1 : 0);

        // 生成报告内容
        StringBuilder content = new StringBuilder();
        content.append("## 质量检测报告\n\n");
        content.append("### 基本信息\n");
        content.append("- 规则名称: ").append(rule.getRuleName()).append("\n");
        content.append("- 规则类型: ").append(rule.getRuleType()).append("\n");
        content.append("- 检测表: ").append(rule.getTableName()).append("\n");
        content.append("- 检测字段: ").append(rule.getColumnName()).append("\n\n");
        content.append("### 检测结果\n");
        content.append("- 检测总行数: ").append(result.getTotalRows()).append("\n");
        content.append("- 错误行数: ").append(result.getErrorRows()).append("\n");
        content.append("- 错误率: ").append(String.format("%.2f%%", result.getErrorRate())).append("\n");
        content.append("- 质量评分: ").append(String.format("%.2f", score)).append("\n");
        content.append("- 质量等级: ").append(grade).append("\n");

        report.setReportContent(content.toString());

        reportMapper.insert(report);
    }

    /**
     * 批量执行规则检测
     *
     * @param ruleIds 规则ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchExecuteRules(List<String> ruleIds) {
        for (String ruleId : ruleIds) {
            try {
                QualityTask task = createTask(ruleId);
                executeTask(task.getId());
            } catch (Exception e) {
                log.error("Failed to execute rule: {}", ruleId, e);
            }
        }
    }
}
