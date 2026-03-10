package cn.healthcaredaas.datasphere.svc.standard.mapper;

import cn.healthcaredaas.datasphere.svc.standard.entity.Indicator;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 指标 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface IndicatorMapper extends BaseMapper<Indicator> {
}
