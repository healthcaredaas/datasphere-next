package cn.healthcaredaas.datasphere.svc.integration.service.impl;

import cn.healthcaredaas.datasphere.svc.integration.entity.ConnectorType;
import cn.healthcaredaas.datasphere.svc.integration.mapper.ConnectorTypeMapper;
import cn.healthcaredaas.datasphere.svc.integration.service.ConnectorTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Connector类型服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectorTypeServiceImpl extends ServiceImpl<ConnectorTypeMapper, ConnectorType>
        implements ConnectorTypeService {

    @Override
    public IPage<ConnectorType> pageQuery(IPage<ConnectorType> page, ConnectorType params) {
        LambdaQueryWrapper<ConnectorType> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getConnectorName())) {
            wrapper.like(ConnectorType::getConnectorName, params.getConnectorName());
        }

        if (StringUtils.isNotBlank(params.getConnectorType())) {
            wrapper.eq(ConnectorType::getConnectorType, params.getConnectorType());
        }

        wrapper.orderByDesc(ConnectorType::getCreateTime);

        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<ConnectorType> listByType(String connectorType) {
        return lambdaQuery()
                .eq(ConnectorType::getConnectorType, connectorType)
                .eq(ConnectorType::getStatus, 1)
                .orderByDesc(ConnectorType::getCreateTime)
                .list();
    }
}
