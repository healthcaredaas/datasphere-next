package cn.healthcaredaas.datasphere.svc.agent.service;

import cn.healthcaredaas.datasphere.svc.agent.entity.AgentSession;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * Agent会话服务接口
 *
 * @author chenpan
 */
public interface AgentSessionService extends IService<AgentSession> {

    /**
     * 分页查询会话
     */
    IPage<AgentSession> pageQuery(IPage<AgentSession> page, AgentSession params);

    /**
     * 获取用户的会话列表
     */
    List<AgentSession> listByUserId(String userId);

    /**
     * 创建新会话
     */
    AgentSession createSession(String title, String modelId, String userId, String tenantId);

    /**
     * 更新会话活跃时间
     */
    void updateActiveTime(String sessionId);

    /**
     * 归档会话
     */
    void archiveSession(String sessionId);

    /**
     * 增加消息计数
     */
    void incrementMessageCount(String sessionId);
}