package cn.healthcaredaas.datasphere.svc.agent.llm;

import cn.healthcaredaas.datasphere.svc.agent.entity.ModelConfig;
import cn.healthcaredaas.datasphere.svc.agent.llm.adapter.ClaudeAdapter;
import cn.healthcaredaas.datasphere.svc.agent.llm.adapter.OpenAiAdapter;
import cn.healthcaredaas.datasphere.svc.agent.llm.adapter.QwenAdapter;
import cn.healthcaredaas.datasphere.svc.agent.llm.adapter.LocalModelAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LLM适配器工厂
 *
 * @author chenpan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LlmAdapterFactory {

    private final List<LlmAdapter> adapters;
    private final Map<String, LlmAdapter> adapterMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (LlmAdapter adapter : adapters) {
            adapterMap.put(adapter.getModelType(), adapter);
        }
        log.info("已加载 {} 个LLM适配器: {}", adapters.size(), adapterMap.keySet());
    }

    /**
     * 获取适配器
     */
    public LlmAdapter getAdapter(ModelConfig config) {
        LlmAdapter adapter = adapterMap.get(config.getModelType());
        if (adapter == null) {
            throw new IllegalArgumentException("不支持的模型类型: " + config.getModelType());
        }
        return adapter;
    }

    /**
     * 获取适配器
     */
    public LlmAdapter getAdapter(String modelType) {
        LlmAdapter adapter = adapterMap.get(modelType);
        if (adapter == null) {
            throw new IllegalArgumentException("不支持的模型类型: " + modelType);
        }
        return adapter;
    }

    /**
     * 支持的模型类型
     */
    public List<String> getSupportedModelTypes() {
        return List.copyOf(adapterMap.keySet());
    }
}