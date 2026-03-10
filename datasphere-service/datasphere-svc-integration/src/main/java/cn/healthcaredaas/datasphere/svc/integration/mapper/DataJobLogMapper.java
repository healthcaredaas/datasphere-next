package cn.healthcaredaas.datasphere.svc.integration.mapper;

import cn.healthcaredaas.datasphere.svc.integration.entity.DataJobLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据作业日志 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface DataJobLogMapper extends BaseMapper<DataJobLog> {
}
