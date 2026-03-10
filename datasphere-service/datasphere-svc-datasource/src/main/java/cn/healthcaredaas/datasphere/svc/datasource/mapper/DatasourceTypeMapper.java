package cn.healthcaredaas.datasphere.svc.datasource.mapper;

import cn.healthcaredaas.datasphere.svc.datasource.entity.DatasourceType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源类型 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface DatasourceTypeMapper extends BaseMapper<DatasourceType> {
}
