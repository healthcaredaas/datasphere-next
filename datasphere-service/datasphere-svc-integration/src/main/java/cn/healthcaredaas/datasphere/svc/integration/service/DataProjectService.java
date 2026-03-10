package cn.healthcaredaas.datasphere.svc.integration.service;

import cn.healthcaredaas.datasphere.svc.integration.entity.DataProject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 数据项目服务接口
 *
 * @author chenpan
 */
public interface DataProjectService extends IService<DataProject> {

    /**
     * 分页查询数据项目
     *
     * @param page   分页参数
     * @param params 查询参数
     * @return 分页结果
     */
    IPage<DataProject> pageQuery(IPage<DataProject> page, DataProject params);
}
