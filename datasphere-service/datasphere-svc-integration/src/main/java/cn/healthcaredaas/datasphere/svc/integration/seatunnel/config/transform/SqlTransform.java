package cn.healthcaredaas.datasphere.svc.integration.seatunnel.config.transform;

import cn.healthcaredaas.datasphere.svc.integration.seatunnel.config.Transform;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SQL转换配置
 *
 * @author chenpan
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SqlTransform extends Transform {

    private String query;

    public SqlTransform() {
        super("sql");
    }
}
