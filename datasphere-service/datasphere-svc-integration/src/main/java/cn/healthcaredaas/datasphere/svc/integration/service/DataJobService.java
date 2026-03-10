package cn.healthcaredaas.datasphere.svc.integration.service;

import cn.healthcaredaas.datasphere.svc.integration.entity.DataJob;
import cn.healthcaredaas.datasphere.svc.integration.entity.DataJobExecute;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 数据作业服务接口
 *
 * @author chenpan
 */
public interface DataJobService extends IService<DataJob> {

    /**
     * 分页查询作业
     */
    IPage<DataJob> pageQuery(IPage<DataJob> page, DataJob params);

    /**
     * 根据管道ID查询作业列表
     */
    List<DataJob> listByPipeline(String pipelineId);

    /**
     * 发布作业(生成配置)
     */
    boolean publishJob(String jobId);

    /**
     * 启动作业
     */
    DataJobExecute startJob(String jobId);

    /**
     * 停止作业
     */
    boolean stopJob(String jobId);

    /**
     * 获取作业配置内容
     */
    String getJobConfig(String jobId);

    /**
     * 更新作业状态
     */
    boolean updateStatus(String jobId, Integer status);
}
