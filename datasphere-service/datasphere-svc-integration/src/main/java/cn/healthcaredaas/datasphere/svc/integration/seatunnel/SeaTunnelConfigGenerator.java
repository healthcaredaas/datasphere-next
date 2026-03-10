package cn.healthcaredaas.datasphere.svc.integration.seatunnel;

import cn.healthcaredaas.datasphere.svc.integration.entity.PipelineConnector;
import cn.healthcaredaas.datasphere.svc.integration.seatunnel.config.*;
import cn.healthcaredaas.datasphere.svc.integration.seatunnel.config.source.JdbcSource;
import cn.healthcaredaas.datasphere.svc.integration.seatunnel.config.sink.JdbcSink;
import cn.healthcaredaas.datasphere.svc.integration.seatunnel.config.transform.SqlTransform;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SeaTunnel 配置文件生成器
 * <p>
 * 使用类型化的DTO对象构建配置，通过Jackson直接序列化为配置字符串
 *
 * @author chenpan
 */
@Slf4j
@Component
public class SeaTunnelConfigGenerator {

    private final ObjectMapper objectMapper;
    private final JavaPropsMapper propsMapper;

    public SeaTunnelConfigGenerator() {
        this.objectMapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(SerializationFeature.INDENT_OUTPUT, true);
        this.propsMapper = new JavaPropsMapper();
    }

    /**
     * 生成SeaTunnel配置（HOCON格式）
     *
     * @param pipelineName 管道名称
     * @param connectors   连接器列表
     * @return HOCON格式的配置字符串
     */
    public String generateConfig(String pipelineName, List<PipelineConnector> connectors) {
        // 构建配置对象
        SeaTunnelConfig config = buildConfigObject(pipelineName, connectors);

        // 使用Jackson序列化为JSON格式
        String jsonConfig = serializeToJson(config);

        log.info("Generated SeaTunnel config for pipeline: {}", pipelineName);
        return jsonConfig;
    }

    /**
     * 构建配置对象（使用类型化的DTO）
     */
    private SeaTunnelConfig buildConfigObject(String pipelineName, List<PipelineConnector> connectors) {
        SeaTunnelConfig config = new SeaTunnelConfig();

        // Env部分
        Env env = new Env();
        env.setName(pipelineName);
        env.setJobMode("BATCH");
        env.setParallelism(1);
        env.setCheckpointInterval(10000L);
        config.setEnv(env);

        // 按类型分组
        Map<String, List<PipelineConnector>> connectorsByType = connectors.stream()
                .collect(java.util.stream.Collectors.groupingBy(PipelineConnector::getConnectorType));

        // Source部分
        List<PipelineConnector> sources = connectorsByType.get("SOURCE");
        if (sources != null && !sources.isEmpty()) {
            config.setSources(buildSources(sources));
        }

        // Transform部分
        List<PipelineConnector> transforms = connectorsByType.get("TRANSFORM");
        if (transforms != null && !transforms.isEmpty()) {
            config.setTransforms(buildTransforms(transforms));
        }

        // Sink部分
        List<PipelineConnector> sinks = connectorsByType.get("SINK");
        if (sinks != null && !sinks.isEmpty()) {
            config.setSinks(buildSinks(sinks));
        }

        return config;
    }

    /**
     * 构建Source列表
     */
    private List<Source> buildSources(List<PipelineConnector> connectors) {
        List<Source> sources = new ArrayList<>();

        for (PipelineConnector connector : connectors) {
            String pluginType = connector.getPluginType();
            JSONObject configJson = connector.getConfig();

            Source source = switch (pluginType.toLowerCase()) {
                case "jdbc" -> buildJdbcSource(configJson);
                default -> buildGenericSource(pluginType, configJson);
            };

            if (source != null) {
                source.setResultTableName(connector.getConnectorName());
                sources.add(source);
            }
        }

        return sources;
    }

    /**
     * 构建JdbcSource
     */
    private JdbcSource buildJdbcSource(JSONObject config) {
        JdbcSource source = new JdbcSource();
        source.setPluginName("jdbc");

        if (config != null) {
            source.setUrl(config.getString("url"));
            source.setDriver(config.getString("driver"));
            source.setUser(config.getString("user"));
            source.setPassword(config.getString("password"));
            source.setQuery(config.getString("query"));
            source.setConnectionCheckTimeoutSec(config.getInteger("connection_check_timeout_sec"));
            source.setPartitionColumn(config.getString("partition_column"));
            source.setPartitionUpperBound(config.getString("partition_upper_bound"));
            source.setPartitionLowerBound(config.getString("partition_lower_bound"));
            source.setPartitionNum(config.getInteger("partition_num"));
            source.setFetchSize(config.getInteger("fetch_size"));
        }

        return source;
    }

