package cn.healthcaredaas.datasphere.svc.integration.seatunnel.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * SeaTunnel Env配置
 *
 * @author chenpan
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Env {

    @JsonProperty("job.name")
    private String name;

    @JsonProperty(value = "job.mode", defaultValue = "BATCH")
    private String jobMode;

    @JsonProperty("checkpoint.interval")
    private Long checkpointInterval;

    @JsonProperty("parallelism")
    private Integer parallelism;
}
