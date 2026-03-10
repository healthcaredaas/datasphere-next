package cn.healthcaredaas.datasphere.svc.datasource.mapper;

import cn.healthcaredaas.datasphere.svc.datasource.entity.DatasourceInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源信息 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface DatasourceInfoMapper extends BaseMapper<DatasourceInfo> {
}
