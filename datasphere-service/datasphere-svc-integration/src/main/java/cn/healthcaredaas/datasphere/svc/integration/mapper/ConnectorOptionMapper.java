package cn.healthcaredaas.datasphere.svc.integration.mapper;

import cn.healthcaredaas.datasphere.svc.integration.entity.ConnectorOption;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Connector配置项 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface ConnectorOptionMapper extends BaseMapper<ConnectorOption> {
}
