package cn.healthcaredaas.datasphere.svc.integration.seatunnel;

import cn.healthcaredaas.datasphere.svc.integration.entity.PipelineConnector;
import cn.healthcaredaas.datasphere.svc.integration.seatunnel.config.SeaTunnelConfig;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SeaTunnel配置生成器单元测试
 *
 * @author chenpan
 */
class SeaTunnelConfigGeneratorTest {

    private SeaTunnelConfigGenerator configGenerator;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        configGenerator = new SeaTunnelConfigGenerator();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("测试生成基本JDBC同步配置")
    void testGenerateJdbcToJdbcConfig() throws Exception {
        // 准备数据
        List<PipelineConnector> connectors = new ArrayList<>();

        // Source连接器
        PipelineConnector source = new PipelineConnector();
        source.setConnectorType("SOURCE");
        source.setConnectorName("source_table");
        source.setPluginType("jdbc");
        JSONObject sourceConfig = new JSONObject();
        sourceConfig.put("url", "jdbc:mysql://localhost:3306/source_db");
        sourceConfig.put("driver", "com.mysql.cj.jdbc.Driver");
        sourceConfig.put("user", "root");
        sourceConfig.put("password", "123456");
        sourceConfig.put("query", "SELECT id, name, age FROM users");
        source.setConfig(sourceConfig);
        source.setOrderNo(1);
        connectors.add(source);

        // Sink连接器
        PipelineConnector sink = new PipelineConnector();
        sink.setConnectorType("SINK");
        sink.setConnectorName("target_table");
        sink.setPluginType("jdbc");
        JSONObject sinkConfig = new JSONObject();
        sinkConfig.put("url", "jdbc:mysql://localhost:3306/target_db");
        sinkConfig.put("driver", "com.mysql.cj.jdbc.Driver");
        sinkConfig.put("user", "root");
        sinkConfig.put("password", "123456");
        sinkConfig.put("query", "INSERT INTO users (id, name, age) VALUES (?, ?, ?)");
        sink.setConfig(sinkConfig);
        sink.setOrderNo(2);
        connectors.add(sink);

        // 执行生成
        String configJson = configGenerator.generateConfig("jdbc-sync-job", connectors);

        // 验证结果
        assertNotNull(configJson);
        assertFalse(configJson.isEmpty());

        // 解析JSON验证结构
        SeaTunnelConfig config = objectMapper.readValue(configJson, SeaTunnelConfig.class);
        assertNotNull(config.getEnv());
        assertEquals("jdbc-sync-job", config.getEnv().getName());
        assertEquals("BATCH", config.getEnv().getJobMode());
        assertNotNull(config.getSources());
        assertEquals(1, config.getSources().size());
        assertNotNull(config.getSinks());
        assertEquals(1, config.getSinks().size());
    }

    @Test
    @DisplayName("测试生成带SQL转换的配置")
    void testGenerateConfigWithSqlTransform() throws Exception {
        // 准备数据
        List<PipelineConnector> connectors = new ArrayList<>();

        // Source连接器
        PipelineConnector source = new PipelineConnector();
        source.setConnectorType("SOURCE");
        source.setConnectorName("source_table");
        source.setPluginType("jdbc");
        JSONObject sourceConfig = new JSONObject();
        sourceConfig.put("url", "jdbc:mysql://localhost:3306/test");
        sourceConfig.put("driver", "com.mysql.cj.jdbc.Driver");
        sourceConfig.put("user", "root");
        sourceConfig.put("password", "123456");
        sourceConfig.put("query", "SELECT * FROM orders");
        source.setConfig(sourceConfig);
        connectors.add(source);

        // Transform连接器
        PipelineConnector transform = new PipelineConnector();
        transform.setConnectorType("TRANSFORM");
        transform.setConnectorName("transform_1");
        transform.setPluginType("sql");
        JSONObject transConfig = new JSONObject();
        transConfig.put("source_table_name", "source_table");
        transConfig.put("result_table_name", "transformed_table");
        transConfig.put("query", "SELECT id, name, price * 2 as new_price FROM source_table");
        transform.setConfig(transConfig);
        connectors.add(transform);

        // Sink连接器
        PipelineConnector sink = new PipelineConnector();
        sink.setConnectorType("SINK");
        sink.setConnectorName("target_table");
        sink.setPluginType("jdbc");
        JSONObject sinkConfig = new JSONObject();
        sinkConfig.put("url", "jdbc:mysql://localhost:3306/test");
        sinkConfig.put("driver", "com.mysql.cj.jdbc.Driver");
        sinkConfig.put("user", "root");
        sinkConfig.put("password", "123456");
        sinkConfig.put("query", "INSERT INTO new_orders VALUES (?, ?, ?)");
        sink.setConfig(sinkConfig);
        connectors.add(sink);

        // 执行生成
        String configJson = configGenerator.generateConfig("transform-job", connectors);

        // 验证结果
        assertNotNull(configJson);
        SeaTunnelConfig config = objectMapper.readValue(configJson, SeaTunnelConfig.class);
        assertNotNull(config.getSources());
        assertNotNull(config.getTransforms());
        assertEquals(1, config.getTransforms().size());
        assertNotNull(config.getSinks());
    }

