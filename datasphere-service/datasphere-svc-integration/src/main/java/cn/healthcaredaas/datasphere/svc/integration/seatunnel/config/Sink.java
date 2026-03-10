package cn.healthcaredaas.datasphere.svc.integration.seatunnel.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * SeaTunnel Sink基类
 *
 * @author chenpan
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sink {

    @JsonProperty(value = "plugin_name", index = -10)
    private String pluginName;

    @JsonProperty(value = "source_table_name", index = -9)
    private String sourceTableName;

    @JsonProperty(index = -8)
    private Integer parallelism;
}
