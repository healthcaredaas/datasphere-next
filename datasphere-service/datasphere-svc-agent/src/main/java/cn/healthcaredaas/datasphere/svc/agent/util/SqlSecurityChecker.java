package cn.healthcaredaas.datasphere.svc.agent.util;

import java.util.List;

/**
 * SQL安全检查工具
 *
 * @author chenpan
 */
public class SqlSecurityChecker {

    /**
     * 禁止的SQL关键字
     */
    private static final List<String> FORBIDDEN_KEYWORDS = List.of(
            "DELETE", "DROP", "TRUNCATE", "ALTER", "CREATE",
            "INSERT", "UPDATE", "GRANT", "REVOKE", "EXEC", "EXECUTE",
            "INTO OUTFILE", "INTO DUMPFILE", "LOAD DATA"
    );

    /**
     * 危险函数
     */
    private static final List<String> DANGEROUS_FUNCTIONS = List.of(
            "SLEEP", "BENCHMARK", "WAITFOR", "PG_SLEEP",
            "XP_", "SP_", "INFORMATION_SCHEMA", "MYSQL.", "SYS."
    );

    /**
     * 检查SQL安全性
     *
     * @param sql SQL语句
     * @return 检查结果
     */
    public static SecurityCheckResult check(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return SecurityCheckResult.fail("SQL语句不能为空");
        }

        String upperSql = sql.toUpperCase().trim();

        // 1. 只允许SELECT或WITH (CTE)
        if (!upperSql.startsWith("SELECT") && !upperSql.startsWith("WITH")) {
            return SecurityCheckResult.fail("只允许执行SELECT查询语句");
        }

        // 2. 检查禁止关键字
        for (String keyword : FORBIDDEN_KEYWORDS) {
            if (containsKeyword(upperSql, keyword)) {
                return SecurityCheckResult.fail("SQL包含禁止关键字: " + keyword);
            }
        }

        // 3. 检查危险函数
        for (String func : DANGEROUS_FUNCTIONS) {
            if (upperSql.contains(func)) {
                return SecurityCheckResult.fail("SQL包含危险函数: " + func);
            }
        }

        // 4. 检查注释注入
        if (sql.contains("--") || sql.contains("/*") || sql.contains("*/")) {
            return SecurityCheckResult.fail("SQL包含可疑注释");
        }

        // 5. 检查分号注入
        if (sql.indexOf(';') != sql.lastIndexOf(';')) {
            return SecurityCheckResult.fail("SQL包含多条语句");
        }

        return SecurityCheckResult.success();
    }

    /**
     * 检查SQL安全性（简化版本，返回错误信息或null）
     *
     * @param sql SQL语句
     * @return 错误信息，如果安全则返回null
     */
    public static String checkSimple(String sql) {
        SecurityCheckResult result = check(sql);
        return result.isSafe() ? null : result.getMessage();
    }

    /**
     * 安全处理SQL（添加LIMIT等）
     *
     * @param sql    原始SQL
     * @param maxRow 最大行数
     * @return 处理后的SQL
     */
    public static String safeProcess(String sql, int maxRow) {
        String upperSql = sql.toUpperCase().trim();

        // 移除末尾分号
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1).trim();
        }

        // 添加LIMIT（如果没有）
        if (!upperSql.contains("LIMIT")) {
            sql = sql + " LIMIT " + maxRow;
        }

        return sql;
    }

    /**
     * 检查是否包含关键字（考虑边界）
     */
    private static boolean containsKeyword(String sql, String keyword) {
        int idx = sql.indexOf(keyword);
        if (idx == -1) {
            return false;
        }

        // 检查关键字边界
        int end = idx + keyword.length();
        if (end < sql.length()) {
            char nextChar = sql.charAt(end);
            if (Character.isLetterOrDigit(nextChar) || nextChar == '_') {
                return false;
            }
        }

        return true;
    }

    /**
     * 安全检查结果
     */
    public static class SecurityCheckResult {
        private final boolean safe;
        private final String message;

        private SecurityCheckResult(boolean safe, String message) {
            this.safe = safe;
            this.message = message;
        }

        public static SecurityCheckResult success() {
            return new SecurityCheckResult(true, "OK");
        }

        public static SecurityCheckResult fail(String message) {
            return new SecurityCheckResult(false, message);
        }

        public boolean isSafe() {
            return safe;
        }

        public String getMessage() {
            return message;
        }
    }
}