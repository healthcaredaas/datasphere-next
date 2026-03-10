package cn.healthcaredaas.datasphere.svc.quality;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 数据质量服务启动类
 *
 * @author chenpan
 */
@SpringBootApplication
@EnableDiscoveryClient
public class QualityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QualityServiceApplication.class, args);
    }
}
