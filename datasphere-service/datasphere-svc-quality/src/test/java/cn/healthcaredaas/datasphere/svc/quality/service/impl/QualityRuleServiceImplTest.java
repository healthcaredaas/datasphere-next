package cn.healthcaredaas.datasphere.svc.quality.service.impl;

import cn.healthcaredaas.datasphere.svc.quality.engine.QualityCheckEngine;
import cn.healthcaredaas.datasphere.svc.quality.entity.QualityRule;
import cn.healthcaredaas.datasphere.svc.quality.entity.QualityRuleTemplate;
import cn.healthcaredaas.datasphere.svc.quality.mapper.QualityRuleMapper;
import cn.healthcaredaas.datasphere.svc.quality.mapper.QualityRuleTemplateMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 质量规则服务测试
 *
 * @author chenpan
 */
@ExtendWith(MockitoExtension.class)
class QualityRuleServiceImplTest {

    @Mock
    private QualityRuleMapper ruleMapper;

    @Mock
    private QualityRuleTemplateMapper templateMapper;

    @Mock
    private QualityCheckEngine checkEngine;

    @InjectMocks
    private QualityRuleServiceImpl ruleService;

    private QualityRule testRule;
    private QualityRuleTemplate testTemplate;

    @BeforeEach
    void setUp() {
        testRule = new QualityRule();
        testRule.setId("1");
        testRule.setRuleCode("RULE_001");
        testRule.setRuleName("测试规则");
        testRule.setRuleType("COMPLETENESS");
        testRule.setDatasourceId("ds_001");
        testRule.setTableName("user_info");
        testRule.setColumnName("phone");
        testRule.setRuleExpression("SELECT * FROM user_info WHERE phone IS NULL");
        testRule.setStatus(1);

        testTemplate = new QualityRuleTemplate();
        testTemplate.setId("1001");
        testTemplate.setTemplateCode("COMPLETENESS_BASIC");
        testTemplate.setTemplateName("基础完整性检查");
        testTemplate.setRuleType("COMPLETENESS");
        testTemplate.setExpressionTemplate("SELECT * FROM ${table} WHERE ${column} IS NULL");
        testTemplate.setParamDefinition("[{"name": "table", "type": "string", "required": true}, {"name": "column", "type": "string", "required": true}]");
        testTemplate.setErrorMessageTemplate("字段${column}不能为空");
    }

    @Test
    @DisplayName("测试分页查询规则")
    void testPageQuery() {
        // given
        Page<QualityRule> page = new Page<>(1, 10);
        QualityRule params = new QualityRule();
        params.setRuleName("测试");

        // when
        when(ruleMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
            .thenReturn(page);
        IPage<QualityRule> result = ruleService.pageQuery(page, params);

        // then
        assertNotNull(result);
        verify(ruleMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("测试测试规则 - 规则有效")
    void testTestRuleSuccess() {
        // given
        when(ruleMapper.selectById("1")).thenReturn(testRule);
        when(checkEngine.testRule(testRule)).thenReturn(true);

        // when
        boolean result = ruleService.testRule("1");

        // then
        assertTrue(result);
        verify(ruleMapper).selectById("1");
        verify(checkEngine).testRule(testRule);
    }

    @Test
    @DisplayName("测试测试规则 - 规则不存在")
    void testTestRuleNotFound() {
        // given
        when(ruleMapper.selectById("999")).thenReturn(null);

        // when
        boolean result = ruleService.testRule("999");

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("测试从模板创建规则 - 成功")
    void testCreateRuleFromTemplateSuccess() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("table", "user_info");
        params.put("column", "phone");
        params.put("datasourceId", "ds_001");
        params.put("ruleName", "用户手机号完整性检查");

        when(templateMapper.selectById("1001")).thenReturn(testTemplate);
        when(ruleMapper.insert(any(QualityRule.class))).thenReturn(1);

        // when
        String ruleId = ruleService.createRuleFromTemplate("1001", params);

        // then
        assertNotNull(ruleId);
        verify(templateMapper).selectById("1001");
        verify(ruleMapper).insert(any(QualityRule.class));
    }

    @Test
    @DisplayName("测试从模板创建规则 - 模板不存在")
    void testCreateRuleFromTemplateNotFound() {
        // given
        Map<String, Object> params = new HashMap<>();
        when(templateMapper.selectById("999")).thenReturn(null);

        // when & then
        assertThrows(RuntimeException.class, () -> {
            ruleService.createRuleFromTemplate("999", params);
        });
    }

    @Test
    @DisplayName("测试从模板创建规则 - 缺少必填参数")
    void testCreateRuleFromTemplateMissingParam() {
        // given
        Map<String, Object> params = new HashMap<>();
        // 缺少table和column参数

        when(templateMapper.selectById("1001")).thenReturn(testTemplate);

        // when & then
        assertThrows(RuntimeException.class, () -> {
            ruleService.createRuleFromTemplate("1001", params);
        });
    }
}
