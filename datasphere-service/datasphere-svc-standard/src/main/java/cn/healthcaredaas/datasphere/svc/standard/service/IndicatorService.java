package cn.healthcaredaas.datasphere.svc.standard.service;

import cn.healthcaredaas.datasphere.svc.standard.entity.Indicator;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 指标服务接口
 *
 * @author chenpan
 */
public interface IndicatorService extends IService<Indicator> {

    IPage<Indicator> pageQuery(IPage<Indicator> page, Indicator params);
}
