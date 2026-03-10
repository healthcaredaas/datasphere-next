package cn.healthcaredaas.datasphere.svc.datasource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 数据源服务启动类
 *
 * @author chenpan
 */
@SpringBootApplication
@EnableDiscoveryClient
public class DatasourceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatasourceServiceApplication.class, args);
    }
}
