package cn.healthcaredaas.datasphere.svc.agent.service.impl;

import cn.healthcaredaas.datasphere.svc.agent.entity.ToolDefinition;
import cn.healthcaredaas.datasphere.svc.agent.mapper.ToolDefinitionMapper;
import cn.healthcaredaas.datasphere.svc.agent.service.ToolDefinitionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工具定义服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ToolDefinitionServiceImpl extends ServiceImpl<ToolDefinitionMapper, ToolDefinition>
        implements ToolDefinitionService {

    @Override
    public IPage<ToolDefinition> pageQuery(IPage<ToolDefinition> page, ToolDefinition params) {
        LambdaQueryWrapper<ToolDefinition> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getToolName())) {
            wrapper.like(ToolDefinition::getToolName, params.getToolName());
        }

        if (StringUtils.isNotBlank(params.getToolType())) {
            wrapper.eq(ToolDefinition::getToolType, params.getToolType());
        }

        if (params.getStatus() != null) {
            wrapper.eq(ToolDefinition::getStatus, params.getStatus());
        }

        wrapper.orderByDesc(ToolDefinition::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<ToolDefinition> listEnabled() {
        LambdaQueryWrapper<ToolDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ToolDefinition::getStatus, 1)
                .orderByByAsc(ToolDefinition::getToolName);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public ToolDefinition getByName(String toolName) {
        LambdaQueryWrapper<ToolDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ToolDefinition::getToolName, toolName)
                .eq(ToolDefinition::getStatus, 1);
        return baseMapper.selectOne(wrapper);
    }
}