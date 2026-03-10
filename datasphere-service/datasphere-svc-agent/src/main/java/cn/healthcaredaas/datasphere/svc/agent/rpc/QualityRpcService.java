package cn.healthcaredaas.datasphere.svc.agent.rpc;

import com.alibaba.fastjson2.JSONObject;

import java.util.List;

/**
 * 数据质量服务接口（RPC调用）
 *
 * @author chenpan
 */
public interface QualityRpcService {

    /**
     * 获取规则详情
     */
    JSONObject getRule(String ruleId);

    /**
     * 创建质量规则
     */
    String createRule(JSONObject rule);

    /**
     * 更新质量规则
     */
    void updateRule(String ruleId, JSONObject rule);

    /**
     * 删除质量规则
     */
    void deleteRule(String ruleId);

    /**
     * 获取规则模板列表
     */
    List<JSONObject> listRuleTemplates();

    /**
     * 从模板创建规则
     */
    String createRuleFromTemplate(String templateId, JSONObject params);

    /**
     * 执行质量检测
     */
    String executeQualityCheck(String ruleId);

    /**
     * 获取检测结果
     */
    JSONObject getCheckResult(String taskId);

    /**
     * 获取质量报告
     */
    JSONObject getQualityReport(String datasourceId, String tableName);
}