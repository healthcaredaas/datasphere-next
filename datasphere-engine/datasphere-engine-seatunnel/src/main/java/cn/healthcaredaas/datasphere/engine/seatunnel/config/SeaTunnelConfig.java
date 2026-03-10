package cn.healthcaredaas.datasphere.engine.seatunnel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * SeaTunnel 引擎配置
 *
 * @author chenpan
 */
@Data
@Component
@ConfigurationProperties(prefix = "datasphere.engine.seatunnel")
public class SeaTunnelConfig {

    /** SeaTunnel Home目录 */
    private String home = "/opt/seatunnel";

    /** 配置文件目录 */
    private String configDir = "${home}/config";

    /** 脚本目录 */
    private String binDir = "${home}/bin";

    /** 是否启用 */
    private boolean enabled = true;

    /** 默认引擎 */
    private String engine = "zeta";

    /** 并行度 */
    private int parallelism = 1;
}
