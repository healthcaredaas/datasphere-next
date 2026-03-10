package cn.healthcaredaas.datasphere.svc.master;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 主数据服务启动类
 *
 * @author chenpan
 */
@SpringBootApplication
@EnableDiscoveryClient
public class MasterDataServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MasterDataServiceApplication.class, args);
    }
}
