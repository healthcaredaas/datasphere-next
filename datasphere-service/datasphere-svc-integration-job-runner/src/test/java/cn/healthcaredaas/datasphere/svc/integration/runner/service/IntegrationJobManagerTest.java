package cn.healthcaredaas.datasphere.svc.integration.runner.service;

import cn.healthcaredaas.datasphere.api.integration.dto.DataJobExecuteDTO;
import cn.healthcaredaas.datasphere.api.integration.feign.IntegrationJobClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * IntegrationJobManager单元测试
 *
 * @author chenpan
 */
@ExtendWith(MockitoExtension.class)
class IntegrationJobManagerTest {

    @Mock
    private IntegrationJobClient jobClient;

    @InjectMocks
    private IntegrationJobManager jobManager;

    @BeforeEach
    void setUp() {
        // 初始化
    }

    @Test
    @DisplayName("测试获取作业配置成功")
    void testGetJobConfigSuccess() {
        String expectedConfig = "{\"env\":{\"job.name\":\"test\"}}";
        when(jobClient.getJobConfig("job-001")).thenReturn(expectedConfig);

        String config = jobManager.getJobConfig("job-001");

        assertEquals(expectedConfig, config);
        verify(jobClient).getJobConfig("job-001");
    }

    @Test
    @DisplayName("测试获取作业配置失败")
    void testGetJobConfigFailure() {
        when(jobClient.getJobConfig("job-001"))
                .thenThrow(new RuntimeException("Connection refused"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jobManager.getJobConfig("job-001");
        });

        assertEquals("获取作业配置失败", exception.getMessage());
    }

    @Test
    @DisplayName("测试创建执行记录成功")
    void testCreateExecuteRecordSuccess() {
        DataJobExecuteDTO executeDTO = new DataJobExecuteDTO();
        executeDTO.setId("execute-001");
        executeDTO.setJobId("job-001");

        when(jobClient.createExecuteRecord("job-001")).thenReturn(executeDTO);

        String executeId = jobManager.createExecuteRecord("job-001");

        assertEquals("execute-001", executeId);
        verify(jobClient).createExecuteRecord("job-001");
    }

    @Test
    @DisplayName("测试创建执行记录返回空")
    void testCreateExecuteRecordNull() {
        when(jobClient.createExecuteRecord("job-001")).thenReturn(null);

        String executeId = jobManager.createExecuteRecord("job-001");

        assertNull(executeId);
    }

    @Test
    @DisplayName("测试更新执行成功状态")
    void testUpdateExecuteSuccess() {
        Map<String, Object> expectedParams = new HashMap<>();
        expectedParams.put("readRows", 1000L);
        expectedParams.put("writeRows", 1000L);

        when(jobClient.onJobSuccess(eq("execute-001"), anyMap()))
                .thenReturn(Map.of("success", true));

        assertDoesNotThrow(() -> {
            jobManager.updateExecuteSuccess("execute-001", 1000L, 1000L);
        });

        verify(jobClient).onJobSuccess(eq("execute-001"), argThat(params ->
                params.get("readRows").equals(1000L) &&
                        params.get("writeRows").equals(1000L)));
    }

    @Test
    @DisplayName("测试更新执行成功状态-异常不抛出")
    void testUpdateExecuteSuccessException() {
        when(jobClient.onJobSuccess(anyString(), anyMap()))
                .thenThrow(new RuntimeException("Connection refused"));

        // 不应该抛出异常
        assertDoesNotThrow(() -> {
            jobManager.updateExecuteSuccess("execute-001", 1000L, 1000L);
        });
    }

    @Test
    @DisplayName("测试更新执行失败状态")
    void testUpdateExecuteFailed() {
        String errorMsg = "Connection timeout";

        when(jobClient.onJobFailed(eq("execute-001"), anyMap()))
                .thenReturn(Map.of("success", true));

        assertDoesNotThrow(() -> {
            jobManager.updateExecuteFailed("execute-001", errorMsg);
        });

        verify(jobClient).onJobFailed(eq("execute-001"), argThat(params ->
                params.get("errorMsg").equals(errorMsg)));
    }

    @Test
    @DisplayName("测试更新作业状态")
    void testUpdateJobStatus() {
        when(jobClient.updateJobStatus(eq("job-001"), anyMap()))
                .thenReturn(Map.of("success", true));

        assertDoesNotThrow(() -> {
            jobManager.updateJobStatus("job-001", 3);
        });

        verify(jobClient).updateJobStatus(eq("job-001"), argThat(params ->
                params.get("status").equals(3)));
    }

    @Test
    @DisplayName("测试更新作业状态-异常不抛出")
    void testUpdateJobStatusException() {
        when(jobClient.updateJobStatus(anyString(), anyMap()))
                .thenThrow(new RuntimeException("Connection refused"));

        // 不应该抛出异常
        assertDoesNotThrow(() -> {
            jobManager.updateJobStatus("job-001", 3);
        });
    }
}
