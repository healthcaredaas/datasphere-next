package cn.healthcaredaas.datasphere.svc.quality.engine.executor;

import cn.healthcaredaas.datasphere.svc.quality.engine.RuleExecuteContext;
import cn.healthcaredaas.datasphere.svc.quality.engine.RuleExecuteResult;
import cn.healthcaredaas.datasphere.svc.quality.engine.RuleExecutor;
import cn.healthcaredaas.datasphere.svc.quality.engine.RuleType;
import cn.healthcaredaas.datasphere.svc.quality.entity.QualityRule;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * 一致性检查执行器
 * 支持多表关联验证，包括：
 * 1. 外键引用检查 - 子表记录在主表中是否存在
 * 2. 汇总一致性 - 父表汇总字段与子表明细汇总是否一致
 * 3. 记录数匹配 - 关联表之间的记录数是否匹配
 * 4. 字段值相等 - 关联表中对应字段值是否相等
 *
 * @author chenpan
 */
@Slf4j
@Component("CONSISTENCYExecutor")
public class ConsistencyRuleExecutor implements RuleExecutor {

    @Override
    public RuleType getSupportedType() {
        return RuleType.CONSISTENCY;
    }

    @Override
    public RuleExecuteResult execute(RuleExecuteContext context) {
        QualityRule rule = context.getRule();
        JdbcTemplate jdbcTemplate = context.getJdbcTemplate();

        log.info("执行一致性检查, 规则: {}", rule.getRuleName());

        try {
            // 解析规则表达式获取检查类型
            String expression = rule.getRuleExpression();
            ConsistencyCheckType checkType = parseCheckType(expression);

            // 根据检查类型执行不同的检查逻辑
            return switch (checkType) {
                case FOREIGN_KEY -> executeForeignKeyCheck(jdbcTemplate, rule);
                case SUM_MATCH -> executeSumMatchCheck(jdbcTemplate, rule);
                case COUNT_MATCH -> executeCountMatchCheck(jdbcTemplate, rule);
                case FIELD_EQUAL -> executeFieldEqualCheck(jdbcTemplate, rule);
                case DATE_SEQUENCE -> executeDateSequenceCheck(jdbcTemplate, rule);
                default -> executeCustomCheck(jdbcTemplate, rule);
            };

        } catch (Exception e) {
            log.error("一致性检查执行失败: {}", e.getMessage(), e);
            return RuleExecuteResult.builder()
                    .success(false)
                    .message("检查执行失败: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public boolean validate(QualityRule rule) {
        if (StringUtils.isBlank(rule.getRuleExpression())) {
            return false;
        }
        // 检查SQL必须包含多表关联
        String expression = rule.getRuleExpression().toUpperCase();
        return expression.contains("JOIN") || expression.contains("EXISTS") || expression.contains("IN");
    }

    /**
     * 外键引用检查
     * 检查：表A中的字段值在表B中是否存在
     * 示例：订单表中的user_id在用户表中是否存在
     */
    private RuleExecuteResult executeForeignKeyCheck(JdbcTemplate jdbc, QualityRule rule) {
        String sql = rule.getRuleExpression();

        // 获取错误数据
        List<Map<String, Object>> errorRows = jdbc.queryForList(sql);
        long errorCount = errorRows.size();

        // 获取总记录数
        String mainTable = extractMainTable(sql);
        long totalCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM " + mainTable, Long.class);

        // 计算得分
        double score = totalCount == 0 ? 100.0 :
                ((totalCount - errorCount) * 100.0 / totalCount);

        return RuleExecuteResult.builder()
                .success(true)
                .totalRows(totalCount)
                .errorRows(errorCount)
                .score(BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP))
                .errorDetails(convertToErrorDetails(errorRows, rule))
                .message("外键引用一致性检查完成")
                .build();
    }

    /**
     * 汇总一致性检查
     * 检查：父表的汇总字段是否等于子表明细的汇总
     * 示例：订单表的total_amount是否等于订单明细的SUM(item_amount)
     */
    private RuleExecuteResult executeSumMatchCheck(JdbcTemplate jdbc, QualityRule rule) {
        String sql = rule.getRuleExpression();

        List<Map<String, Object>> mismatchedRows = jdbc.queryForList(sql);
        long errorCount = mismatchedRows.size();

        // 获取主表总记录数
        String mainTable = extractMainTable(sql);
        long totalCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM " + mainTable, Long.class);

        double score = totalCount == 0 ? 100.0 :
                ((totalCount - errorCount) * 100.0 / totalCount);

        return RuleExecuteResult.builder()
                .success(true)
                .totalRows(totalCount)
                .errorRows(errorCount)
                .score(BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP))
                .errorDetails(convertToErrorDetails(mismatchedRows, rule))
                .message("汇总一致性检查完成")
                .build();
    }

