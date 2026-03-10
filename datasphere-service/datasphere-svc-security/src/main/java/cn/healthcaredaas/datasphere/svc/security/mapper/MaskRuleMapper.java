package cn.healthcaredaas.datasphere.svc.security.mapper;

import cn.healthcaredaas.datasphere.svc.security.entity.MaskRule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 脱敏规则 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface MaskRuleMapper extends BaseMapper<MaskRule> {
}