    @Test
    @DisplayName("测试生成Console输出配置")
    void testGenerateConsoleSinkConfig() throws Exception {
        // 准备数据
        List<PipelineConnector> connectors = new ArrayList<>();

        // Source连接器
        PipelineConnector source = new PipelineConnector();
        source.setConnectorType("SOURCE");
        source.setConnectorName("source_table");
        source.setPluginType("jdbc");
        JSONObject sourceConfig = new JSONObject();
        sourceConfig.put("url", "jdbc:mysql://localhost:3306/test");
        sourceConfig.put("driver", "com.mysql.cj.jdbc.Driver");
        sourceConfig.put("user", "root");
        sourceConfig.put("password", "123456");
        sourceConfig.put("query", "SELECT * FROM test_table");
        source.setConfig(sourceConfig);
        connectors.add(source);

        // Console Sink
        PipelineConnector sink = new PipelineConnector();
        sink.setConnectorType("SINK");
        sink.setConnectorName("console_output");
        sink.setPluginType("Console");
        sink.setConfig(new JSONObject());
        connectors.add(sink);

        // 执行生成
        String configJson = configGenerator.generateConfig("console-test-job", connectors);

        // 验证结果
        assertNotNull(configJson);
        assertTrue(configJson.contains("Console"));
    }

    @Test
    @DisplayName("测试空连接器列表")
    void testGenerateWithEmptyConnectors() {
        List<PipelineConnector> connectors = new ArrayList<>();

        String configJson = configGenerator.generateConfig("empty-job", connectors);

        assertNotNull(configJson);
        // 空连接器时只有env部分
        assertTrue(configJson.contains("env"));
    }

    @Test
    @DisplayName("测试多个Source和Sink")
    void testGenerateWithMultipleSourcesAndSinks() throws Exception {
        // 准备数据
        List<PipelineConnector> connectors = new ArrayList<>();

        // 第一个Source
        PipelineConnector source1 = new PipelineConnector();
        source1.setConnectorType("SOURCE");
        source1.setConnectorName("users_source");
        source1.setPluginType("jdbc");
        JSONObject sourceConfig1 = new JSONObject();
        sourceConfig1.put("url", "jdbc:mysql://localhost:3306/db1");
        sourceConfig1.put("query", "SELECT * FROM users");
        source1.setConfig(sourceConfig1);
        connectors.add(source1);

        // 第二个Source
        PipelineConnector source2 = new PipelineConnector();
        source2.setConnectorType("SOURCE");
        source2.setConnectorName("orders_source");
        source2.setPluginType("jdbc");
        JSONObject sourceConfig2 = new JSONObject();
        sourceConfig2.put("url", "jdbc:mysql://localhost:3306/db2");
        sourceConfig2.put("query", "SELECT * FROM orders");
        source2.setConfig(sourceConfig2);
        connectors.add(source2);

        // Sink
        PipelineConnector sink = new PipelineConnector();
        sink.setConnectorType("SINK");
        sink.setConnectorName("combined_sink");
        sink.setPluginType("jdbc");
        JSONObject sinkConfig = new JSONObject();
        sinkConfig.put("url", "jdbc:mysql://localhost:3306/target");
        sink.setConfig(sinkConfig);
        connectors.add(sink);

        // 执行生成
        String configJson = configGenerator.generateConfig("multi-source-job", connectors);

        // 验证结果
        SeaTunnelConfig config = objectMapper.readValue(configJson, SeaTunnelConfig.class);
        assertEquals(2, config.getSources().size());
        assertEquals(1, config.getSinks().size());
    }

    @Test
    @DisplayName("测试generateZetaConfig与generateConfig结果相同")
    void testGenerateZetaConfig() {
        List<PipelineConnector> connectors = new ArrayList<>();

        PipelineConnector source = new PipelineConnector();
        source.setConnectorType("SOURCE");
        source.setConnectorName("test");
        source.setPluginType("jdbc");
        source.setConfig(new JSONObject());
        connectors.add(source);

        String config1 = configGenerator.generateConfig("test-job", connectors);
        String config2 = configGenerator.generateZetaConfig("test-job", connectors);

        assertEquals(config1, config2);
    }
}