    /**
     * 记录数匹配检查
     * 检查：关联表的记录数是否匹配
     * 示例：用户表的order_count是否等于该用户的订单数量
     */
    private RuleExecuteResult executeCountMatchCheck(JdbcTemplate jdbc, QualityRule rule) {
        String sql = rule.getRuleExpression();

        List<Map<String, Object>> mismatchedRows = jdbc.queryForList(sql);
        long errorCount = mismatchedRows.size();

        String mainTable = extractMainTable(sql);
        long totalCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM " + mainTable, Long.class);

        double score = totalCount == 0 ? 100.0 :
                ((totalCount - errorCount) * 100.0 / totalCount);

        return RuleExecuteResult.builder()
                .success(true)
                .totalRows(totalCount)
                .errorRows(errorCount)
                .score(BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP))
                .errorDetails(convertToErrorDetails(mismatchedRows, rule))
                .message("记录数匹配检查完成")
                .build();
    }

    /**
     * 字段值相等检查
     * 检查：关联表中对应字段的值是否相等
     * 示例：订单表的pay_amount是否等于支付表的amount
     */
    private RuleExecuteResult executeFieldEqualCheck(JdbcTemplate jdbc, QualityRule rule) {
        String sql = rule.getRuleExpression();

        List<Map<String, Object>> mismatchedRows = jdbc.queryForList(sql);
        long errorCount = mismatchedRows.size();

        String mainTable = extractMainTable(sql);
        long totalCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM " + mainTable, Long.class);

        double score = totalCount == 0 ? 100.0 :
                ((totalCount - errorCount) * 100.0 / totalCount);

        return RuleExecuteResult.builder()
                .success(true)
                .totalRows(totalCount)
                .errorRows(errorCount)
                .score(BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP))
                .errorDetails(convertToErrorDetails(mismatchedRows, rule))
                .message("字段值相等检查完成")
                .build();
    }

    /**
     * 时间顺序检查
     * 检查：多个时间字段的逻辑顺序是否正确
     * 示例：create_time < pay_time < ship_time < complete_time
     */
    private RuleExecuteResult executeDateSequenceCheck(JdbcTemplate jdbc, QualityRule rule) {
        String sql = rule.getRuleExpression();

        List<Map<String, Object>> invalidRows = jdbc.queryForList(sql);
        long errorCount = invalidRows.size();

        String mainTable = rule.getTableName();
        long totalCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM " + mainTable, Long.class);

        double score = totalCount == 0 ? 100.0 :
                ((totalCount - errorCount) * 100.0 / totalCount);

        return RuleExecuteResult.builder()
                .success(true)
                .totalRows(totalCount)
                .errorRows(errorCount)
                .score(BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP))
                .errorDetails(convertToErrorDetails(invalidRows, rule))
                .message("时间顺序检查完成")
                .build();
    }

    /**
     * 自定义一致性检查
     */
    private RuleExecuteResult executeCustomCheck(JdbcTemplate jdbc, QualityRule rule) {
        String sql = rule.getRuleExpression();

        List<Map<String, Object>> errorRows = jdbc.queryForList(sql);
        long errorCount = errorRows.size();

        String mainTable = rule.getTableName();
        long totalCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM " + mainTable, Long.class);

        double score = totalCount == 0 ? 100.0 :
                ((totalCount - errorCount) * 100.0 / totalCount);

        return RuleExecuteResult.builder()
                .success(true)
                .totalRows(totalCount)
                .errorRows(errorCount)
                .score(BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP))
                .errorDetails(convertToErrorDetails(errorRows, rule))
                .message("自定义一致性检查完成")
                .build();
    }

    /**
     * 解析检查类型
     */
    private ConsistencyCheckType parseCheckType(String expression) {
        String upperExpr = expression.toUpperCase();

        if (upperExpr.contains("SUM(") && upperExpr.contains("GROUP BY")) {
            return ConsistencyCheckType.SUM_MATCH;
        }
        if (upperExpr.contains("COUNT(") && upperExpr.contains("GROUP BY")) {
            return ConsistencyCheckType.COUNT_MATCH;
        }
        if (upperExpr.contains("LEFT JOIN") && upperExpr.contains("IS NULL")) {
            return ConsistencyCheckType.FOREIGN_KEY;
        }
        if (upperExpr.contains("NOT EXISTS")) {
            return ConsistencyCheckType.FOREIGN_KEY;
        }
        if (upperExpr.contains("CREATE_TIME") && upperExpr.contains("UPDATE_TIME")) {
            return ConsistencyCheckType.DATE_SEQUENCE;
        }
        if (upperExpr.matches(".*[<>!=].*")) {
            return ConsistencyCheckType.FIELD_EQUAL;
        }

        return ConsistencyCheckType.CUSTOM;
    }

    /**
     * 从SQL中提取主表名
     */
    private String extractMainTable(String sql) {
        // 简单提取，实际可能需要更复杂的解析
        String upperSql = sql.toUpperCase();
        int fromIndex = upperSql.indexOf("FROM ");
        if (fromIndex > 0) {
            String afterFrom = sql.substring(fromIndex + 5).trim();
            // 获取第一个单词（表名）
            return afterFrom.split("\\s+")[0];
        }
        return "";
    }

    /**
     * 转换错误详情
     */
    private List<RuleExecuteResult.ErrorDetail> convertToErrorDetails(
            List<Map<String, Object>> rows, QualityRule rule) {
        return rows.stream()
                .map(row -> RuleExecuteResult.ErrorDetail.builder()
                        .primaryKeyValue(row.getOrDefault("id", "").toString())
                        .message(rule.getColumnName())
                        .errorValue(row.getOrDefault(rule.getColumnName(), "").toString())
                        .errorMessage("一致性检查失败: " + row)
                        .build())
                .limit(100) // 最多返回100条
                .toList();
    }

    /**
     * 一致性检查类型枚举
     */
    private enum ConsistencyCheckType {
        FOREIGN_KEY,    // 外键引用检查
        SUM_MATCH,      // 汇总一致性
        COUNT_MATCH,    // 记录数匹配
        FIELD_EQUAL,    // 字段值相等
        DATE_SEQUENCE,  // 时间顺序
        CUSTOM          // 自定义
    }
}
