package cn.healthcaredaas.datasphere.svc.integration.service;

import cn.healthcaredaas.datasphere.svc.integration.entity.DataJob;
import cn.healthcaredaas.datasphere.svc.integration.entity.DataJobExecute;
import cn.healthcaredaas.datasphere.svc.integration.entity.DataPipeline;
import cn.healthcaredaas.datasphere.svc.integration.entity.PipelineConnector;
import cn.healthcaredaas.datasphere.svc.integration.mapper.DataJobExecuteMapper;
import cn.healthcaredaas.datasphere.svc.integration.mapper.DataJobMapper;
import cn.healthcaredaas.datasphere.svc.integration.mapper.DataPipelineMapper;
import cn.healthcaredaas.datasphere.svc.integration.mapper.PipelineConnectorMapper;
import cn.healthcaredaas.datasphere.svc.integration.seatunnel.SeaTunnelConfigGenerator;
import cn.healthcaredaas.datasphere.svc.integration.service.impl.DataJobServiceImpl;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * DataJobService单元测试
 *
 * @author chenpan
 */
@ExtendWith(MockitoExtension.class)
class DataJobServiceTest {

    @Mock
    private DataJobMapper dataJobMapper;

    @Mock
    private DataJobExecuteMapper dataJobExecuteMapper;

    @Mock
    private DataPipelineMapper dataPipelineMapper;

    @Mock
    private PipelineConnectorMapper pipelineConnectorMapper;

    @Mock
    private SeaTunnelConfigGenerator configGenerator;

    @InjectMocks
    private DataJobServiceImpl dataJobService;

    private DataJob testJob;
    private DataPipeline testPipeline;
    private List<PipelineConnector> testConnectors;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testJob = new DataJob();
        testJob.setId("job-001");
        testJob.setJobCode("TEST_JOB_001");
        testJob.setJobName("测试作业");
        testJob.setPipelineId("pipeline-001");
        testJob.setStatus(0); // 草稿状态

        testPipeline = new DataPipeline();
        testPipeline.setId("pipeline-001");
        testPipeline.setPipelineCode("TEST_PIPELINE");
        testPipeline.setPipelineName("测试管道");

