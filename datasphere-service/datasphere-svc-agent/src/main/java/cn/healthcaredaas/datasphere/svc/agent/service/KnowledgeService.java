package cn.healthcaredaas.datasphere.svc.agent.service;

import cn.healthcaredaas.datasphere.svc.agent.entity.Knowledge;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 知识库服务接口
 *
 * @author chenpan
 */
public interface KnowledgeService extends IService<Knowledge> {

    /**
     * 分页查询知识
     */
    IPage<Knowledge> pageQuery(IPage<Knowledge> page, Knowledge params);

    /**
     * 按类型查询知识
     */
    List<Knowledge> listByType(String knowledgeType);

    /**
     * 语义检索知识
     */
    List<Knowledge> searchKnowledge(String query, int limit);

    /**
     * 按标签查询知识
     */
    List<Knowledge> listByTags(List<String> tags);
}