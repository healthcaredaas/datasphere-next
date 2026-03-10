package cn.healthcaredaas.datasphere.svc.quality.mapper;

import cn.healthcaredaas.datasphere.svc.quality.entity.QualityTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 质量检测任务 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface QualityTaskMapper extends BaseMapper<QualityTask> {
}
