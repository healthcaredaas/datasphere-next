package cn.healthcaredaas.datasphere.svc.quality.engine.executor;

import cn.healthcaredaas.datasphere.svc.quality.engine.RuleExecuteResult;
import cn.healthcaredaas.datasphere.svc.quality.engine.RuleExecutor;
import cn.healthcaredaas.datasphere.svc.quality.entity.QualityRule;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 值域检查执行器
 * 检查字段值是否在指定范围内(枚举值、数值范围、日期范围等)
 *
 * @author chenpan
 */
@Slf4j
@Component
public class ValueRangeRuleExecutor implements RuleExecutor {

    @Override
    public boolean supports(String ruleType) {
        return "VALUE_RANGE".equals(ruleType);
    }

    @Override
    public String buildCheckSql(QualityRule rule) {
        // 解析规则表达式
        String expression = rule.getRuleExpression();

        // 支持格式: min:max (数值范围) 或 val1,val2,val3 (枚举值)
        if (expression.contains(":")) {
            // 数值范围
            String[] parts = expression.split(":");
            String min = parts[0];
            String max = parts[1];
            return String.format(
                    "SELECT COUNT(*) as error_count FROM %s WHERE %s < %s OR %s > %s",
                    rule.getTableName(), rule.getColumnName(), min,
                    rule.getColumnName(), max
            );
        } else if (expression.contains(",")) {
            // 枚举值
            String[] values = expression.split(",");
            String inClause = String.join(", ", values);
            return String.format(
                    "SELECT COUNT(*) as error_count FROM %s WHERE %s NOT IN (%s)",
                    rule.getTableName(), rule.getColumnName(), inClause
            );
        }

        return "";
    }

    @Override
    public String buildErrorSql(QualityRule rule) {
        List<String> pkColumns = getPrimaryKeyColumns(rule);
        String pkSelect = String.join(", ", pkColumns);

        String expression = rule.getRuleExpression();

        if (expression.contains(":")) {
            String[] parts = expression.split(":");
            String min = parts[0];
            String max = parts[1];
            return String.format(
                    "SELECT %s, %s FROM %s WHERE %s < %s OR %s > %s LIMIT 1000",
                    pkSelect, rule.getColumnName(), rule.getTableName(),
                    rule.getColumnName(), min, rule.getColumnName(), max
            );
        } else if (expression.contains(",")) {
            String[] values = expression.split(",");
            String inClause = String.join(", ", values);
            return String.format(
                    "SELECT %s, %s FROM %s WHERE %s NOT IN (%s) LIMIT 1000",
                    pkSelect, rule.getColumnName(), rule.getTableName(),
                    rule.getColumnName(), inClause
            );
        }

        return "";
    }

    @Override
    public RuleExecuteResult execute(QualityRule rule, Connection connection) {
        long startTime = System.currentTimeMillis();
        RuleExecuteResult.RuleExecuteResultBuilder resultBuilder = RuleExecuteResult.builder();

        try {
            // 验证规则表达式
            if (!validate(rule)) {
                return RuleExecuteResult.builder()
                        .success(false)
                        .errorMessage("规则表达式格式无效，应为 min:max 或 val1,val2,val3")
                        .duration(System.currentTimeMillis() - startTime)
                        .build();
            }

            // 获取总记录数
            long totalRows = getTotalCount(rule, connection);

            // 获取错误记录数
            long errorRows = getErrorCount(rule, connection);

            // 获取错误详情
            List<RuleExecuteResult.ErrorDetail> errorDetails = getErrorDetails(rule, connection);

            // 构建期望值描述
            String expectedValue = buildExpectedValue(rule.getRuleExpression());

            resultBuilder
                    .success(true)
                    .totalRows(totalRows)
                    .errorRows(errorRows)
                    .errorRate(totalRows > 0 ? (errorRows * 100.0 / totalRows) : 0)
                    .errorDetails(errorDetails);

        } catch (Exception e) {
            log.error("Value range check failed for rule: {}", rule.getId(), e);
            resultBuilder.success(false)
                    .errorMessage("执行值域检查失败: " + e.getMessage());
        }

        resultBuilder.duration(System.currentTimeMillis() - startTime);
        return resultBuilder.build();
    }

    private long getTotalCount(QualityRule rule, Connection connection) throws SQLException {
        String sql = buildCountSql(rule);
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }

    private long getErrorCount(QualityRule rule, Connection connection) throws SQLException {
        String sql = buildCheckSql(rule);
        if (sql.isEmpty()) return 0;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong("error_count");
            }
        }
        return 0;
    }

    private List<RuleExecuteResult.ErrorDetail> getErrorDetails(QualityRule rule, Connection connection) throws SQLException {
        List<RuleExecuteResult.ErrorDetail> details = new ArrayList<>();
        String sql = buildErrorSql(rule);
        if (sql.isEmpty()) return details;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<String> pkColumns = getPrimaryKeyColumns(rule);

            while (rs.next()) {
                JSONObject pkValue = new JSONObject();
                for (String pkCol : pkColumns) {
                    pkValue.put(pkCol, rs.getObject(pkCol));
                }

                Object errorValue = rs.getObject(rule.getColumnName());

                RuleExecuteResult.ErrorDetail detail = RuleExecuteResult.ErrorDetail.builder()
                        .primaryKeyValue(JSON.toJSONString(pkValue))
                        .errorValue(errorValue != null ? errorValue.toString() : null)
                        .expectedValue(buildExpectedValue(rule.getRuleExpression()))
                        .message("字段值不在指定范围内")
                        .build();

                details.add(detail);
            }
        }

        return details;
    }

    private String buildExpectedValue(String expression) {
        if (expression.contains(":")) {
            String[] parts = expression.split(":");
            return String.format("范围: %s - %s", parts[0], parts[1]);
        } else if (expression.contains(",")) {
            return "枚举值: " + expression;
        }
        return expression;
    }

    private List<String> getPrimaryKeyColumns(QualityRule rule) {
        List<String> columns = new ArrayList<>();
        columns.add("id");
        return columns;
    }
}
