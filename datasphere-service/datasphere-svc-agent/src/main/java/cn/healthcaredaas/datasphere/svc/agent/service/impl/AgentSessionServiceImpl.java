package cn.healthcaredaas.datasphere.svc.agent.service.impl;

import cn.healthcaredaas.datasphere.svc.agent.entity.AgentSession;
import cn.healthcaredaas.datasphere.svc.agent.mapper.AgentSessionMapper;
import cn.healthcaredaas.datasphere.svc.agent.service.AgentSessionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Agent会话服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentSessionServiceImpl extends ServiceImpl<AgentSessionMapper, AgentSession>
        implements AgentSessionService {

    @Override
    public IPage<AgentSession> pageQuery(IPage<AgentSession> page, AgentSession params) {
        LambdaQueryWrapper<AgentSession> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getTitle())) {
            wrapper.like(AgentSession::getTitle, params.getTitle());
        }

        if (StringUtils.isNotBlank(params.getUserId())) {
            wrapper.eq(AgentSession::getUserId, params.getUserId());
        }

        if (StringUtils.isNotBlank(params.getTenantId())) {
            wrapper.eq(AgentSession::getTenantId, params.getTenantId());
        }

        if (StringUtils.isNotBlank(params.getStatus())) {
            wrapper.eq(AgentSession::getStatus, params.getStatus());
        }

        wrapper.orderByDesc(AgentSession::getLastActiveTime);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<AgentSession> listByUserId(String userId) {
        LambdaQueryWrapper<AgentSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentSession::getUserId, userId)
                .eq(AgentSession::getStatus, "ACTIVE")
                .orderByDesc(AgentSession::getLastActiveTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public AgentSession createSession(String title, String modelId, String userId, String tenantId) {
        AgentSession session = new AgentSession();
        session.setTitle(title);
        session.setModelId(modelId);
        session.setUserId(userId);
        session.setTenantId(tenantId);
        session.setStatus("ACTIVE");
        session.setMessageCount(0);
        session.setLastActiveTime(LocalDateTime.now());
        save(session);
        return session;
    }

    @Override
    public void updateActiveTime(String sessionId) {
        AgentSession session = new AgentSession();
        session.setId(sessionId);
        session.setLastActiveTime(LocalDateTime.now());
        updateById(session);
    }

    @Override
    public void archiveSession(String sessionId) {
        AgentSession session = new AgentSession();
        session.setId(sessionId);
        session.setStatus("ARCHIVED");
        updateById(session);
    }

    @Override
    public void incrementMessageCount(String sessionId) {
        AgentSession session = getById(sessionId);
        if (session != null) {
            session.setMessageCount(session.getMessageCount() + 1);
            session.setLastActiveTime(LocalDateTime.now());
            updateById(session);
        }
    }
}