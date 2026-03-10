package cn.healthcaredaas.datasphere.svc.agent.controller;

import cn.healthcaredaas.datasphere.core.web.BaseController;
import cn.healthcaredaas.datasphere.svc.agent.entity.AgentMessage;
import cn.healthcaredaas.datasphere.svc.agent.entity.AgentSession;
import cn.healthcaredaas.datasphere.svc.agent.engine.AgentEngine;
import cn.healthcaredaas.datasphere.svc.agent.service.AgentMessageService;
import cn.healthcaredaas.datasphere.svc.agent.service.AgentSessionService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Agent会话控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/agent/sessions")
@RequiredArgsConstructor
@Tag(name = "Agent会话管理", description = "Agent会话管理相关接口")
public class AgentSessionController extends BaseController {

    private final AgentSessionService sessionService;
    private final AgentMessageService messageService;
    private final AgentEngine agentEngine;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Operation(summary = "分页查询会话列表")
    @GetMapping
    public IPage<AgentSession> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            AgentSession params) {
        return sessionService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取用户的会话列表")
    @GetMapping("/user/{userId}")
    public List<AgentSession> listByUser(@PathVariable("userId") String userId) {
        return sessionService.listByUserId(userId);
    }

    @Operation(summary = "获取会话详情")
    @GetMapping("/{id}")
    public AgentSession getById(@PathVariable("id") String id) {
        return sessionService.getById(id);
    }

    @Operation(summary = "创建会话")
    @PostMapping
    public AgentSession create(@RequestBody Map<String, String> request) {
        String title = request.getOrDefault("title", "新对话");
        String modelId = request.get("modelId");
        String userId = request.getOrDefault("userId", "default");
        String tenantId = request.getOrDefault("tenantId", "default");
        return sessionService.createSession(title, modelId, userId, tenantId);
    }

    @Operation(summary = "更新会话标题")
    @PutMapping("/{id}/title")
    public AgentSession updateTitle(@PathVariable("id") String id, @RequestBody Map<String, String> request) {
        AgentSession session = sessionService.getById(id);
        if (session != null) {
            session.setTitle(request.get("title"));
            sessionService.updateById(session);
        }
        return session;
    }

    @Operation(summary = "归档会话")
    @PutMapping("/{id}/archive")
    public void archive(@PathVariable("id") String id) {
        sessionService.archiveSession(id);
    }

    @Operation(summary = "删除会话")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        sessionService.removeById(id);
    }

    @Operation(summary = "获取会话消息列表")
    @GetMapping("/{sessionId}/messages")
    public List<AgentMessage> getMessages(@PathVariable("sessionId") String sessionId) {
        return messageService.listBySessionId(sessionId);
    }

    @Operation(summary = "分页获取会话消息")
    @GetMapping("/{sessionId}/messages/page")
    public IPage<AgentMessage> getMessagesPage(
            @PathVariable("sessionId") String sessionId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") long size) {
        AgentMessage params = new AgentMessage();
        params.setSessionId(sessionId);
        return messageService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "发送消息")
    @PostMapping("/{sessionId}/messages")
    public AgentMessage sendMessage(
            @PathVariable("sessionId") String sessionId,
            @RequestBody Map<String, String> request) {
        String content = request.get("content");
        String userId = request.getOrDefault("userId", "default");
        String tenantId = request.getOrDefault("tenantId", "default");

        return agentEngine.processMessage(sessionId, content, userId, tenantId);
    }

    @Operation(summary = "发送消息(SSE流式)")
    @PostMapping(value = "/{sessionId}/messages/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendMessageStream(
            @PathVariable("sessionId") String sessionId,
            @RequestBody Map<String, String> request) {
        String content = request.get("content");
        String userId = request.getOrDefault("userId", "default");
        String tenantId = request.getOrDefault("tenantId", "default");

        SseEmitter emitter = new SseEmitter(60000L);

        executorService.execute(() -> {
            try {
                emitter.send(SseEmitter.event().name("start").data(""));
                agentEngine.processMessageStream(sessionId, content, userId, tenantId, new cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapter.StreamCallback() {
                    @Override
                    public void onToken(String token) {
                        try {
                            emitter.send(SseEmitter.event().name("message").data(token));
                        } catch (IOException e) {
                            emitter.completeWithError(e);
                        }
                    }

                    @Override
                    public void onComplete(String fullResponse) {
                        try {
                            emitter.send(SseEmitter.event().name("done").data(""));
                            emitter.complete();
                        } catch (IOException e) {
                            emitter.completeWithError(e);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        try {
                            emitter.send(SseEmitter.event().name("error").data(error.getMessage()));
                        } catch (IOException e) {
                            // ignore
                        }
                        emitter.completeWithError(error);
                    }
                });
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}