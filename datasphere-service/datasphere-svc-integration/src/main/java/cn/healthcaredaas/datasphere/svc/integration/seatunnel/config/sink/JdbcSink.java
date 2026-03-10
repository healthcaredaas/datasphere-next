package cn.healthcaredaas.datasphere.svc.integration.seatunnel.config.sink;

import cn.healthcaredaas.datasphere.svc.integration.seatunnel.config.Sink;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Jdbc Sink配置
 *
 * @author chenpan
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JdbcSink extends Sink {

    public JdbcSink() {
        super.setPluginName("jdbc");
    }

    private String url;
    private String driver;
    private String user;
    private String password;
    private String query;
    private String database;
    private String table;

    @JsonProperty(value = "primary_keys")
    private String[] primaryKeys;

    @JsonProperty(value = "support_upsert_by_query_primary_key_exist")
    private Boolean supportUpsertByQueryPrimaryKeyExist;

    @JsonProperty(value = "connection_check_timeout_sec")
    private Integer connectionCheckTimeoutSec;

    @JsonProperty(value = "max_retries")
    private Integer maxRetries;

    @JsonProperty(value = "batch_size")
    private Integer batchSize;

    @JsonProperty(value = "batch_interval_ms")
    private Integer batchIntervalMs;

    @JsonProperty(value = "is_exactly_once")
    private Boolean isExactlyOnce;

    @JsonProperty(value = "xa_data_source_class_name")
    private String xaDataSourceClassName;

    @JsonProperty(value = "auto_commit")
    private Boolean autoCommit;
}
