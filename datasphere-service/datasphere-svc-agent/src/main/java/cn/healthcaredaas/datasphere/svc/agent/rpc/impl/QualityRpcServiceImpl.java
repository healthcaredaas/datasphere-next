package cn.healthcaredaas.datasphere.svc.agent.rpc.impl;

import cn.healthcaredaas.datasphere.svc.agent.rpc.QualityRpcService;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据质量服务本地实现
 *
 * @author chenpan
 */
@Slf4j
@Component
public class QualityRpcServiceImpl implements QualityRpcService {

    @Override
    public JSONObject getRule(String ruleId) {
        log.info("RPC调用: getRule, id={}", ruleId);

        JSONObject rule = new JSONObject();
        rule.put("id", ruleId);
        rule.put("ruleName", "身份证号格式检查");
        rule.put("ruleType", "FORMAT");
        rule.put("status", 1);
        return rule;
    }

    @Override
    public String createRule(JSONObject rule) {
        log.info("RPC调用: createRule, rule={}", rule);
        return "rule_" + System.currentTimeMillis();
    }

    @Override
    public void updateRule(String ruleId, JSONObject rule) {
        log.info("RPC调用: updateRule, id={}", ruleId);
    }

    @Override
    public void deleteRule(String ruleId) {
        log.info("RPC调用: deleteRule, id={}", ruleId);
    }

    @Override
    public List<JSONObject> listRuleTemplates() {
        log.info("RPC调用: listRuleTemplates");

        return List.of(
                createTemplate("tpl_001", "完整性检查", "COMPLETENESS", "检查字段是否为空"),
                createTemplate("tpl_002", "唯一性检查", "UNIQUENESS", "检查字段值是否唯一"),
                createTemplate("tpl_003", "格式检查", "FORMAT", "检查字段格式是否正确"),
                createTemplate("tpl_004", "值域检查", "VALUE_RANGE", "检查字段值是否在指定范围内"),
                createTemplate("tpl_005", "身份证号检查", "FORMAT", "检查身份证号格式"),
                createTemplate("tpl_006", "手机号检查", "FORMAT", "检查手机号格式")
        );
    }

    @Override
    public String createRuleFromTemplate(String templateId, JSONObject params) {
        log.info("RPC调用: createRuleFromTemplate, templateId={}", templateId);
        return "rule_" + System.currentTimeMillis();
    }

    @Override
    public String executeQualityCheck(String ruleId) {
        log.info("RPC调用: executeQualityCheck, ruleId={}", ruleId);
        return "task_" + System.currentTimeMillis();
    }

    @Override
    public JSONObject getCheckResult(String taskId) {
        log.info("RPC调用: getCheckResult, taskId={}", taskId);

        JSONObject result = new JSONObject();
        result.put("taskId", taskId);
        result.put("status", "COMPLETED");
        result.put("totalRecords", 1000);
        result.put("errorRecords", 15);
        result.put("qualityScore", 98.5);
        return result;
    }

    @Override
    public JSONObject getQualityReport(String datasourceId, String tableName) {
        log.info("RPC调用: getQualityReport, datasourceId={}, tableName={}", datasourceId, tableName);

        JSONObject report = new JSONObject();
        report.put("datasourceId", datasourceId);
        report.put("tableName", tableName);
        report.put("qualityScore", 95.5);
        report.put("dimensionScores", JSONObject.of(
                "completeness", 98.0,
                "accuracy", 94.0,
                "consistency", 96.0,
                "timeliness", 95.0
        ));
        return report;
    }

    private JSONObject createTemplate(String id, String name, String type, String desc) {
        JSONObject template = new JSONObject();
        template.put("id", id);
        template.put("templateName", name);
        template.put("ruleType", type);
        template.put("description", desc);
        return template;
    }
}