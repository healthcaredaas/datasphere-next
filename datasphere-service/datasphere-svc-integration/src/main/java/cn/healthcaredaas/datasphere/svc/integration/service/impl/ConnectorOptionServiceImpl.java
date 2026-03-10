package cn.healthcaredaas.datasphere.svc.integration.service.impl;

import cn.healthcaredaas.datasphere.svc.integration.entity.ConnectorOption;
import cn.healthcaredaas.datasphere.svc.integration.mapper.ConnectorOptionMapper;
import cn.healthcaredaas.datasphere.svc.integration.service.ConnectorOptionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Connector配置项服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectorOptionServiceImpl extends ServiceImpl<ConnectorOptionMapper, ConnectorOption>
        implements ConnectorOptionService {

    @Override
    public IPage<ConnectorOption> pageQuery(IPage<ConnectorOption> page, ConnectorOption params) {
        LambdaQueryWrapper<ConnectorOption> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getConnectorTypeId())) {
            wrapper.eq(ConnectorOption::getConnectorTypeId, params.getConnectorTypeId());
        }

        if (StringUtils.isNotBlank(params.getOptionName())) {
            wrapper.like(ConnectorOption::getOptionName, params.getOptionName());
        }

        wrapper.orderByAsc(ConnectorOption::getSortNo);

        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<ConnectorOption> listByConnectorType(String connectorTypeId) {
        return lambdaQuery()
                .eq(ConnectorOption::getConnectorTypeId, connectorTypeId)
                .eq(ConnectorOption::getStatus, 1)
                .orderByAsc(ConnectorOption::getSortNo)
                .list();
    }
}
