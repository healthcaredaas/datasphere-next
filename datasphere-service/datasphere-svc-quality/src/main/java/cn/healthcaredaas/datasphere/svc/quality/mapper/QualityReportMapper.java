package cn.healthcaredaas.datasphere.svc.quality.mapper;

import cn.healthcaredaas.datasphere.svc.quality.entity.QualityReport;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 质量报告Mapper
 *
 * @author chenpan
 */
@Mapper
public interface QualityReportMapper extends BaseMapper<QualityReport> {
}
