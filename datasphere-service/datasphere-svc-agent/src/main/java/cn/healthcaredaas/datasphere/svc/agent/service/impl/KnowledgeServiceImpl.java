package cn.healthcaredaas.datasphere.svc.agent.service.impl;

import cn.healthcaredaas.datasphere.svc.agent.entity.Knowledge;
import cn.healthcaredaas.datasphere.svc.agent.mapper.KnowledgeMapper;
import cn.healthcaredaas.datasphere.svc.agent.service.KnowledgeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 知识库服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl extends ServiceImpl<KnowledgeMapper, Knowledge>
        implements KnowledgeService {

    @Override
    public IPage<Knowledge> pageQuery(IPage<Knowledge> page, Knowledge params) {
        LambdaQueryWrapper<Knowledge> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getTitle())) {
            wrapper.like(Knowledge::getTitle, params.getTitle());
        }

        if (StringUtils.isNotBlank(params.getKnowledgeType())) {
            wrapper.eq(Knowledge::getKnowledgeType, params.getKnowledgeType());
        }

        if (StringUtils.isNotBlank(params.getTenantId())) {
            wrapper.eq(Knowledge::getTenantId, params.getTenantId());
        }

        wrapper.orderByDesc(Knowledge::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Knowledge> listByType(String knowledgeType) {
        LambdaQueryWrapper<Knowledge> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Knowledge::getKnowledgeType, knowledgeType)
                .orderByByAsc(Knowledge::getTitle);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<Knowledge> searchKnowledge(String query, int limit) {
        // TODO: 集成Dify后使用向量检索
        // 目前使用简单的模糊匹配
        LambdaQueryWrapper<Knowledge> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Knowledge::getTitle, query)
                .or()
                .like(Knowledge::getContent, query)
                .orderByDesc(Knowledge::getCreateTime)
                .last("LIMIT " + limit);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<Knowledge> listByTags(List<String> tags) {
        // TODO: 实现标签查询
        LambdaQueryWrapper<Knowledge> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Knowledge::getCreateTime);
        return baseMapper.selectList(wrapper);
    }
}