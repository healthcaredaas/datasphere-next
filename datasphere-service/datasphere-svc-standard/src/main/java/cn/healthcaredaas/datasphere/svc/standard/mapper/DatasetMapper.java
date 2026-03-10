package cn.healthcaredaas.datasphere.svc.standard.mapper;

import cn.healthcaredaas.datasphere.svc.standard.entity.Dataset;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据集 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface DatasetMapper extends BaseMapper<Dataset> {
}
