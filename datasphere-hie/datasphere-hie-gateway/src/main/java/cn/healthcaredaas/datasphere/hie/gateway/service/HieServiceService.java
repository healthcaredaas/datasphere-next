package cn.healthcaredaas.datasphere.hie.gateway.service;

import cn.healthcaredaas.datasphere.hie.gateway.entity.HieService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 交互服务接口
 *
 * @author chenpan
 */
public interface HieServiceService extends IService<HieService> {

    IPage<HieService> pageQuery(IPage<HieService> page, HieService params);
}
