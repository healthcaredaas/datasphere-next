package cn.healthcaredaas.datasphere.svc.agent.mapper;

import cn.healthcaredaas.datasphere.svc.agent.entity.AgentMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Agent消息 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface AgentMessageMapper extends BaseMapper<AgentMessage> {
}