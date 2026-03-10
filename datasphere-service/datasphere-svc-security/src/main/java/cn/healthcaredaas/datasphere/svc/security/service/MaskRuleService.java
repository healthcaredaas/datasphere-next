package cn.healthcaredaas.datasphere.svc.security.service;

import cn.healthcaredaas.datasphere.svc.security.entity.MaskRule;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 脱敏规则服务接口
 *
 * @author chenpan
 */
public interface MaskRuleService extends IService<MaskRule> {

    IPage<MaskRule> pageQuery(IPage<MaskRule> page, MaskRule params);
}
