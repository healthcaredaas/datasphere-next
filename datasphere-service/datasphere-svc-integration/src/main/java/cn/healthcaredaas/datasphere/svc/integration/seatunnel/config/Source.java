package cn.healthcaredaas.datasphere.svc.integration.seatunnel.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * SeaTunnel Source基类
 *
 * @author chenpan
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Source {

    @JsonProperty(value = "plugin_name", index = -10)
    private String pluginName;

    @JsonProperty(value = "result_table_name", index = -9)
    private String resultTableName;

    @JsonProperty(index = -8)
    private Integer parallelism;
}
