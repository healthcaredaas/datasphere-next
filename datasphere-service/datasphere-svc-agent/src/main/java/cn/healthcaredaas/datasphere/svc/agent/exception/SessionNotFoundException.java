package cn.healthcaredaas.datasphere.svc.agent.exception;

import cn.healthcaredaas.datasphere.svc.agent.constant.AgentConstants;

/**
 * 会话不存在异常
 *
 * @author chenpan
 */
public class SessionNotFoundException extends AgentException {

    public SessionNotFoundException(String sessionId) {
        super(AgentConstants.ERROR_CODE_SESSION_NOT_FOUND,
                "会话不存在: " + sessionId);
    }
}