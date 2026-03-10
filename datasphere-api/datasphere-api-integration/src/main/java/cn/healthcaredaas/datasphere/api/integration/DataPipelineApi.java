package cn.healthcaredaas.datasphere.api.integration;

import cn.healthcaredaas.datasphere.api.integration.dto.DataPipelineDTO;

import java.util.List;

/**
 * 数据管道 Dubbo 接口
 *
 * @author chenpan
 */
public interface DataPipelineApi {

    /**
     * 根据ID获取管道
     *
     * @param id 管道ID
     * @return 管道信息
     */
    DataPipelineDTO getById(String id);

    /**
     * 根据项目ID获取管道列表
     *
     * @param projectId 项目ID
     * @return 管道列表
     */
    List<DataPipelineDTO> listByProject(String projectId);

    /**
     * 启动管道
     *
     * @param pipelineId 管道ID
     * @return 是否成功
     */
    boolean startPipeline(String pipelineId);

    /**
     * 停止管道
     *
     * @param pipelineId 管道ID
     * @return 是否成功
     */
    boolean stopPipeline(String pipelineId);
}
