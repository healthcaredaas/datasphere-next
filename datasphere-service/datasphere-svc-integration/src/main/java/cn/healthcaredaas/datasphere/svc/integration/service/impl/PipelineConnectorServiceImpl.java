package cn.healthcaredaas.datasphere.svc.integration.service.impl;

import cn.healthcaredaas.datasphere.svc.integration.entity.PipelineConnector;
import cn.healthcaredaas.datasphere.svc.integration.mapper.PipelineConnectorMapper;
import cn.healthcaredaas.datasphere.svc.integration.service.PipelineConnectorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 管道连接器服务实现
 *
 * @author chenpan
 */
@Service
public class PipelineConnectorServiceImpl extends ServiceImpl<PipelineConnectorMapper, PipelineConnector>
        implements PipelineConnectorService {
}
