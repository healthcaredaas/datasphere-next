package cn.healthcaredaas.datasphere.svc.integration.service;

import cn.healthcaredaas.datasphere.svc.integration.entity.DataJobExecute;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 数据作业执行记录服务接口
 *
 * @author chenpan
 */
public interface DataJobExecuteService extends IService<DataJobExecute> {

    IPage<DataJobExecute> pageQuery(IPage<DataJobExecute> page, DataJobExecute params);

    List<DataJobExecute> listByJob(String jobId);

    DataJobExecute getLatestExecute(String jobId);
}
