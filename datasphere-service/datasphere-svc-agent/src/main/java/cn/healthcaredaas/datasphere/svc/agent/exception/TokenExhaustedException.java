package cn.healthcaredaas.datasphere.svc.agent.exception;

import cn.healthcaredaas.datasphere.svc.agent.constant.AgentConstants;

/**
 * Token额度异常
 *
 * @author chenpan
 */
public class TokenExhaustedException extends AgentException {

    private final String userId;
    private final long usedTokens;
    private final long maxTokens;

    public TokenExhaustedException(String userId, long usedTokens, long maxTokens) {
        super(AgentConstants.ERROR_CODE_TOKEN_EXHAUSTED,
                String.format("Token额度不足。用户: %s, 已用: %d, 上限: %d",
                        userId, usedTokens, maxTokens));
        this.userId = userId;
        this.usedTokens = usedTokens;
        this.maxTokens = maxTokens;
    }

    public String getUserId() {
        return userId;
    }

    public long getUsedTokens() {
        return usedTokens;
    }

    public long getMaxTokens() {
        return maxTokens;
    }
}