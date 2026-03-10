package cn.healthcaredaas.datasphere.svc.asset.mapper;

import cn.healthcaredaas.datasphere.svc.asset.entity.DataAsset;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据资产 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface DataAssetMapper extends BaseMapper<DataAsset> {
}
