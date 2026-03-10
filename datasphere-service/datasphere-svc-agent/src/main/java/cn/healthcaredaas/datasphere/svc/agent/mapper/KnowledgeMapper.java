package cn.healthcaredaas.datasphere.svc.agent.mapper;

import cn.healthcaredaas.datasphere.svc.agent.entity.Knowledge;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识库 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface KnowledgeMapper extends BaseMapper<Knowledge> {
}