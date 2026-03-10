package cn.healthcaredaas.datasphere.svc.agent.tools;

import com.alibaba.fastjson2.JSONObject;

/**
 * 工具执行接口
 *
 * @author chenpan
 */
public interface Tool {

    /**
     * 获取工具名称
     */
    String getName();

    /**
     * 获取工具描述
     */
    String getDescription();

    /**
     * 获取输入参数Schema
     */
    JSONObject getInputSchema();

    /**
     * 执行工具
     *
     * @param params 输入参数
     * @param context 执行上下文
     * @return 执行结果
     */
    ToolResult execute(JSONObject params, ToolContext context);
}