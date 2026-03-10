package cn.healthcaredaas.datasphere.svc.agent.util;

import cn.healthcaredaas.datasphere.svc.agent.util.SqlSecurityChecker.SecurityCheckResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SQL安全检查测试
 *
 * @author chenpan
 */
class SqlSecurityCheckerTest {

    @Test
    @DisplayName("合法SELECT语句 - 通过检查")
    void testValidSelectStatement() {
        String sql = "SELECT * FROM patient WHERE age > 18 LIMIT 100";
        SecurityCheckResult result = SqlSecurityChecker.check(sql);

        assertTrue(result.isSafe());
        assertEquals("OK", result.getMessage());
    }

    @Test
    @DisplayName("非SELECT语句 - 检查失败")
    void testNonSelectStatement() {
        String[] invalidSqls = {
                "DELETE FROM patient",
                "UPDATE patient SET name = 'test'",
                "INSERT INTO patient VALUES (1, 'test')",
                "DROP TABLE patient",
                "TRUNCATE TABLE patient"
        };

        for (String sql : invalidSqls) {
            SecurityCheckResult result = SqlSecurityChecker.check(sql);
            assertFalse(result.isSafe());
            assertTrue(result.getMessage().contains("SELECT"));
        }
    }

    @Test
    @DisplayName("包含禁止关键字 - 检查失败")
    void testForbiddenKeywords() {
        String sql = "SELECT * FROM patient; DROP TABLE patient;--";
        SecurityCheckResult result = SqlSecurityChecker.check(sql);

        assertFalse(result.isSafe());
    }

    @Test
    @DisplayName("包含SQL注释 - 检查失败")
    void testSqlCommentInjection() {
        String sql = "SELECT * FROM patient WHERE id = 1 -- AND status = 1";
        SecurityCheckResult result = SqlSecurityChecker.check(sql);

        assertFalse(result.isSafe());
        assertTrue(result.getMessage().contains("注释"));
    }

    @Test
    @DisplayName("包含多条语句 - 检查失败")
    void testMultipleStatements() {
        String sql = "SELECT * FROM patient; SELECT * FROM doctor;";
        SecurityCheckResult result = SqlSecurityChecker.check(sql);

        assertFalse(result.isSafe());
        assertTrue(result.getMessage().contains("多条语句"));
    }

    @Test
    @DisplayName("空SQL - 检查失败")
    void testEmptySql() {
        SecurityCheckResult result1 = SqlSecurityChecker.check(null);
        SecurityCheckResult result2 = SqlSecurityChecker.check("");
        SecurityCheckResult result3 = SqlSecurityChecker.check("   ");

        assertFalse(result1.isSafe());
        assertFalse(result2.isSafe());
        assertFalse(result3.isSafe());
    }

    @Test
    @DisplayName("安全处理SQL - 添加LIMIT")
    void testSafeProcessAddLimit() {
        String sql = "SELECT * FROM patient";
        String processed = SqlSecurityChecker.safeProcess(sql, 1000);

        assertTrue(processed.contains("LIMIT 1000"));
    }

    @Test
    @DisplayName("安全处理SQL - 已有LIMIT不重复添加")
    void testSafeProcessExistingLimit() {
        String sql = "SELECT * FROM patient LIMIT 10";
        String processed = SqlSecurityChecker.safeProcess(sql, 1000);

        assertTrue(processed.contains("LIMIT 10"));
        assertFalse(processed.contains("LIMIT 1000"));
    }

    @Test
    @DisplayName("安全处理SQL - 移除末尾分号")
    void testSafeProcessRemoveSemicolon() {
        String sql = "SELECT * FROM patient;";
        String processed = SqlSecurityChecker.safeProcess(sql, 100);

        assertFalse(processed.endsWith(";"));
    }
}