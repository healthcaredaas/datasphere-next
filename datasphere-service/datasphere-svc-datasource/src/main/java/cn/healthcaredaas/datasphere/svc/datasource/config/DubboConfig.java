package cn.healthcaredaas.datasphere.svc.datasource.config;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Dubbo 配置
 *
 * @author chenpan
 */
@Configuration
public class DubboConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${nacos.server-addr:localhost:8848}")
    private String nacosServerAddr;

    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig config = new ApplicationConfig();
        config.setName(applicationName);
        return config;
    }

    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig config = new RegistryConfig();
        config.setAddress("nacos://" + nacosServerAddr);
        return config;
    }

    @Bean
    public ProtocolConfig protocolConfig() {
        ProtocolConfig config = new ProtocolConfig();
        config.setName("dubbo");
        config.setPort(-1); // 自动分配端口
        return config;
    }
}
