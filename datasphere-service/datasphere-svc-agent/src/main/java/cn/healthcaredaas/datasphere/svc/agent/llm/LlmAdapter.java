package cn.healthcaredaas.datasphere.svc.agent.llm;

import cn.healthcaredaas.datasphere.svc.agent.entity.ModelConfig;

/**
 * LLM适配器接口
 *
 * @author chenpan
 */
public interface LlmAdapter {

    /**
     * 获取模型类型
     */
    String getModelType();

    /**
     * 测试连接
     */
    boolean testConnection();

    /**
     * 同步对话
     */
    String chat(String systemPrompt, String userMessage, ModelConfig config);

    /**
     * 流式对话
     */
    void chatStream(String systemPrompt, String userMessage, ModelConfig config, StreamCallback callback);

    /**
     * 流式回调接口
     */
    interface StreamCallback {
        void onToken(String token);
        void onComplete(String fullResponse);
        void onError(Throwable error);
    }
}