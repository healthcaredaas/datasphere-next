package cn.healthcaredaas.datasphere.svc.agent.service;

import cn.healthcaredaas.datasphere.svc.agent.entity.ModelConfig;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 模型配置服务接口
 *
 * @author chenpan
 */
public interface ModelConfigService extends IService<ModelConfig> {

    /**
     * 分页查询模型配置
     */
    IPage<ModelConfig> pageQuery(IPage<ModelConfig> page, ModelConfig params);

    /**
     * 获取启用的模型列表
     */
    List<ModelConfig> listEnabled();

    /**
     * 根据类型获取模型
     */
    ModelConfig getByType(String modelType);

    /**
     * 获取默认模型
     */
    ModelConfig getDefaultModel();

    /**
     * 测试模型连接
     */
    boolean testConnection(String modelId);
}