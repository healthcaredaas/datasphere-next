package cn.healthcaredaas.datasphere.svc.integration.mapper;

import cn.healthcaredaas.datasphere.svc.integration.entity.DataJob;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据作业 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface DataJobMapper extends BaseMapper<DataJob> {
}
