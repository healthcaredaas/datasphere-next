package cn.healthcaredaas.datasphere.svc.agent.service;

import cn.healthcaredaas.datasphere.svc.agent.entity.ToolDefinition;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 工具定义服务接口
 *
 * @author chenpan
 */
public interface ToolDefinitionService extends IService<ToolDefinition> {

    /**
     * 分页查询工具定义
     */
    IPage<ToolDefinition> pageQuery(IPage<ToolDefinition> page, ToolDefinition params);

    /**
     * 获取启用的工具列表
     */
    List<ToolDefinition> listEnabled();

    /**
     * 根据名称获取工具
     */
    ToolDefinition getByName(String toolName);
}