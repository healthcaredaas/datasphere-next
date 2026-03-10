package cn.healthcaredaas.datasphere.svc.integration.runner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 数据集成作业运行器启动类
 *
 * @author chenpan
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.healthcaredaas.datasphere.api.integration.feign")
public class JobRunnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobRunnerApplication.class, args);
    }
}
