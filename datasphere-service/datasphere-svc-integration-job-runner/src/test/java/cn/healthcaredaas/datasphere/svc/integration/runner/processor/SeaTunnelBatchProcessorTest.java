package cn.healthcaredaas.datasphere.svc.integration.runner.processor;

import cn.healthcaredaas.datasphere.svc.integration.runner.service.IntegrationJobManager;
import cn.healthcaredaas.datasphere.svc.integration.runner.service.SeaTunnelClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SeaTunnelBatchProcessor单元测试
 *
 * @author chenpan
 */
@ExtendWith(MockitoExtension.class)
class SeaTunnelBatchProcessorTest {

    @Mock
    private SeaTunnelClientService seaTunnelClientService;

    @Mock
    private IntegrationJobManager jobManager;

    @Mock
    private TaskContext taskContext;

    @InjectMocks
    private SeaTunnelBatchProcessor processor;

    @BeforeEach
    void setUp() {
        // 初始化
    }

    @Test
    @DisplayName("测试处理作业成功")
    void testProcessSuccess() {
        // 准备
        String jobParams = "{\"jobId\":\"job-001\",\"executeId\":\"exec-001\"}";
        when(taskContext.getJobParams()).thenReturn(jobParams);
        when(taskContext.getInstanceId()).thenReturn(1001L);
        when(seaTunnelClientService.executeJob("job-001", "exec-001")).thenReturn(true);

        // 执行
        ProcessResult result = processor.process(taskContext);

        // 验证
        assertTrue(result.isSuccess());
        assertEquals("Job executed successfully", result.getMsg());
        verify(seaTunnelClientService).executeJob("job-001", "exec-001");
    }

    @Test
    @DisplayName("测试处理作业失败")
    void testProcessFailure() {
        // 准备
        String jobParams = "{\"jobId\":\"job-001\",\"executeId\":\"exec-001\"}";
        when(taskContext.getJobParams()).thenReturn(jobParams);
        when(taskContext.getInstanceId()).thenReturn(1001L);
        when(seaTunnelClientService.executeJob("job-001", "exec-001")).thenReturn(false);

        // 执行
        ProcessResult result = processor.process(taskContext);

        // 验证
        assertFalse(result.isSuccess());
        assertEquals("Job execution failed", result.getMsg());
    }

    @Test
    @DisplayName("测试空参数")
    void testProcessEmptyParams() {
        // 准备
        when(taskContext.getJobParams()).thenReturn(null);
        when(taskContext.getInstanceId()).thenReturn(1001L);

        // 执行
        ProcessResult result = processor.process(taskContext);

        // 验证
        assertFalse(result.isSuccess());
        assertEquals("Job params is empty", result.getMsg());
        verifyNoInteractions(seaTunnelClientService);
    }

    @Test
    @DisplayName("测试空白参数")
    void testProcessBlankParams() {
        // 准备
        when(taskContext.getJobParams()).thenReturn("   ");
        when(taskContext.getInstanceId()).thenReturn(1001L);

        // 执行
        ProcessResult result = processor.process(taskContext);

        // 验证
        assertFalse(result.isSuccess());
        assertEquals("Job params is empty", result.getMsg());
        verifyNoInteractions(seaTunnelClientService);
    }

    @Test
    @DisplayName("测试无效JSON参数")
    void testProcessInvalidJson() {
        // 准备
        when(taskContext.getJobParams()).thenReturn("invalid json");
        when(taskContext.getInstanceId()).thenReturn(1001L);

        // 执行
        ProcessResult result = processor.process(taskContext);

        // 验证
        assertFalse(result.isSuccess());
        assertTrue(result.getMsg().startsWith("Invalid job params"));
        verifyNoInteractions(seaTunnelClientService);
    }

    @Test
    @DisplayName("测试缺少jobId")
    void testProcessMissingJobId() {
        // 准备
        String jobParams = "{\"executeId\":\"exec-001\"}";
        when(taskContext.getJobParams()).thenReturn(jobParams);
        when(taskContext.getInstanceId()).thenReturn(1001L);

        // 执行
        ProcessResult result = processor.process(taskContext);

        // 验证
        assertFalse(result.isSuccess());
        assertEquals("Missing required param: jobId", result.getMsg());
        verifyNoInteractions(seaTunnelClientService);
    }

    @Test
    @DisplayName("测试无executeId执行")
    void testProcessWithoutExecuteId() {
        // 准备 - 只有jobId，没有executeId
        String jobParams = "{\"jobId\":\"job-001\"}";
        when(taskContext.getJobParams()).thenReturn(jobParams);
        when(taskContext.getInstanceId()).thenReturn(1001L);
        when(seaTunnelClientService.executeJobWithoutCallback("job-001")).thenReturn(true);

        // 执行
        ProcessResult result = processor.process(taskContext);

        // 验证
        assertTrue(result.isSuccess());
        verify(seaTunnelClientService).executeJobWithoutCallback("job-001");
        verify(seaTunnelClientService, never()).executeJob(anyString(), anyString());
    }

    @Test
    @DisplayName("测试执行抛出异常")
    void testProcessException() {
        // 准备
        String jobParams = "{\"jobId\":\"job-001\",\"executeId\":\"exec-001\"}";
        when(taskContext.getJobParams()).thenReturn(jobParams);
        when(taskContext.getInstanceId()).thenReturn(1001L);
        when(seaTunnelClientService.executeJob(anyString(), anyString()))
                .thenThrow(new RuntimeException("SeaTunnel connection failed"));

        // 执行
        ProcessResult result = processor.process(taskContext);

        // 验证
        assertFalse(result.isSuccess());
        assertTrue(result.getMsg().contains("SeaTunnel connection failed"));
    }
}
