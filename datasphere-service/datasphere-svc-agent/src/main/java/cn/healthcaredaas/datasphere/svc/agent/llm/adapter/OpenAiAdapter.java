package cn.healthcaredaas.datasphere.svc.agent.llm.adapter;

import cn.healthcaredaas.datasphere.svc.agent.entity.ModelConfig;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapter;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * OpenAI适配器 - 基于LangChain4j实现
 *
 * @author chenpan
 */
@Slf4j
@Component
public class OpenAiAdapter implements LlmAdapter {

    private static final String DEFAULT_MODEL = "gpt-4o";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);

    @Override
    public String getModelType() {
        return "GPT";
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
        String endpoint = config.getApiEndpoint();

        OpenAiChatModel.OpenAiChatModelBuilder builder = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(mapModelName(modelName))
                .temperature(0.7)
                .maxTokens(4096)
                .timeout(DEFAULT_TIMEOUT)
                .logRequests(true)
                .logResponses(true);

        // 支持自定义端点（用于兼容OpenAI API的服务）
        if (endpoint != null && !endpoint.isEmpty()) {
            builder.baseUrl(endpoint);
        }

        return builder.build();
    }

    /**
     * 创建流式Chat模型
     */
    private StreamingChatLanguageModel createStreamingChatModel(ModelConfig config) {
        String modelName = config.getModelName() != null ? config.getModelName() : DEFAULT_MODEL;
        String apiKey = config.getApiKey();
        String endpoint = config.getApiEndpoint();

        OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder builder = OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName(mapModelName(modelName))
                .temperature(0.7)
                .logRequests(true)
                .logResponses(true);

        if (endpoint != null && !endpoint.isEmpty()) {
            builder.baseUrl(endpoint);
        }

        return builder.build();
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
            log.error("OpenAI chat error: {}", e.getMessage(), e);
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
                    log.error("OpenAI stream error: {}", error.getMessage(), error);
                    callback.onError(error);
                }
            });

        } catch (Exception e) {
            log.error("OpenAI stream init error: {}", e.getMessage(), e);
            callback.onError(e);
        }
    }

    /**
     * 映射模型名称
     */
    private String mapModelName(String modelName) {
        if (modelName == null) {
            return DEFAULT_MODEL;
        }

        return switch (modelName.toLowerCase()) {
            case "gpt-4", "gpt4" -> "gpt-4-turbo";
            case "gpt-4o", "gpt4o" -> "gpt-4o";
            case "gpt-4o-mini" -> "gpt-4o-mini";
            case "gpt-3.5", "gpt-3.5-turbo" -> "gpt-3.5-turbo";
            default -> modelName;
        };
    }
}