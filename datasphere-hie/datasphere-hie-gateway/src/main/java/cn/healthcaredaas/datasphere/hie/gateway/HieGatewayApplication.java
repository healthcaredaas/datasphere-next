package cn.healthcaredaas.datasphere.hie.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * HIE网关服务启动类
 *
 * @author chenpan
 */
@SpringBootApplication
@EnableDiscoveryClient
public class HieGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(HieGatewayApplication.class, args);
    }
}
