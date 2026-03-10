package cn.healthcaredaas.datasphere.svc.integration.seatunnel.config.source;

import cn.healthcaredaas.datasphere.svc.integration.seatunnel.config.Source;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Jdbc Source配置
 *
 * @author chenpan
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JdbcSource extends Source {

    public JdbcSource() {
        super.setPluginName("jdbc");
    }

    private String url;
    private String driver;
    private String user;
    private String password;
    private String query;

    @JsonProperty(value = "connection_check_timeout_sec")
    private Integer connectionCheckTimeoutSec;

    @JsonProperty("partition_column")
    private String partitionColumn;

    @JsonProperty("partition_upper_bound")
    private String partitionUpperBound;

    @JsonProperty("partition_lower_bound")
    private String partitionLowerBound;

    @JsonProperty("partition_num")
    private Integer partitionNum;

    @JsonProperty("fetch_size")
    private Integer fetchSize;
}
