package cn.healthcaredaas.datasphere.svc.quality.engine;

import cn.healthcaredaas.datasphere.svc.quality.entity.QualityRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 规则执行器接口
 *
 * @author chenpan
 */
public interface RuleExecutor {

    /**
     * 执行规则检测
     *
     * @param rule       规则配置
     * @param connection 数据库连接
     * @return 执行结果
     */
    RuleExecuteResult execute(QualityRule rule, Connection connection);

    /**
     * 是否支持该规则类型
     *
     * @param ruleType 规则类型
     * @return 是否支持
     */
    boolean supports(String ruleType);

    /**
     * 验证规则表达式是否有效
     *
     * @param rule 规则配置
     * @return 是否有效
     */
    default boolean validate(QualityRule rule) {
        return rule.getRuleExpression() != null && !rule.getRuleExpression().isEmpty();
    }

    /**
     * 获取检测SQL
     *
     * @param rule 规则
     * @return SQL
     */
    String buildCheckSql(QualityRule rule);

    /**
     * 获取统计SQL
     *
     * @param rule 规则
     * @return SQL
     */
    default String buildCountSql(QualityRule rule) {
        return "SELECT COUNT(*) FROM " + rule.getTableName();
    }

    /**
     * 获取错误数据SQL
     *
     * @param rule 规则
     * @return SQL
     */
    String buildErrorSql(QualityRule rule);

    /**
     * 构建查询主键列的SQL
     *
     * @param rule       规则
     * @param connection 数据库连接
     * @return 主键列名列表
     */
    default List<String> getPrimaryKeyColumns(QualityRule rule, Connection connection) {
        List<String> pkColumns = new ArrayList<>();
        String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE " +
                "WHERE TABLE_SCHEMA = DATABASE() " +
                "AND TABLE_NAME = ? " +
                "AND CONSTRAINT_NAME = 'PRIMARY'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, rule.getTableName());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                pkColumns.add(rs.getString("COLUMN_NAME"));
            }
        } catch (SQLException e) {
            // 如果无法获取主键，使用默认的id
            pkColumns.add("id");
        }

        if (pkColumns.isEmpty()) {
            pkColumns.add("id");
        }

        return pkColumns;
    }
}