    /**
     * 构建通用Source
     */
    private Source buildGenericSource(String pluginType, JSONObject config) {
        Source source = new Source();
        source.setPluginName(pluginType);
        return source;
    }

    /**
     * 构建Transform列表
     */
    private List<Transform> buildTransforms(List<PipelineConnector> connectors) {
        List<Transform> transforms = new ArrayList<>();

        for (PipelineConnector connector : connectors) {
            String pluginType = connector.getPluginType();
            JSONObject configJson = connector.getConfig();

            Transform transform = switch (pluginType.toLowerCase()) {
                case "sql" -> buildSqlTransform(configJson);
                default -> buildGenericTransform(pluginType, configJson);
            };

            if (transform != null) {
                transform.setSourceTableName(configJson != null ? configJson.getString("source_table_name") : null);
                transform.setResultTableName(configJson != null ? configJson.getString("result_table_name") : null);
                transforms.add(transform);
            }
        }

        return transforms;
    }

    /**
     * 构建SqlTransform
     */
    private SqlTransform buildSqlTransform(JSONObject config) {
        SqlTransform transform = new SqlTransform();
        transform.setPluginName("sql");

        if (config != null) {
            transform.setQuery(config.getString("query"));
        }

        return transform;
    }

    /**
     * 构建通用Transform
     */
    private Transform buildGenericTransform(String pluginType, JSONObject config) {
        Transform transform = new Transform();
        transform.setPluginName(pluginType);
        return transform;
    }

    /**
     * 构建Sink列表
     */
    private List<Sink> buildSinks(List<PipelineConnector> connectors) {
        List<Sink> sinks = new ArrayList<>();

        for (PipelineConnector connector : connectors) {
            String pluginType = connector.getPluginType();
            JSONObject configJson = connector.getConfig();

            Sink sink = switch (pluginType.toLowerCase()) {
                case "jdbc" -> buildJdbcSink(configJson);
                case "console" -> buildConsoleSink(configJson);
                default -> buildGenericSink(pluginType, configJson);
            };

            if (sink != null) {
                sinks.add(sink);
            }
        }

        return sinks;
    }

    /**
     * 构建JdbcSink
     */
    private JdbcSink buildJdbcSink(JSONObject config) {
        JdbcSink sink = new JdbcSink();
        sink.setPluginName("jdbc");

        if (config != null) {
            sink.setUrl(config.getString("url"));
            sink.setDriver(config.getString("driver"));
            sink.setUser(config.getString("user"));
            sink.setPassword(config.getString("password"));
            sink.setQuery(config.getString("query"));
            sink.setDatabase(config.getString("database"));
            sink.setTable(config.getString("table"));
            sink.setConnectionCheckTimeoutSec(config.getInteger("connection_check_timeout_sec"));
            sink.setMaxRetries(config.getInteger("max_retries"));
            sink.setBatchSize(config.getInteger("batch_size"));
            sink.setBatchIntervalMs(config.getInteger("batch_interval_ms"));
        }

        return sink;
    }

    /**
     * 构建ConsoleSink
     */
    private Sink buildConsoleSink(JSONObject config) {
        Sink sink = new Sink();
        sink.setPluginName("Console");
        return sink;
    }

    /**
     * 构建通用Sink
     */
    private Sink buildGenericSink(String pluginType, JSONObject config) {
        Sink sink = new Sink();
        sink.setPluginName(pluginType);
        return sink;
    }

    /**
     * 序列化为JSON字符串
     */
    private String serializeToJson(SeaTunnelConfig config) {
        try {
            return objectMapper.writeValueAsString(config);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize SeaTunnel config", e);
            throw new RuntimeException("配置序列化失败", e);
        }
    }

    /**
     * 生成Zeta引擎配置（与generateConfig相同，用于兼容）
     */
    public String generateZetaConfig(String pipelineName, List<PipelineConnector> connectors) {
        return generateConfig(pipelineName, connectors);
    }
}
