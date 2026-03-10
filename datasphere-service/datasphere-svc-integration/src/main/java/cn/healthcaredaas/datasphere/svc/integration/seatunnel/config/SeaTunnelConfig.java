package cn.healthcaredaas.datasphere.svc.integration.seatunnel.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * SeaTunnel配置根类
 *
 * @author chenpan
 */
@Data
public class SeaTunnelConfig {

    public SeaTunnelConfig() {
    }

    public SeaTunnelConfig(Env env, List<Source> sources, List<Transform> transforms, List<Sink> sinks) {
        this.env = env;
        this.sources = sources;
        this.transforms = transforms;
        this.sinks = sinks;
    }

    @JsonProperty(index = 0)
    private Env env;

    @JsonProperty("source")
    private List<Source> sources;

    @JsonProperty("transform")
    private List<Transform> transforms;

    @JsonProperty("sink")
    private List<Sink> sinks;
}
