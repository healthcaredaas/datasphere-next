package cn.healthcaredaas.datasphere.svc.agent.llm.adapter;

import cn.healthcaredaas.datasphere.svc.agent.entity.ModelConfig;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapter;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.localai.LocalAiChatModel;
import dev.langchain4j.model.localai.LocalAiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地模型适配器 - 支持Ollama、LocalAI等本地部署模型
 * 基于LangChain4j实现
 *
 * @author chenpan
 */
@Slf4j
@Component
public class LocalModelAdapter implements LlmAdapter {

    private static final String DEFAULT_MODEL = "llama3";
    private static final String DEFAULT_ENDPOINT = "http://localhost:11434/v1";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(120);

    @Override
    public String getModelType() {
        return "LOCAL";
    }

    @Override
    public boolean testConnection() {
        return true;
    }

    /**
     * 获取API端点
     */
    private String getEndpoint(ModelConfig config) {
        String endpoint = config.getApiEndpoint();
        if (endpoint == null || endpoint.isEmpty()) {
            return DEFAULT_ENDPOINT;
        }
        // 确保端点格式正确
        if (!endpoint.endsWith("/v1")) {
            endpoint = endpoint.rstrip() + "/v1";
        }
        return endpoint;
    }

    /**
     * 创建Chat模型
     */
    private ChatLanguageModel createChatModel(ModelConfig config) {
        String modelName = config.getModelName() != null ? config.getModelName() : DEFAULT_MODEL;
        String endpoint = getEndpoint(config);

        // 本地模型通常不需要API Key，使用占位符
        String apiKey = config.getApiKey() != null ? config.getApiKey() : "local";

        return LocalAiChatModel.builder()
                .baseUrl(endpoint)
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.7)
                .maxTokens(4096)
                .timeout(DEFAULT_TIMEOUT)
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    /**
     * 创建流式Chat模型
     */
    private StreamingChatLanguageModel createStreamingChatModel(ModelConfig config) {
        String modelName = config.getModelName() != null ? config.getModelName() : DEFAULT_MODEL;
        String endpoint = getEndpoint(config);
        String apiKey = config.getApiKey() != null ? config.getApiKey() : "local";

        return LocalAiStreamingChatModel.builder()
                .baseUrl(endpoint)
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.7)
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Override
    public String chat(String systemPrompt, String userMessage, ModelConfig config) {
        try {
            ChatLanguageModel model = createChatModel(config);

            List<ChatMessage> messages = new ArrayList<>();
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                messages.add(SystemMessage.from(systemPrompt));
            }
            messages.add(UserMessage.from(userMessage));

            Response<AiMessage> response = model.generate(messages);
            return response.content().text();

        } catch (Exception e) {
            log.error("Local model chat error: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public void chatStream(String systemPrompt, String userMessage, ModelConfig config, StreamCallback callback) {
        try {
            StreamingChatLanguageModel model = createStreamingChatModel(config);

            List<ChatMessage> messages = new ArrayList<>();
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                messages.add(SystemMessage.from(systemPrompt));
            }
            messages.add(UserMessage.from(userMessage));

            StringBuilder fullResponse = new StringBuilder();

            model.generate(messages, new dev.langchain4j.model.output.StreamingResponseHandler<AiMessage>() {
                @Override
                public void onNext(String token) {
                    fullResponse.append(token);
                    callback.onToken(token);
                }

                @Override
                public void onComplete(Response<AiMessage> response) {
                    callback.onComplete(fullResponse.toString());
                }

                @Override
                public void onError(Throwable error) {
                    log.error("Local model stream error: {}", error.getMessage(), error);
                    callback.onError(error);
                }
            });

        } catch (Exception e) {
            log.error("Local model stream init error: {}", e.getMessage(), e);
            callback.onError(e);
        }
    }
}