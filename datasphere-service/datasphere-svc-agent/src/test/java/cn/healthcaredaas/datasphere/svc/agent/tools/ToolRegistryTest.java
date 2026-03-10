package cn.healthcaredaas.datasphere.svc.agent.tools;

import cn.healthcaredaas.datasphere.svc.agent.entity.ToolDefinition;
import cn.healthcaredaas.datasphere.svc.agent.service.ToolDefinitionService;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ToolRegistry 单元测试
 *
 * @author chenpan
 */
@ExtendWith(MockitoExtension.class)
class ToolRegistryTest {

    @Mock
    private Tool sqlGeneratorTool;

    @Mock
    private Tool sqlExecutionTool;

    @Mock
    private Tool qualityRuleTool;

    @Mock
    private ToolDefinitionService toolDefinitionService;

    private ToolRegistry toolRegistry;

    @BeforeEach
    void setUp() {
        // 配置Mock工具
        when(sqlGeneratorTool.getName()).thenReturn("sql_generator");
        when(sqlGeneratorTool.getDescription()).thenReturn("SQL生成工具");
        when(sqlGeneratorTool.getInputSchema()).thenReturn(new JSONObject());

        when(sqlExecutionTool.getName()).thenReturn("sql_executor");
        when(sqlExecutionTool.getDescription()).thenReturn("SQL执行工具");
        when(sqlExecutionTool.getInputSchema()).thenReturn(new JSONObject());

        when(qualityRuleTool.getName()).thenReturn("quality_rule_generator");
        when(qualityRuleTool.getDescription()).thenReturn("质量规则生成工具");
        when(qualityRuleTool.getInputSchema()).thenReturn(new JSONObject());

        // 创建ToolRegistry并初始化
        toolRegistry = new ToolRegistry(
                List.of(sqlGeneratorTool, sqlExecutionTool, qualityRuleTool),
                toolDefinitionService
        );
        toolRegistry.init();
    }

    @Test
    @DisplayName("初始化 - 注册所有工具")
    void testInit_RegistersAllTools() {
        // When
        List<String> toolNames = toolRegistry.getToolNames();

        // Then
        assertEquals(3, toolNames.size());
        assertTrue(toolNames.contains("sql_generator"));
        assertTrue(toolNames.contains("sql_executor"));
        assertTrue(toolNames.contains("quality_rule_generator"));
    }

    @Test
    @DisplayName("获取工具 - 存在的工具")
    void testGetTool_ExistingTool() {
        // When
        Tool tool = toolRegistry.getTool("sql_generator");

        // Then
        assertNotNull(tool);
        assertEquals("sql_generator", tool.getName());
    }

    @Test
    @DisplayName("获取工具 - 不存在的工具")
    void testGetTool_NonExistingTool() {
        // When
        Tool tool = toolRegistry.getTool("unknown_tool");

        // Then
        assertNull(tool);
    }

    @Test
    @DisplayName("检查工具是否存在 - 存在")
    void testHasTool_Existing() {
        // When
        boolean exists = toolRegistry.hasTool("sql_executor");

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("检查工具是否存在 - 不存在")
    void testHasTool_NonExisting() {
        // When
        boolean exists = toolRegistry.hasTool("unknown_tool");

        // Then
        assertFalse(exists);
    }

    @Test
    @DisplayName("获取工具Schema")
    void testGetToolSchema() {
        // When
        JSONObject schema = toolRegistry.getToolSchema("sql_generator");

        // Then
        assertNotNull(schema);
        assertEquals("sql_generator", schema.getString("name"));
        assertEquals("SQL生成工具", schema.getString("description"));
        assertNotNull(schema.get("parameters"));
    }

    @Test
    @DisplayName("获取工具Schema - 工具不存在")
    void testGetToolSchema_NonExisting() {
        // When
        JSONObject schema = toolRegistry.getToolSchema("unknown_tool");

        // Then
        assertNull(schema);
    }

    @Test
    @DisplayName("获取所有工具Schema")
    void testGetAllToolSchemas() {
        // When
        List<JSONObject> schemas = toolRegistry.getAllToolSchemas();

        // Then
        assertNotNull(schemas);
        assertEquals(3, schemas.size());
    }

    @Test
    @DisplayName("获取工具定义列表")
    void testGetToolDefinitions() {
        // Given
        ToolDefinition def1 = new ToolDefinition();
        def1.setId("tool-001");
        def1.setToolName("sql_generator");

        when(toolDefinitionService.listEnabled()).thenReturn(List.of(def1));

        // When
        List<ToolDefinition> definitions = toolRegistry.getToolDefinitions();

        // Then
        assertNotNull(definitions);
        assertEquals(1, definitions.size());
        verify(toolDefinitionService, times(1)).listEnabled();
    }
}