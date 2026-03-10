package cn.healthcaredaas.datasphere.migration.callback;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.springframework.stereotype.Component;

/**
 * Flyway 迁移回调 - 日志记录
 *
 * <p>在数据库迁移执行的各个阶段打印日志信息，便于监控和问题排查。</p>
 *
 * @author chenpan
 * @since 2.0.0
 */
@Slf4j
@Component
public class MigrationLogCallback implements Callback {

    @Override
    public boolean supports(Event event, Context context) {
        return true;
    }

    @Override
    public boolean canHandleInTransaction(Event event, Context context) {
        return true;
    }

    @Override
    public void handle(Event event, Context context) {
        switch (event) {
            case BEFORE_MIGRATE:
                log.info("==========================================================");
                log.info("  [Flyway] 开始执行数据库迁移...");
                log.info("  数据库: {}", getJdbcUrl(context));
                log.info("==========================================================");
                break;

            case BEFORE_EACH_MIGRATE:
                if (context.getMigrationInfo() != null) {
                    log.info("  [Flyway] 执行脚本: {} - {}",
                            context.getMigrationInfo().getVersion(),
                            context.getMigrationInfo().getDescription());
                }
                break;

            case AFTER_EACH_MIGRATE:
                if (context.getMigrationInfo() != null) {
                    log.info("  [Flyway] 脚本执行完成: {} (耗时: {}ms)",
                            context.getMigrationInfo().getVersion(),
                            context.getMigrationInfo().getExecutionTime());
                }
                break;

            case AFTER_MIGRATE:
                log.info("==========================================================");
                log.info("  [Flyway] 数据库迁移执行完成！");
                log.info("  成功执行脚本数: {}", getSuccessCount(context));
                log.info("==========================================================");
                break;

            case AFTER_MIGRATE_ERROR:
                log.error("==========================================================");
                log.error("  [Flyway] 数据库迁移执行失败！");
                log.error("==========================================================");
                break;

            case BEFORE_VALIDATE:
                log.info("  [Flyway] 开始验证数据库迁移状态...");
                break;

            case AFTER_VALIDATE:
                log.info("  [Flyway] 数据库迁移状态验证完成");
                break;

            case AFTER_VALIDATE_ERROR:
                log.error("  [Flyway] 数据库迁移状态验证失败！");
                break;

            case BEFORE_BASELINE:
                log.info("  [Flyway] 开始创建基线...");
                break;

            case AFTER_BASELINE:
                log.info("  [Flyway] 基线创建完成");
                break;

            case BEFORE_REPAIR:
                log.info("  [Flyway] 开始修复迁移历史...");
                break;

            case AFTER_REPAIR:
                log.info("  [Flyway] 迁移历史修复完成");
                break;

            default:
                // 其他事件不做处理
                break;
        }
    }

    @Override
    public String getCallbackName() {
        return "MigrationLogCallback";
    }

    /**
     * 获取数据库连接URL
     */
    private String getJdbcUrl(Context context) {
        try {
            return context.getConnection().getMetaData().getURL();
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * 获取成功执行的迁移数量
     */
    private int getSuccessCount(Context context) {
        try {
            var migrationInfos = context.getMigrationInfoService().all();
            int count = 0;
            for (var info : migrationInfos) {
                if (info.getState().isApplied()) {
                    count++;
                }
            }
            return count;
        } catch (Exception e) {
            return 0;
        }
    }
}