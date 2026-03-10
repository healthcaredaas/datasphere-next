package cn.healthcaredaas.datasphere.svc.agent.service.impl;

import cn.healthcaredaas.datasphere.svc.agent.entity.ModelConfig;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapter;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapterFactory;
import cn.healthcaredaas.datasphere.svc.agent.mapper.ModelConfigMapper;
import cn.healthcaredaas.datasphere.svc.agent.service.ModelConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 模型配置服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ModelConfigServiceImpl extends ServiceImpl<ModelConfigMapper, ModelConfig>
        implements ModelConfigService {

    private final LlmAdapterFactory llmAdapterFactory;

    @Override
    public IPage<ModelConfig> pageQuery(IPage<ModelConfig> page, ModelConfig params) {
        LambdaQueryWrapper<ModelConfig> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getModelName())) {
            wrapper.like(ModelConfig::getModelName, params.getModelName());
        }

        if (StringUtils.isNotBlank(params.getModelType())) {
            wrapper.eq(ModelConfig::getModelType, params.getModelType());
        }

        if (params.getStatus() != null) {
            wrapper.eq(ModelConfig::getStatus, params.getStatus());
        }

        wrapper.orderByDesc(ModelConfig::getPriority);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<ModelConfig> listEnabled() {
        LambdaQueryWrapper<ModelConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelConfig::getStatus, 1)
                .orderByDesc(ModelConfig::getPriority);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public ModelConfig getByType(String modelType) {
        LambdaQueryWrapper<ModelConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelConfig::getModelType, modelType)
                .eq(ModelConfig::getStatus, 1)
                .orderByDesc(ModelConfig::getPriority)
                .last("LIMIT 1");
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public ModelConfig getDefaultModel() {
        LambdaQueryWrapper<ModelConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelConfig::getStatus, 1)
                .orderByDesc(ModelConfig::getPriority)
                .last("LIMIT 1");
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public boolean testConnection(String modelId) {
        ModelConfig config = getById(modelId);
        if (config == null || config.getStatus() != 1) {
            return false;
        }

        try {
            LlmAdapter adapter = llmAdapterFactory.getAdapter(config);
            return adapter.testConnection();
        } catch (Exception e) {
            log.error("测试模型连接失败: {}", e.getMessage());
            return false;
        }
    }
}