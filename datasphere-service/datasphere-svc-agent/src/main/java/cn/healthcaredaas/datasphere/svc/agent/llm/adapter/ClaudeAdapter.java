package cn.healthcaredaas.datasphere.svc.agent.llm.adapter;

import cn.healthcaredaas.datasphere.svc.agent.entity.ModelConfig;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapter;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Claude适配器 - 基于LangChain4j实现
 *
 * @author chenpan
 */
@Slf4j
@Component
public class ClaudeAdapter implements LlmAdapter {

    private static final String DEFAULT_MODEL = "claude-3-5-sonnet-20241022";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);

    @Override
    public String getModelType() {
        return "CLAUDE";
    }

    @Override
    public boolean testConnection() {
        return true;
    }

    /**
     * 创建Chat模型
     */
    private ChatLanguageModel createChatModel(ModelConfig config) {
        String modelName = config.getModelName() != null ? config.getModelName() : DEFAULT_MODEL;
        String apiKey = config.getApiKey();

        return AnthropicChatModel.builder()
                .apiKey(apiKey)
                .modelName(mapModelName(modelName))
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
        String apiKey = config.getApiKey();

        return AnthropicStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName(mapModelName(modelName))
                .temperature(0.7)
                .maxTokens(4096)
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
            log.error("Claude chat error: {}", e.getMessage(), e);
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
                    log.error("Claude stream error: {}", error.getMessage(), error);
                    callback.onError(error);
                }
            });

        } catch (Exception e) {
            log.error("Claude stream init error: {}", e.getMessage(), e);
            callback.onError(e);
        }
    }

    /**
     * 映射模型名称到Anthropic格式
     */
    private String mapModelName(String modelName) {
        if (modelName == null) {
            return DEFAULT_MODEL;
        }

        // 处理常见的模型名称变体
        return switch (modelName.toLowerCase()) {
            case "claude-3-opus", "opus" -> "claude-3-opus-20240229";
            case "claude-3-sonnet", "sonnet" -> "claude-3-5-sonnet-20241022";
            case "claude-3-haiku", "haiku" -> "claude-3-5-haiku-20241022";
            case "claude-3.5-sonnet", "claude-3-5-sonnet" -> "claude-3-5-sonnet-20241022";
            case "claude-3.5-haiku", "claude-3-5-haiku" -> "claude-3-5-haiku-20241022";
            default -> modelName;
        };
    }
}