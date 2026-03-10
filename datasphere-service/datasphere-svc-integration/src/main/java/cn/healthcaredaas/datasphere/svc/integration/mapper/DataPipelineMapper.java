package cn.healthcaredaas.datasphere.svc.integration.mapper;

import cn.healthcaredaas.datasphere.svc.integration.entity.DataPipeline;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据管道 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface DataPipelineMapper extends BaseMapper<DataPipeline> {
}
