package cn.healthcaredaas.datasphere.svc.agent.service;

import cn.healthcaredaas.datasphere.svc.agent.entity.ApiKey;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * API密钥服务接口
 *
 * @author chenpan
 */
public interface ApiKeyService extends IService<ApiKey> {

    /**
     * 分页查询API密钥
     */
    IPage<ApiKey> pageQuery(IPage<ApiKey> page, ApiKey params);

    /**
     * 根据API密钥查询
     */
    ApiKey getByApiKey(String apiKey);

    /**
     * 生成新的API密钥
     */
    ApiKey generateApiKey(String keyName, String userId, String tenantId, List<String> permissions, Integer rateLimit);

    /**
     * 验证API密钥
     */
    boolean validateApiKey(String apiKey, String permission);

    /**
     * 更新最后使用时间
     */
    void updateLastUsedTime(String apiKey);

    /**
     * 吊销API密钥
     */
    void revokeApiKey(String apiKey);
}