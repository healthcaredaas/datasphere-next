package cn.healthcaredaas.datasphere.svc.integration.service;

import cn.healthcaredaas.datasphere.svc.integration.entity.ConnectorOption;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * Connector配置项服务接口
 *
 * @author chenpan
 */
public interface ConnectorOptionService extends IService<ConnectorOption> {

    IPage<ConnectorOption> pageQuery(IPage<ConnectorOption> page, ConnectorOption params);

    List<ConnectorOption> listByConnectorType(String connectorTypeId);
}
