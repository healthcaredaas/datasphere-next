package cn.healthcaredaas.datasphere.migration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * DataSphere 数据库迁移服务启动类
 *
 * <p>基于 Flyway 的数据库版本管理服务，用于：</p>
 * <ul>
 *     <li>数据库表结构的版本化管理</li>
 *     <li>数据库脚本的自动化迁移</li>
 *     <li>多环境数据库初始化</li>
 * </ul>
 *
 * @author chenpan
 * @since 2.0.0
 */
@Slf4j
@SpringBootApplication
public class MigrationApplication {

    public static void main(String[] args) {
        log.info("==========================================================");
        log.info("  DataSphere 数据库迁移服务启动中...");
        log.info("==========================================================");

        SpringApplication.run(MigrationApplication.class, args);

        log.info("==========================================================");
        log.info("  DataSphere 数据库迁移服务启动完成！");
        log.info("==========================================================");
    }
}