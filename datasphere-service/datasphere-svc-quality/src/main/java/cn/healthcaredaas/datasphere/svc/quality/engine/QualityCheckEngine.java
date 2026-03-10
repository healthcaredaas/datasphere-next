package cn.healthcaredaas.datasphere.svc.quality.engine;

import cn.healthcaredaas.datasphere.svc.quality.entity.QualityRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

/**
 * 质量检测引擎
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QualityCheckEngine {

    private final List<RuleExecutor> ruleExecutors;
    private final DataSource dataSource;

    /**
     * 执行规则检测
     *
     * @param rule 质量规则
     * @return 执行结果
     */
    public RuleExecuteResult executeRule(QualityRule rule) {
        log.info("Executing quality rule: {}", rule.getRuleName());

        // 查找对应的执行器
        RuleExecutor executor = findExecutor(rule.getRuleType());
        if (executor == null) {
            return RuleExecuteResult.builder()
                    .success(false)
                    .errorMessage("不支持的规则类型: " + rule.getRuleType())
                    .build();
        }

        // 验证规则
        if (!executor.validate(rule)) {
            return RuleExecuteResult.builder()
                    .success(false)
                    .errorMessage("规则表达式无效")
                    .build();
        }

        // 执行检测
        try (Connection connection = dataSource.getConnection()) {
            return executor.execute(rule, connection);
        } catch (Exception e) {
            log.error("Rule execution failed: {}", rule.getId(), e);
            return RuleExecuteResult.builder()
                    .success(false)
                    .errorMessage("规则执行失败: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 批量执行规则检测
     *
     * @param rules 规则列表
     * @return 执行结果列表
     */
    public List<RuleExecuteResult> executeRules(List<QualityRule> rules) {
        return rules.stream()
                .map(this::executeRule)
                .toList();
    }

    /**
     * 测试规则SQL是否有效
     *
     * @param rule 规则
     * @return 是否有效
     */
    public boolean testRule(QualityRule rule) {
        RuleExecutor executor = findExecutor(rule.getRuleType());
        if (executor == null) {
            return false;
        }

        try (Connection connection = dataSource.getConnection()) {
            String sql = executor.buildCheckSql(rule);
            try (var stmt = connection.createStatement()) {
                stmt.executeQuery(sql);
                return true;
            }
        } catch (Exception e) {
            log.error("Rule test failed: {}", rule.getId(), e);
            return false;
        }
    }

    /**
     * 查找规则执行器
     */
    private RuleExecutor findExecutor(String ruleType) {
        for (RuleExecutor executor : ruleExecutors) {
            if (executor.supports(ruleType)) {
                return executor;
            }
        }
        return null;
    }
}
