package cn.healthcaredaas.datasphere.svc.agent.service.impl;

import cn.healthcaredaas.datasphere.svc.agent.entity.AgentMessage;
import cn.healthcaredaas.datasphere.svc.agent.mapper.AgentMessageMapper;
import cn.healthcaredaas.datasphere.svc.agent.service.AgentMessageService;
import cn.healthcaredaas.datasphere.svc.agent.service.AgentSessionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Agent消息服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentMessageServiceImpl extends ServiceImpl<AgentMessageMapper, AgentMessage>
        implements AgentMessageService {

    private final AgentSessionService sessionService;

    @Override
    public IPage<AgentMessage> pageQuery(IPage<AgentMessage> page, AgentMessage params) {
        LambdaQueryWrapper<AgentMessage> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getSessionId())) {
            wrapper.eq(AgentMessage::getSessionId, params.getSessionId());
        }

        if (StringUtils.isNotBlank(params.getRole())) {
            wrapper.eq(AgentMessage::getRole, params.getRole());
        }

        if (StringUtils.isNotBlank(params.getContentType())) {
            wrapper.eq(AgentMessage::getContentType, params.getContentType());
        }

        wrapper.orderByAsc(AgentMessage::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<AgentMessage> listBySessionId(String sessionId) {
        LambdaQueryWrapper<AgentMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentMessage::getSessionId, sessionId)
                .orderByAsc(AgentMessage::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<AgentMessage> getRecentMessages(String sessionId, int limit) {
        LambdaQueryWrapper<AgentMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentMessage::getSessionId, sessionId)
                .orderByDesc(AgentMessage::getCreateTime)
                .last("LIMIT " + limit);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public AgentMessage addUserMessage(String sessionId, String content) {
        AgentMessage message = new AgentMessage();
        message.setSessionId(sessionId);
        message.setRole("USER");
        message.setContent(content);
        message.setContentType("TEXT");
        save(message);

        sessionService.incrementMessageCount(sessionId);
        return message;
    }

    @Override
    public AgentMessage addAssistantMessage(String sessionId, String content, String contentType,
                                            String toolCalls, String tokenUsage) {
        AgentMessage message = new AgentMessage();
        message.setSessionId(sessionId);
        message.setRole("ASSISTANT");
        message.setContent(content);
        message.setContentType(contentType);
        message.setToolCalls(toolCalls);
        message.setTokenUsage(tokenUsage);
        save(message);

        sessionService.incrementMessageCount(sessionId);
        return message;
    }
}