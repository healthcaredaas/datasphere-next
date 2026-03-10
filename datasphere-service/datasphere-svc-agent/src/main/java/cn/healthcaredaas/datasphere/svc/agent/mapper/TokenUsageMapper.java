package cn.healthcaredaas.datasphere.svc.agent.mapper;

import cn.healthcaredaas.datasphere.svc.agent.entity.TokenUsage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Token用量统计 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface TokenUsageMapper extends BaseMapper<TokenUsage> {
}