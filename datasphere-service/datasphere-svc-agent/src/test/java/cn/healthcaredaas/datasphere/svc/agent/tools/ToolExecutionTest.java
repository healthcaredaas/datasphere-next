package cn.healthcaredaas.datasphere.svc.agent.tools;

import cn.healthcaredaas.datasphere.svc.agent.tools.executor.SqlExecutionTool;
import cn.healthcaredaas.datasphere.svc.agent.tools.executor.SqlGeneratorTool;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 工具执行测试
 *
 * @author chenpan
 */
class ToolExecutionTest {

    private SqlExecutionTool sqlExecutionTool;
    private SqlGeneratorTool sqlGeneratorTool;
    private ToolContext context;

    @BeforeEach
    void setUp() {
        sqlExecutionTool = new SqlExecutionTool();
        sqlGeneratorTool = new SqlGeneratorTool();
        context = ToolContext.of("session-001", "user-001", "tenant-001");
    }

    @Test
    @DisplayName("SQL执行工具 - 获取工具信息")
    void testSqlExecutionToolInfo() {
        assertEquals("sql_executor", sqlExecutionTool.getName());
        assertNotNull(sqlExecutionTool.getDescription());
        assertNotNull(sqlExecutionTool.getInputSchema());
    }

    @Test
    @DisplayName("SQL执行工具 - 执行SELECT查询")
    void testSqlExecution() {
        JSONObject params = new JSONObject();
        params.put("datasource_id", "ds_001");
        params.put("sql", "SELECT * FROM patient LIMIT 10");

        ToolResult result = sqlExecutionTool.execute(params, context);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    @DisplayName("SQL执行工具 - 拒绝非SELECT语句")
    void testSqlExecutionRejectNonSelect() {
        JSONObject params = new JSONObject();
        params.put("datasource_id", "ds_001");
        params.put("sql", "DELETE FROM patient");

        ToolResult result = sqlExecutionTool.execute(params, context);

        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("SELECT"));
    }

    @Test
    @DisplayName("SQL生成工具 - 获取工具信息")
    void testSqlGeneratorToolInfo() {
        assertEquals("sql_generator", sqlGeneratorTool.getName());
        assertNotNull(sqlGeneratorTool.getDescription());
        assertNotNull(sqlGeneratorTool.getInputSchema());
    }

    @Test
    @DisplayName("SQL生成工具 - 生成SQL")
    void testSqlGeneration() {
        JSONObject params = new JSONObject();
        params.put("datasource_id", "ds_001");
        params.put("natural_language", "查询最近一个月的门诊数据");

        ToolResult result = sqlGeneratorTool.execute(params, context);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());

        JSONObject data = (JSONObject) result.getData();
        assertNotNull(data.getString("sql"));
        assertNotNull(data.getString("explanation"));
    }

    @Test
    @DisplayName("工具上下文 - 创建")
    void testToolContext() {
        assertEquals("session-001", context.getSessionId());
        assertEquals("user-001", context.getUserId());
        assertEquals("tenant-001", context.getTenantId());
    }

    @Test
    @DisplayName("工具结果 - 成功结果")
    void testToolResultSuccess() {
        JSONObject data = new JSONObject();
        data.put("key", "value");

        ToolResult result = ToolResult.success(data);

        assertTrue(result.isSuccess());
        assertEquals(data, result.getData());
        assertNull(result.getErrorMessage());
    }

    @Test
    @DisplayName("工具结果 - 失败结果")
    void testToolResultError() {
        ToolResult result = ToolResult.error("执行失败");

        assertFalse(result.isSuccess());
        assertEquals("执行失败", result.getErrorMessage());
    }
}