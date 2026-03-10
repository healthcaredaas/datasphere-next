package cn.healthcaredaas.datasphere.svc.agent.mapper;

import cn.healthcaredaas.datasphere.svc.agent.entity.ApiKey;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * API密钥 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface ApiKeyMapper extends BaseMapper<ApiKey> {
}