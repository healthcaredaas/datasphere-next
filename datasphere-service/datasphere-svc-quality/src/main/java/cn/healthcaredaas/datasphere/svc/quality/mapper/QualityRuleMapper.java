package cn.healthcaredaas.datasphere.svc.quality.mapper;

import cn.healthcaredaas.datasphere.svc.quality.entity.QualityRule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 质量规则 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface QualityRuleMapper extends BaseMapper<QualityRule> {
}
