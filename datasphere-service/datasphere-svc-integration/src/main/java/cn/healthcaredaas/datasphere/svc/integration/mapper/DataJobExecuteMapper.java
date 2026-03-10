package cn.healthcaredaas.datasphere.svc.integration.mapper;

import cn.healthcaredaas.datasphere.svc.integration.entity.DataJobExecute;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据作业执行记录 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface DataJobExecuteMapper extends BaseMapper<DataJobExecute> {
}
