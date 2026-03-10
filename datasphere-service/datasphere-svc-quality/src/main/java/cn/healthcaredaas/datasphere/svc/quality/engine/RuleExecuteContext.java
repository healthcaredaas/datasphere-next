package cn.healthcaredaas.datasphere.svc.quality.engine;

import cn.healthcaredaas.datasphere.svc.quality.entity.QualityRule;
import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 规则执行上下文
 *
 * @author chenpan
 */
@Data
@Builder
public class RuleExecuteContext {

    /**
     * 质量规则
     */
    private QualityRule rule;

    /**
     * JDBC连接
     */
    private Connection connection;

    /**
     * 数据源
     */
    private DataSource dataSource;

    /**
     * JDBC模板
     */
    private JdbcTemplate jdbcTemplate;
}
