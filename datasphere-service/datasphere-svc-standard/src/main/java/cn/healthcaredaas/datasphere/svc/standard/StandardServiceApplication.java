package cn.healthcaredaas.datasphere.svc.standard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 数据标准服务启动类
 *
 * @author chenpan
 */
@SpringBootApplication
@EnableDiscoveryClient
public class StandardServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StandardServiceApplication.class, args);
    }
}
