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

/**
 * 唯一性检查执行器
 * 检查字段值是否重复
 *
 * @author chenpan
 */
@Slf4j
@Component
public class UniquenessRuleExecutor implements RuleExecutor {

    @Override
    public boolean supports(String ruleType) {
        return "UNIQUENESS".equals(ruleType);
    }

    @Override
    public String buildCheckSql(QualityRule rule) {
        String column = rule.getColumnName();
        return String.format(
                "SELECT COUNT(*) as error_count FROM (" +
                        "SELECT %s FROM %s GROUP BY %s HAVING COUNT(*) > 1" +
                        ") t",
                column, rule.getTableName(), column
        );
    }

    @Override
    public String buildErrorSql(QualityRule rule) {
        String column = rule.getColumnName();
        List<String> pkColumns = getPrimaryKeyColumns(rule);
        String pkSelect = String.join(", ", pkColumns);

        return String.format(
                "SELECT %s, %s FROM %s t1 " +
                        "WHERE EXISTS (" +
                        "SELECT 1 FROM %s t2 " +
                        "WHERE t1.%s = t2.%s AND t1.id != t2.id" +
                        ") LIMIT 1000",
                pkSelect, column, rule.getTableName(),
                rule.getTableName(), column, column
        );
    }

    @Override
    public RuleExecuteResult execute(QualityRule rule, Connection connection) {
        long startTime = System.currentTimeMillis();
        RuleExecuteResult.RuleExecuteResultBuilder resultBuilder = RuleExecuteResult.builder();

        try {
            // 获取重复值统计
            String duplicateSql = String.format(
                    "SELECT %s, COUNT(*) as cnt FROM %s GROUP BY %s HAVING COUNT(*) > 1",
                    rule.getColumnName(), rule.getTableName(), rule.getColumnName()
            );

            long errorRows = 0;
            List<String> duplicateValues = new ArrayList<>();

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(duplicateSql)) {
                while (rs.next()) {
                    errorRows += rs.getLong("cnt");
                    duplicateValues.add(rs.getString(rule.getColumnName()));
                }
            }

            // 获取总记录数
            long totalRows = getTotalCount(rule, connection);

            // 获取错误详情
            List<RuleExecuteResult.ErrorDetail> errorDetails = getErrorDetails(rule, connection, duplicateValues);

            resultBuilder
                    .success(true)
                    .totalRows(totalRows)
                    .errorRows(errorRows)
                    .errorRate(totalRows > 0 ? (errorRows * 100.0 / totalRows) : 0)
                    .errorDetails(errorDetails);

        } catch (Exception e) {
            log.error("Uniqueness check failed for rule: {}", rule.getId(), e);
            resultBuilder.success(false)
                    .errorMessage("执行唯一性检查失败: " + e.getMessage());
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

    private List<RuleExecuteResult.ErrorDetail> getErrorDetails(QualityRule rule, Connection connection,
                                                                  List<String> duplicateValues) throws SQLException {
        List<RuleExecuteResult.ErrorDetail> details = new ArrayList<>();

        if (duplicateValues.isEmpty()) {
            return details;
        }

        // 查询重复值的详情
        String inClause = String.join(", ", duplicateValues.stream()
                .map(v -> "'" + v + "'")
                .toArray(String[]::new));

        String sql = String.format(
                "SELECT id, %s FROM %s WHERE %s IN (%s) ORDER BY %s LIMIT 100",
                rule.getColumnName(), rule.getTableName(),
                rule.getColumnName(), inClause, rule.getColumnName()
        );

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                JSONObject pkValue = new JSONObject();
                pkValue.put("id", rs.getObject("id"));

                Object errorValue = rs.getObject(rule.getColumnName());

                RuleExecuteResult.ErrorDetail detail = RuleExecuteResult.ErrorDetail.builder()
                        .primaryKeyValue(JSON.toJSONString(pkValue))
                        .errorValue(errorValue != null ? errorValue.toString() : null)
                        .expectedValue("唯一值")
                        .message("字段值重复")
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
