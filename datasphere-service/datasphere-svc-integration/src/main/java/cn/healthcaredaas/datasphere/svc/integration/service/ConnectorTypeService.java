package cn.healthcaredaas.datasphere.svc.integration.service;

import cn.healthcaredaas.datasphere.svc.integration.entity.ConnectorType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * Connector类型服务接口
 *
 * @author chenpan
 */
public interface ConnectorTypeService extends IService<ConnectorType> {

    IPage<ConnectorType> pageQuery(IPage<ConnectorType> page, ConnectorType params);

    List<ConnectorType> listByType(String connectorType);
}
