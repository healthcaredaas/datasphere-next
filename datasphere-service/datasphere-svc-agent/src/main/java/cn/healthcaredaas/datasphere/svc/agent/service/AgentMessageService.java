package cn.healthcaredaas.datasphere.svc.agent.service;

import cn.healthcaredaas.datasphere.svc.agent.entity.AgentMessage;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * Agent消息服务接口
 *
 * @author chenpan
 */
public interface AgentMessageService extends IService<AgentMessage> {

    /**
     * 分页查询消息
     */
    IPage<AgentMessage> pageQuery(IPage<AgentMessage> page, AgentMessage params);

    /**
     * 获取会话的消息列表
     */
    List<AgentMessage> listBySessionId(String sessionId);

    /**
     * 获取会话最近的N条消息
     */
    List<AgentMessage> getRecentMessages(String sessionId, int limit);

    /**
     * 添加用户消息
     */
    AgentMessage addUserMessage(String sessionId, String content);

    /**
     * 添加助手消息
     */
    AgentMessage addAssistantMessage(String sessionId, String content, String contentType, String toolCalls, String tokenUsage);
}