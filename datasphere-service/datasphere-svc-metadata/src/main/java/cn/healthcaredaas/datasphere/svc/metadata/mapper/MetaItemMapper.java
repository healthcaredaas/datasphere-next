package cn.healthcaredaas.datasphere.svc.metadata.mapper;

import cn.healthcaredaas.datasphere.svc.metadata.entity.MetaItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 元数据项 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface MetaItemMapper extends BaseMapper<MetaItem> {
}
