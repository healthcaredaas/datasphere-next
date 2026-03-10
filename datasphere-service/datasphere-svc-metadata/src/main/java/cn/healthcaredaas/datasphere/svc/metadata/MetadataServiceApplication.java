package cn.healthcaredaas.datasphere.svc.metadata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 元数据服务启动类
 *
 * @author chenpan
 */
@SpringBootApplication
@EnableDiscoveryClient
public class MetadataServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetadataServiceApplication.class, args);
    }
}