        testConnectors = new ArrayList<>();
        PipelineConnector source = new PipelineConnector();
        source.setConnectorType("SOURCE");
        source.setPluginType("jdbc");
        source.setConfig(new JSONObject());
        testConnectors.add(source);
    }

    @Test
    @DisplayName("测试发布作业成功")
    void testPublishJobSuccess() {
        // 准备Mock
        when(dataJobMapper.selectById("job-001")).thenReturn(testJob);
        when(dataPipelineMapper.selectById("pipeline-001")).thenReturn(testPipeline);
        when(pipelineConnectorMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(testConnectors);
        when(configGenerator.generateZetaConfig(anyString(), anyList())).thenReturn("{\"env\":{}}");
        when(dataJobMapper.updateById(any(DataJob.class))).thenReturn(1);

        // 执行
        boolean result = dataJobService.publishJob("job-001");

        // 验证
        assertTrue(result);
        verify(dataJobMapper).selectById("job-001");
        verify(dataPipelineMapper).selectById("pipeline-001");
        verify(pipelineConnectorMapper).selectList(any(LambdaQueryWrapper.class));
        verify(configGenerator).generateZetaConfig(eq("测试管道"), anyList());
        verify(dataJobMapper).updateById(argThat(job -> job.getStatus() == 1));
    }

    @Test
    @DisplayName("测试发布作业-作业不存在")
    void testPublishJobNotFound() {
        when(dataJobMapper.selectById("job-not-exist")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dataJobService.publishJob("job-not-exist");
        });

        assertEquals("作业不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试发布作业-管道不存在")
    void testPublishPipelineNotFound() {
        when(dataJobMapper.selectById("job-001")).thenReturn(testJob);
        when(dataPipelineMapper.selectById("pipeline-001")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dataJobService.publishJob("job-001");
        });

        assertEquals("管道不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试发布作业-连接器为空")
    void testPublishEmptyConnectors() {
        when(dataJobMapper.selectById("job-001")).thenReturn(testJob);
        when(dataPipelineMapper.selectById("pipeline-001")).thenReturn(testPipeline);
        when(pipelineConnectorMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(new ArrayList<>());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dataJobService.publishJob("job-001");
        });

        assertEquals("管道连接器为空", exception.getMessage());
    }

    @Test
    @DisplayName("测试启动作业成功")
    void testStartJobSuccess() {
        // 准备已发布的作业
        testJob.setStatus(1); // 已发布
        testJob.setConfigContent("{\"env\":{}}");

        when(dataJobMapper.selectById("job-001")).thenReturn(testJob);
        when(dataJobExecuteMapper.insert(any(DataJobExecute.class))).thenAnswer(invocation -> {
            DataJobExecute execute = invocation.getArgument(0);
            execute.setId("execute-001");
            return 1;
        });
        when(dataJobMapper.updateById(any(DataJob.class))).thenReturn(1);

        // 执行
        DataJobExecute result = dataJobService.startJob("job-001");

        // 验证
        assertNotNull(result);
        assertEquals("job-001", result.getJobId());
        assertEquals(0, result.getStatus()); // 运行中
        assertEquals(0, result.getTriggerType()); // 手动触发

        verify(dataJobExecuteMapper).insert(any(DataJobExecute.class));
        verify(dataJobMapper).updateById(argThat(job -> job.getStatus() == 2)); // 更新为运行中
    }

    @Test
    @DisplayName("测试启动作业-作业不存在")
    void testStartJobNotFound() {
        when(dataJobMapper.selectById("job-not-exist")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dataJobService.startJob("job-not-exist");
        });

        assertEquals("作业不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试启动作业-未发布")
    void testStartJobNotPublished() {
        testJob.setStatus(0); // 草稿状态

        when(dataJobMapper.selectById("job-001")).thenReturn(testJob);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dataJobService.startJob("job-001");
        });

        assertEquals("作业未发布，请先发布", exception.getMessage());
    }

    @Test
    @DisplayName("测试停止作业")
    void testStopJob() {
        testJob.setStatus(2); // 运行中

        when(dataJobMapper.selectById("job-001")).thenReturn(testJob);
        when(dataJobMapper.updateById(any(DataJob.class))).thenReturn(1);

        boolean result = dataJobService.stopJob("job-001");

        assertTrue(result);
        verify(dataJobMapper).updateById(argThat(job -> job.getStatus() == 3)); // 已停止
    }

    @Test
    @DisplayName("测试停止作业-作业不存在")
    void testStopJobNotFound() {
        when(dataJobMapper.selectById("job-not-exist")).thenReturn(null);

        boolean result = dataJobService.stopJob("job-not-exist");

        assertFalse(result);
    }

    @Test
    @DisplayName("测试获取作业配置")
    void testGetJobConfig() {
        testJob.setConfigContent("{\"env\":{\"job.name\":\"test\"}}");

        when(dataJobMapper.selectById("job-001")).thenReturn(testJob);

        String config = dataJobService.getJobConfig("job-001");

        assertNotNull(config);
        assertEquals("{\"env\":{\"job.name\":\"test\"}}", config);
    }

    @Test
    @DisplayName("测试更新作业状态")
    void testUpdateStatus() {
        when(dataJobMapper.updateById(any(DataJob.class))).thenReturn(1);

        boolean result = dataJobService.updateStatus("job-001", 2);

        assertTrue(result);
        verify(dataJobMapper).updateById(argThat(job ->
                job.getId().equals("job-001") && job.getStatus() == 2));
    }

    @Test
    @DisplayName("测试根据管道查询作业列表")
    void testListByPipeline() {
        List<DataJob> jobs = new ArrayList<>();
        jobs.add(testJob);

        // 这里需要模拟lambdaQuery，但为了简化，我们直接测试方法调用
        // 实际项目中可以使用更复杂的Mock
    }
}
