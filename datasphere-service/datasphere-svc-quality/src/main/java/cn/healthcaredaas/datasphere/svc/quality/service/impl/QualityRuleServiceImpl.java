package cn.healthcaredaas.datasphere.svc.quality.service.impl;

import cn.healthcaredaas.datasphere.svc.quality.engine.QualityCheckEngine;
import cn.healthcaredaas.datasphere.svc.quality.entity.QualityRule;
import cn.healthcaredaas.datasphere.svc.quality.entity.QualityRuleTemplate;
import cn.healthcaredaas.datasphere.svc.quality.mapper.QualityRuleMapper;
import cn.healthcaredaas.datasphere.svc.quality.mapper.QualityRuleTemplateMapper;
import cn.healthcaredaas.datasphere.svc.quality.service.QualityRuleService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 质量规则服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QualityRuleServiceImpl extends ServiceImpl<QualityRuleMapper, QualityRule>
        implements QualityRuleService {

    private final QualityCheckEngine checkEngine;
    private final QualityRuleTemplateMapper templateMapper;

    @Override
    public IPage<QualityRule> pageQuery(IPage<QualityRule> page, QualityRule params) {
        LambdaQueryWrapper<QualityRule> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getRuleName())) {
            wrapper.like(QualityRule::getRuleName, params.getRuleName());
        }

        if (StringUtils.isNotBlank(params.getRuleType())) {
            wrapper.eq(QualityRule::getRuleType, params.getRuleType());
        }

        if (params.getStatus() != null) {
            wrapper.eq(QualityRule::getStatus, params.getStatus());
        }

        wrapper.orderByDesc(QualityRule::getCreateTime);

        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean testRule(String ruleId) {
        QualityRule rule = getById(ruleId);
        if (rule == null) {
            return false;
        }
        return checkEngine.testRule(rule);
    }

    @Override
    public String createRuleFromTemplate(String templateId, Map<String, Object> params) {
        QualityRuleTemplate template = templateMapper.selectById(templateId);
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }

        // 验证参数
        validateTemplateParams(template, params);

        // 替换表达式模板
        String expression = template.getExpressionTemplate();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            expression = expression.replace("${" + entry.getKey() + "}", entry.getValue().toString());
        }

        // 创建规则
        QualityRule rule = new QualityRule();
        rule.setRuleCode("RULE_" + System.currentTimeMillis());
        rule.setRuleName((String) params.getOrDefault("ruleName", template.getTemplateName()));
        rule.setRuleType(template.getRuleType());
        rule.setDatasourceId((String) params.get("datasourceId"));
        rule.setTableName((String) params.get("tableName"));
        rule.setColumnName((String) params.get("columnName"));
        rule.setRuleExpression(expression);
        rule.setErrorMessage(template.getErrorMessageTemplate());
        rule.setStatus(1);

        save(rule);
        return rule.getId();
    }

    /**
     * 验证模板参数
     */
    private void validateTemplateParams(QualityRuleTemplate template, Map<String, Object> params) {
        if (StringUtils.isBlank(template.getParamDefinition())) {
            return;
        }

        try {
            JSONArray paramDefs = JSON.parseArray(template.getParamDefinition());
            for (int i = 0; i < paramDefs.size(); i++) {
                JSONObject paramDef = paramDefs.getJSONObject(i);
                String paramName = paramDef.getString("name");
                Boolean required = paramDef.getBoolean("required");

                if (Boolean.TRUE.equals(required) && !params.containsKey(paramName)) {
                    throw new RuntimeException("缺少必填参数: " + paramName);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to validate template params: {}", e.getMessage());
        }
    }
}
