package cn.healthcaredaas.datasphere.svc.quality.mapper;

import cn.healthcaredaas.datasphere.svc.quality.entity.QualityIssue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 质量问题Mapper
 *
 * @author chenpan
 */
@Mapper
public interface QualityIssueMapper extends BaseMapper<QualityIssue> {
}
