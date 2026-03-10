package cn.healthcaredaas.datasphere.svc.asset.mapper;

import cn.healthcaredaas.datasphere.svc.asset.entity.AssetCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产分类Mapper
 *
 * @author chenpan
 */
@Mapper
public interface AssetCategoryMapper extends BaseMapper<AssetCategory> {
}
