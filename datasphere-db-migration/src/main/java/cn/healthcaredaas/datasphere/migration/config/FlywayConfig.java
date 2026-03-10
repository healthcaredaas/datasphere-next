package cn.healthcaredaas.datasphere.migration.config;

import cn.healthcaredaas.datasphere.migration.callback.MigrationLogCallback;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Flyway 配置类
 *
 * @author chenpan
 * @since 2.0.0
 */
@Configuration
@RequiredArgsConstructor
public class FlywayConfig {

    private final MigrationLogCallback migrationLogCallback;

    /**
     * 自定义 Flyway 配置
     */
    @Bean
    public FlywayConfigurationCustomizer flywayConfigurationCustomizer() {
        return configuration -> {
            FluentConfiguration fluentConfiguration = (FluentConfiguration) configuration;
            fluentConfiguration.callbacks(migrationLogCallback);
        };
    }
}