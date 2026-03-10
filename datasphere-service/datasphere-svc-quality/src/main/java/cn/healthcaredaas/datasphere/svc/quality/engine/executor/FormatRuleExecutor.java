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
import java.util.regex.Pattern;

/**
 * 格式检查执行器
 * 检查字段值是否符合指定正则表达式格式
 *
 * @author chenpan
 */
@Slf4j
@Component
public class FormatRuleExecutor implements RuleExecutor {

    @Override
    public boolean supports(String ruleType) {
        return "FORMAT".equals(ruleType);
    }

    @Override
    public boolean validate(QualityRule rule) {
        if (rule.getRuleExpression() == null || rule.getRuleExpression().isEmpty()) {
            return false;
        }
        try {
            Pattern.compile(rule.getRuleExpression());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String buildCheckSql(QualityRule rule) {
        String column = rule.getColumnName();
        String regex = rule.getRuleExpression();

        return String.format(
                "SELECT COUNT(*) as error_count FROM %s WHERE %s IS NOT NULL AND %s != '' AND %s NOT REGEXP '%s'",
                rule.getTableName(), column, column, column, regex
        );
    }

    @Override
    public String buildErrorSql(QualityRule rule) {
        String column = rule.getColumnName();
        String regex = rule.getRuleExpression();
        List<String> pkColumns = getPrimaryKeyColumns(rule);
        String pkSelect = String.join(", ", pkColumns);

        return String.format(
                "SELECT %s, %s FROM %s WHERE %s IS NOT NULL AND %s != '' AND %s NOT REGEXP '%s' LIMIT 1000",
                pkSelect, column, rule.getTableName(),
                column, column, column, regex
        );
    }

    @Override
    public RuleExecuteResult execute(QualityRule rule, Connection connection) {
        long startTime = System.currentTimeMillis();
        RuleExecuteResult.RuleExecuteResultBuilder resultBuilder = RuleExecuteResult.builder();

        try {
            // 验证正则表达式
            if (!validate(rule)) {
                return resultBuilder
                        .success(false)
                        .errorMessage("无效的正则表达式: " + rule.getRuleExpression())
                        .build();
            }

            // 获取总记录数
            long totalRows = getTotalCount(rule, connection);
            resultBuilder.totalRows(totalRows);

            // 获取不符合格式的记录数
            long errorRows = getErrorCount(rule, connection);
            resultBuilder.errorRows(errorRows);

            // 计算错误率
            double errorRate = totalRows > 0 ? (errorRows * 100.0 / totalRows) : 0;
            resultBuilder.errorRate(errorRate);

            // 获取错误详情
            List<RuleExecuteResult.ErrorDetail> errorDetails = getErrorDetails(rule, connection);
            resultBuilder.errorDetails(errorDetails);

            resultBuilder.success(true);

        } catch (Exception e) {
            log.error("Format check failed for rule: {}", rule.getId(), e);
            resultBuilder.success(false)
                    .errorMessage("执行格式检查失败: " + e.getMessage());
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
                        .expectedValue("匹配正则: " + rule.getRuleExpression())
                        .message("字段值格式不符合要求")
                        .build();

                details.add(detail);
            }
        }

        return details;
    }

    private List<String> getPrimaryKeyColumns(QualityRule rule) {
        List<String> columns = new ArrayList<>();
        columns.add("id");
        return columns;
    }
}
