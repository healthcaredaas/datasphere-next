package cn.healthcaredaas.datasphere.svc.standard.service;

import cn.healthcaredaas.datasphere.svc.standard.entity.Dataset;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 数据集服务接口
 *
 * @author chenpan
 */
public interface DatasetService extends IService<Dataset> {

    IPage<Dataset> pageQuery(IPage<Dataset> page, Dataset params);
}
