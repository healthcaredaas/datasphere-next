package cn.healthcaredaas.datasphere.svc.agent.mapper;

import cn.healthcaredaas.datasphere.svc.agent.entity.AgentSession;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Agent会话 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface AgentSessionMapper extends BaseMapper<AgentSession> {
}