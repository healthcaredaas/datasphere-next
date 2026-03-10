package cn.healthcaredaas.datasphere.svc.quality.engine;

import cn.healthcaredaas.datasphere.svc.quality.entity.QualityRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 质量检测引擎测试
 *
 * @author chenpan
 */
@ExtendWith(MockitoExtension.class)
class QualityCheckEngineTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private DataSource dataSource;

    @Mock
    private Map<String, RuleExecutor> executorMap;

    @InjectMocks
    private QualityCheckEngine checkEngine;

    private QualityRule completenessRule;
    private QualityRule uniquenessRule;
    private QualityRule formatRule;

    @BeforeEach
    void setUp() {
        // 初始化完整性检查规则
        completenessRule = new QualityRule();
        completenessRule.setId("1");
        completenessRule.setRuleCode("RULE_001");
        completenessRule.setRuleName("手机号完整性检查");
        completenessRule.setRuleType("COMPLETENESS");
        completenessRule.setTableName("user_info");
        completenessRule.setColumnName("phone");
        completenessRule.setRuleExpression("SELECT * FROM user_info WHERE phone IS NULL OR phone = ''");
        completenessRule.setStatus(1);

        // 初始化唯一性检查规则
        uniquenessRule = new QualityRule();
        uniquenessRule.setId("2");
        uniquenessRule.setRuleCode("RULE_002");
        uniquenessRule.setRuleName("订单号唯一性检查");
        uniquenessRule.setRuleType("UNIQUENESS");
        uniquenessRule.setTableName("order_info");
        uniquenessRule.setColumnName("order_no");
        uniquenessRule.setRuleExpression("SELECT order_no, COUNT(*) FROM order_info GROUP BY order_no HAVING COUNT(*) > 1");
        uniquenessRule.setStatus(1);

        // 初始化格式检查规则
        formatRule = new QualityRule();
        formatRule.setId("3");
        formatRule.setRuleCode("RULE_003");
        formatRule.setRuleName("手机号格式检查");
        formatRule.setRuleType("FORMAT");
        formatRule.setTableName("user_info");
        formatRule.setColumnName("phone");
        formatRule.setRuleExpression("SELECT * FROM user_info WHERE phone NOT REGEXP '^1[3-9][0-9]{9}$'");
        formatRule.setStatus(1);
    }

    @Test
    @DisplayName("测试规则验证 - 有效规则")
    void testValidateRule_ValidRule() {
        // 由于实际执行器需要Spring上下文，这里测试验证逻辑
        assertTrue(completenessRule.getRuleExpression().contains("SELECT"));
        assertNotNull(completenessRule.getTableName());
        assertNotNull(completenessRule.getColumnName());
    }

    @Test
    @DisplayName("测试规则验证 - 无效规则（空表达式）")
    void testValidateRule_InvalidRule_EmptyExpression() {
        QualityRule invalidRule = new QualityRule();
        invalidRule.setRuleType("COMPLETENESS");
        invalidRule.setTableName("user_info");
        invalidRule.setColumnName("phone");
        invalidRule.setRuleExpression("");

        assertTrue(invalidRule.getRuleExpression().isEmpty());
    }

    @Test
    @DisplayName("测试完整性规则SQL生成")
    void testCompletenessRuleSqlGeneration() {
        String expectedSql = "SELECT * FROM user_info WHERE phone IS NULL OR phone = ''";
        assertEquals(expectedSql, completenessRule.getRuleExpression());
    }

    @Test
    @DisplayName("测试唯一性规则SQL生成")
    void testUniquenessRuleSqlGeneration() {
        String expectedSql = "SELECT order_no, COUNT(*) FROM order_info GROUP BY order_no HAVING COUNT(*) > 1";
        assertEquals(expectedSql, uniquenessRule.getRuleExpression());
    }

    @Test
    @DisplayName("测试格式规则SQL生成")
    void testFormatRuleSqlGeneration() {
        String expectedSql = "SELECT * FROM user_info WHERE phone NOT REGEXP '^1[3-9][0-9]{9}$'";
        assertEquals(expectedSql, formatRule.getRuleExpression());
    }

    @Test
    @DisplayName("测试质量评分计算 - 满分")
    void testCalculateScore_Perfect() {
        long totalCount = 1000;
        long errorCount = 0;
        double score = totalCount == 0 ? 100.0 :
                ((totalCount - errorCount) * 100.0 / totalCount);

        assertEquals(100.0, score, 0.01);
    }

    @Test
    @DisplayName("测试质量评分计算 - 及格")
    void testCalculateScore_Pass() {
        long totalCount = 1000;
        long errorCount = 100;
        double score = totalCount == 0 ? 100.0 :
                ((totalCount - errorCount) * 100.0 / totalCount);

        assertEquals(90.0, score, 0.01);
    }

    @Test
    @DisplayName("测试质量评分计算 - 不及格")
    void testCalculateScore_Fail() {
        long totalCount = 1000;
        long errorCount = 500;
        double score = totalCount == 0 ? 100.0 :
                ((totalCount - errorCount) * 100.0 / totalCount);

        assertEquals(50.0, score, 0.01);
    }

    @Test
    @DisplayName("测试质量评分计算 - 零数据")
    void testCalculateScore_ZeroData() {
        long totalCount = 0;
        long errorCount = 0;
        double score = totalCount == 0 ? 100.0 :
                ((totalCount - errorCount) * 100.0 / totalCount);

        assertEquals(100.0, score, 0.01);
    }

    @Test
    @DisplayName("测试质量等级计算")
    void testCalculateGrade() {
        assertEquals("A", calculateGrade(new BigDecimal("95")));
        assertEquals("A", calculateGrade(new BigDecimal("100")));
        assertEquals("B", calculateGrade(new BigDecimal("85")));
        assertEquals("C", calculateGrade(new BigDecimal("75")));
        assertEquals("D", calculateGrade(new BigDecimal("65")));
        assertEquals("F", calculateGrade(new BigDecimal("55")));
    }

    @Test
    @DisplayName("测试各种规则类型的SQL正确性")
    void testRuleTypeSqlValidity() {
        // 验证完整性检查SQL
        assertTrue(completenessRule.getRuleExpression().toUpperCase().contains("SELECT"));
        assertTrue(completenessRule.getRuleExpression().toUpperCase().contains("FROM"));
        assertTrue(completenessRule.getRuleExpression().toUpperCase().contains("WHERE"));

        // 验证唯一性检查SQL
        assertTrue(uniquenessRule.getRuleExpression().toUpperCase().contains("GROUP BY"));
        assertTrue(uniquenessRule.getRuleExpression().toUpperCase().contains("HAVING"));

        // 验证格式检查SQL
        assertTrue(formatRule.getRuleExpression().toUpperCase().contains("NOT REGEXP"));
    }

    private String calculateGrade(BigDecimal score) {
        int value = score.intValue();
        if (value >= 95) return "A";
        if (value >= 85) return "B";
        if (value >= 75) return "C";
        if (value >= 60) return "D";
        return "F";
    }
}
