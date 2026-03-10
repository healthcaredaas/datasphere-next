package cn.healthcaredaas.datasphere.svc.security.mapper;

import cn.healthcaredaas.datasphere.svc.security.entity.SensitiveField;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 敏感字段识别Mapper
 *
 * @author chenpan
 */
@Mapper
public interface SensitiveFieldMapper extends BaseMapper<SensitiveField> {
}
