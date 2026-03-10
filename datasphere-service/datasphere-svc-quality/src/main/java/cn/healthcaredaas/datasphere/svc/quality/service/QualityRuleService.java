package cn.healthcaredaas.datasphere.svc.quality.service;

import cn.healthcaredaas.datasphere.svc.quality.entity.QualityRule;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 质量规则服务接口
 *
 * @author chenpan
 */
public interface QualityRuleService extends IService<QualityRule> {

    IPage<QualityRule> pageQuery(IPage<QualityRule> page, QualityRule params);

    /**
     * 测试规则SQL是否有效
     *
     * @param ruleId 规则ID
     * @return 是否有效
     */
    boolean testRule(String ruleId);

    /**
     * 根据模板创建规则
     *
     * @param templateId 模板ID
     * @param params 参数
     * @return 规则ID
     */
    String createRuleFromTemplate(String templateId, java.util.Map<String, Object> params);
}
