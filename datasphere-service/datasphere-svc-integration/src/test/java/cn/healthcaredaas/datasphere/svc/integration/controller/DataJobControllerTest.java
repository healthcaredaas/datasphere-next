package cn.healthcaredaas.datasphere.svc.integration.controller;

import cn.healthcaredaas.datasphere.svc.integration.entity.DataJob;
import cn.healthcaredaas.datasphere.svc.integration.entity.DataJobExecute;
import cn.healthcaredaas.datasphere.svc.integration.service.DataJobService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
 * DataJobController单元测试
 *
 * @author chenpan
 */
@ExtendWith(MockitoExtension.class)
class DataJobControllerTest {

    @Mock
    private DataJobService dataJobService;

    @InjectMocks
    private DataJobController dataJobController;

    private DataJob testJob;
    private DataJobExecute testExecute;

    @BeforeEach
    void setUp() {
        testJob = new DataJob();
        testJob.setId("job-001");
        testJob.setJobCode("TEST_JOB");
        testJob.setJobName("测试作业");
        testJob.setPipelineId("pipeline-001");

        testExecute = new DataJobExecute();
        testExecute.setId("execute-001");
        testExecute.setJobId("job-001");
    }

    @Test
    @DisplayName("测试分页查询")
    void testPage() {
        // 准备
        Page<DataJob> page = new Page<>(1, 10);
        IPage<DataJob> resultPage = new Page<>();
        when(dataJobService.pageQuery(any(Page.class), any(DataJob.class))).thenReturn(resultPage);

        // 执行
        IPage<DataJob> result = dataJobController.page(1, 10, new DataJob());

        // 验证
        assertNotNull(result);
        verify(dataJobService).pageQuery(any(Page.class), any(DataJob.class));
    }

    @Test
    @DisplayName("测试根据ID获取作业")
    void testGetById() {
        when(dataJobService.getById("job-001")).thenReturn(testJob);

        DataJob result = dataJobController.getById("job-001");

        assertNotNull(result);
        assertEquals("job-001", result.getId());
        verify(dataJobService).getById("job-001");
    }

    @Test
    @DisplayName("测试新增作业")
    void testSave() {
        doNothing().when(dataJobService).save(any(DataJob.class));

        DataJob result = dataJobController.save(testJob);

        assertNotNull(result);
        verify(dataJobService).save(testJob);
    }

    @Test
    @DisplayName("测试更新作业")
    void testUpdate() {
        when(dataJobService.getById("job-001")).thenReturn(testJob);
        when(dataJobService.updateById(any(DataJob.class))).thenReturn(true);

        DataJob result = dataJobController.update("job-001", testJob);

        assertNotNull(result);
        verify(dataJobService).updateById(argThat(job ->
                job.getId().equals("job-001")));
    }

    @Test
    @DisplayName("测试删除作业")
    void testDelete() {
        when(dataJobService.removeById("job-001")).thenReturn(true);

        assertDoesNotThrow(() -> dataJobController.delete("job-001"));

        verify(dataJobService).removeById("job-001");
    }

    @Test
    @DisplayName("测试发布作业")
    void testPublish() {
        when(dataJobService.publishJob("job-001")).thenReturn(true);

        boolean result = dataJobController.publish("job-001");

        assertTrue(result);
        verify(dataJobService).publishJob("job-001");
    }

    @Test
    @DisplayName("测试启动作业")
    void testStart() {
        when(dataJobService.startJob("job-001")).thenReturn(testExecute);

        DataJobExecute result = dataJobController.start("job-001");

        assertNotNull(result);
        assertEquals("execute-001", result.getId());
        verify(dataJobService).startJob("job-001");
    }

    @Test
    @DisplayName("测试停止作业")
    void testStop() {
        when(dataJobService.stopJob("job-001")).thenReturn(true);

        boolean result = dataJobController.stop("job-001");

        assertTrue(result);
        verify(dataJobService).stopJob("job-001");
    }

    @Test
    @DisplayName("测试获取作业配置")
    void testGetConfig() {
        String config = "{\"env\":{}}";
        when(dataJobService.getJobConfig("job-001")).thenReturn(config);

        String result = dataJobController.getConfig("job-001");

        assertEquals(config, result);
        verify(dataJobService).getJobConfig("job-001");
    }

    @Test
    @DisplayName("测试创建执行记录")
    void testCreateExecuteRecord() {
        when(dataJobService.startJob("job-001")).thenReturn(testExecute);

        DataJobExecute result = dataJobController.createExecuteRecord("job-001");

        assertNotNull(result);
        verify(dataJobService).startJob("job-001");
    }

    @Test
    @DisplayName("测试更新作业状态")
    void testUpdateJobStatus() {
        when(dataJobService.updateStatus("job-001", 2)).thenReturn(true);

        Map<String, Integer> params = new HashMap<>();
        params.put("status", 2);
        Map<String, Object> result = dataJobController.updateJobStatus("job-001", params);

        assertTrue((Boolean) result.get("success"));
        verify(dataJobService).updateStatus("job-001", 2);
    }
}
