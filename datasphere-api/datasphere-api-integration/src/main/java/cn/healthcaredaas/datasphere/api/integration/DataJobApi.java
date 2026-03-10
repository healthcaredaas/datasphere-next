package cn.healthcaredaas.datasphere.api.integration;

import cn.healthcaredaas.datasphere.api.integration.dto.DataJobDTO;
import cn.healthcaredaas.datasphere.api.integration.dto.DataJobExecuteDTO;
import cn.healthcaredaas.datasphere.api.integration.dto.PipelineConnectorDTO;

import java.util.List;

/**
 * 数据作业 Feign 接口
 * <p>
 * 供Job Runner服务调用，获取作业配置和更新执行状态
 *
 * @author chenpan
 */
public interface DataJobApi {

    /**
     * 根据ID获取作业
     *
     * @param jobId 作业ID
     * @return 作业信息
     */
    DataJobDTO getJobById(String jobId);

    /**
     * 获取作业配置内容
     *
     * @param jobId 作业ID
     * @return 配置内容(HOCON格式)
     */
    String getJobConfig(String jobId);

    /**
     * 获取管道的连接器列表
     *
     * @param pipelineId 管道ID
     * @return 连接器列表
     */
    List<PipelineConnectorDTO> getPipelineConnectors(String pipelineId);

    /**
     * 创建执行记录
     *
     * @param jobId       作业ID
     * @param triggerType 触发类型 0-手动 1-定时
     * @return 执行记录DTO
     */
    DataJobExecuteDTO createExecuteRecord(String jobId, Integer triggerType);

    /**
     * 更新执行记录为成功
     *
     * @param executeId 执行记录ID
     * @param readRows  读取行数
     * @param writeRows 写入行数
     */
    void updateExecuteSuccess(String executeId, Long readRows, Long writeRows);

    /**
     * 更新执行记录为失败
     *
     * @param executeId 执行记录ID
     * @param errorMsg  错误信息
     */
    void updateExecuteFailed(String executeId, String errorMsg);

    /**
     * 更新执行进度
     *
     * @param executeId 执行记录ID
     * @param readRows  读取行数
     * @param writeRows 写入行数
     */
    void updateExecuteProgress(String executeId, Long readRows, Long writeRows);

    /**
     * 更新作业状态
     *
     * @param jobId  作业ID
     * @param status 状态
     */
    void updateJobStatus(String jobId, Integer status);
}
