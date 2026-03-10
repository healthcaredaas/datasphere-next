package cn.healthcaredaas.datasphere.svc.agent.service.impl;

import cn.healthcaredaas.datasphere.svc.agent.entity.ApiKey;
import cn.healthcaredaas.datasphere.svc.agent.mapper.ApiKeyMapper;
import cn.healthcaredaas.datasphere.svc.agent.service.ApiKeyService;
import cn.healthcaredaas.datasphere.svc.agent.service.EncryptService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * API密钥服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl extends ServiceImpl<ApiKeyMapper, ApiKey>
        implements ApiKeyService {

    private final EncryptService encryptService;

    @Override
    public IPage<ApiKey> pageQuery(IPage<ApiKey> page, ApiKey params) {
        LambdaQueryWrapper<ApiKey> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getKeyName())) {
            wrapper.like(ApiKey::getKeyName, params.getKeyName());
        }

        if (StringUtils.isNotBlank(params.getUserId())) {
            wrapper.eq(ApiKey::getUserId, params.getUserId());
        }

        if (StringUtils.isNotBlank(params.getTenantId())) {
            wrapper.eq(ApiKey::getTenantId, params.getTenantId());
        }

        if (params.getStatus() != null) {
            wrapper.eq(ApiKey::getStatus, params.getStatus());
        }

        wrapper.orderByDesc(ApiKey::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public ApiKey getByApiKey(String apiKey) {
        if (StringUtils.isBlank(apiKey)) {
            return null;
        }

        // 遍历所有启用的API密钥进行匹配(因为密钥是加密存储的)
        LambdaQueryWrapper<ApiKey> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApiKey::getStatus, 1);
        List<ApiKey> keys = baseMapper.selectList(wrapper);

        for (ApiKey key : keys) {
            if (encryptService.matches(apiKey, key.getApiKey())) {
                return key;
            }
        }

        return null;
    }

    @Override
    public ApiKey generateApiKey(String keyName, String userId, String tenantId,
                                 List<String> permissions, Integer rateLimit) {
        // 生成原始密钥
        String plainApiKey = "sk-" + tenantId + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 32);

        ApiKey apiKey = new ApiKey();
        apiKey.setKeyName(keyName);
        // 加密存储密钥
        apiKey.setApiKey(encryptService.encrypt(plainApiKey));
        apiKey.setUserId(userId);
        apiKey.setTenantId(tenantId);
        apiKey.setPermissions(JSON.toJSONString(permissions));
        apiKey.setRateLimit(rateLimit != null ? rateLimit : 100);
        apiKey.setStatus(1);
        save(apiKey);

        // 返回原始密钥(仅此一次机会看到明文)
        // 注意：这里将加密后的密钥替换为明文返回给调用者
        // 调用者需要保存这个明文密钥
        ApiKey result = new ApiKey();
        result.setId(apiKey.getId());
        result.setKeyName(apiKey.getKeyName());
        result.setApiKey(plainApiKey); // 返回明文密钥
        result.setUserId(apiKey.getUserId());
        result.setTenantId(apiKey.getTenantId());
        result.setPermissions(apiKey.getPermissions());
        result.setRateLimit(apiKey.getRateLimit());
        result.setStatus(apiKey.getStatus());
        result.setCreateTime(apiKey.getCreateTime());

        log.info("生成API密钥成功 - keyName: {}, userId: {}", keyName, userId);
        return result;
    }

    @Override
    public boolean validateApiKey(String apiKey, String permission) {
        ApiKey key = getByApiKey(apiKey);
        if (key == null) {
            return false;
        }

        // 检查过期时间
        if (key.getExpiredAt() != null && key.getExpiredAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        // 检查权限
        if (StringUtils.isNotBlank(permission) && StringUtils.isNotBlank(key.getPermissions())) {
            List<String> permissions = JSON.parseArray(key.getPermissions(), String.class);
            if (!permissions.contains(permission) && !permissions.contains("*")) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void updateLastUsedTime(String apiKey) {
        ApiKey key = getByApiKey(apiKey);
        if (key != null) {
            key.setLastUsedAt(LocalDateTime.now());
            updateById(key);
            log.debug("更新API密钥最后使用时间 - keyId: {}", key.getId());
        }
    }

    @Override
    public void revokeApiKey(String apiKey) {
        ApiKey key = getByApiKey(apiKey);
        if (key != null) {
            key.setStatus(0);
            updateById(key);
            log.info("吊销API密钥成功 - keyId: {}", key.getId());
        }
    }
}