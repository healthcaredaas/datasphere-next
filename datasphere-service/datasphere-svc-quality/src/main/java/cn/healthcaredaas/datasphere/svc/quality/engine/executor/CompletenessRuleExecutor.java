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
 * 完整性检查执行器
 * 检查字段值是否为空或空字符串
 *
 * @author chenpan
 */
@Slf4j
@Component
public class CompletenessRuleExecutor implements RuleExecutor {

    @Override
    public boolean supports(String ruleType) {
        return "COMPLETENESS".equals(ruleType);
    }

    @Override
    public String buildCheckSql(QualityRule rule) {
        String column = rule.getColumnName();
        return String.format(
                "SELECT COUNT(*) as error_count FROM %s WHERE %s IS NULL OR %s = ''",
                rule.getTableName(), column, column
        );
    }

    @Override
    public String buildErrorSql(QualityRule rule) {
        String column = rule.getColumnName();
        List<String> pkColumns = getPrimaryKeyColumns(rule);
        String pkSelect = String.join(", ", pkColumns);

        return String.format(
                "SELECT %s, %s FROM %s WHERE %s IS NULL OR %s = '' LIMIT 1000",
                pkSelect, column, rule.getTableName(), column, column
        );
    }

    @Override
    public RuleExecuteResult execute(QualityRule rule, Connection connection) {
        long startTime = System.currentTimeMillis();
        RuleExecuteResult.RuleExecuteResultBuilder resultBuilder = RuleExecuteResult.builder();

        try {
            // 1. 获取总记录数
            long totalRows = getTotalCount(rule, connection);
            resultBuilder.totalRows(totalRows);

            // 2. 获取错误记录数
            long errorRows = getErrorCount(rule, connection);
            resultBuilder.errorRows(errorRows);

            // 3. 计算错误率
            double errorRate = totalRows > 0 ? (errorRows * 100.0 / totalRows) : 0;
            resultBuilder.errorRate(errorRate);

            // 4. 获取错误详情(前1000条)
            List<RuleExecuteResult.ErrorDetail> errorDetails = getErrorDetails(rule, connection);
            resultBuilder.errorDetails(errorDetails);

            resultBuilder.success(true);

        } catch (Exception e) {
            log.error("Completeness check failed for rule: {}", rule.getId(), e);
            resultBuilder.success(false)
                    .errorMessage("执行完整性检查失败: " + e.getMessage());
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

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
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
                        .expectedValue("非空值")
                        .message("字段值为空或空字符串")
                        .build();

                details.add(detail);
            }
        }

        return details;
    }

    private List<String> getPrimaryKeyColumns(QualityRule rule) {
        // 简化处理，实际应从数据库元数据获取
        List<String> columns = new ArrayList<>();
        columns.add("id");
        return columns;
    }
}
