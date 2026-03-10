package cn.healthcaredaas.datasphere.svc.integration.service;

import cn.healthcaredaas.datasphere.svc.integration.entity.DataJob;
import cn.healthcaredaas.datasphere.svc.integration.entity.DataJobExecute;

/**
 * 作业执行回调服务
 *
 * @author chenpan
 */
public interface JobExecuteCallbackService {

    /**
     * 作业执行成功回调
     *
     * @param executeId 执行记录ID
     * @param readRows  读取行数
     * @param writeRows 写入行数
     */
    void onJobSuccess(String executeId, Long readRows, Long writeRows);

    /**
     * 作业执行失败回调
     *
     * @param executeId 执行记录ID
     * @param errorMsg  错误信息
     */
    void onJobFailed(String executeId, String errorMsg);

    /**
     * 更新作业执行进度
     *
     * @param executeId 执行记录ID
     * @param readRows  读取行数
     * @param writeRows 写入行数
     */
    void updateProgress(String executeId, Long readRows, Long writeRows);

    /**
     * 获取作业配置内容
     *
     * @param jobId 作业ID
     * @return 配置内容
     */
    String getJobConfig(String jobId);

    /**
     * 更新作业状态
     *
     * @param jobId  作业ID
     * @param status 状态
     */
    void updateJobStatus(String jobId, Integer status);
}
