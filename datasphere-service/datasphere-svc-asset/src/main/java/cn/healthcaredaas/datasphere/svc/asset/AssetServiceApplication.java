package cn.healthcaredaas.datasphere.svc.asset;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 数据资产服务启动类
 *
 * @author chenpan
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AssetServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssetServiceApplication.class, args);
    }
}
