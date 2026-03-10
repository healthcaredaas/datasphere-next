package cn.healthcaredaas.datasphere.svc.integration.service;

import cn.healthcaredaas.datasphere.svc.integration.entity.DataPipeline;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 数据管道服务接口
 *
 * @author chenpan
 */
public interface DataPipelineService extends IService<DataPipeline> {

    /**
     * 分页查询数据管道
     *
     * @param page   分页参数
     * @param params 查询参数
     * @return 分页结果
     */
    IPage<DataPipeline> pageQuery(IPage<DataPipeline> page, DataPipeline params);

    /**
     * 根据项目ID查询管道列表
     *
     * @param projectId 项目ID
     * @return 管道列表
     */
    List<DataPipeline> listByProject(String projectId);
}
