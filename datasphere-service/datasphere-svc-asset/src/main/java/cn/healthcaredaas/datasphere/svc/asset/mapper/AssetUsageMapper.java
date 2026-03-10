package cn.healthcaredaas.datasphere.svc.asset.mapper;

import cn.healthcaredaas.datasphere.svc.asset.entity.AssetUsage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 资产使用记录Mapper
 *
 * @author chenpan
 */
@Mapper
public interface AssetUsageMapper extends BaseMapper<AssetUsage> {

    /**
     * 统计资产访问次数
     */
    @Select("SELECT asset_id, COUNT(*) as access_count FROM da_asset_usage " +
            "WHERE access_time >= #{startTime} AND access_time <= #{endTime} AND delete_flag = '0' " +
            "GROUP BY asset_id ORDER BY access_count DESC LIMIT #{limit}")
    List<Map<String, Object>> selectTopAccessedAssets(@Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime,
                                                       @Param("limit") int limit);
}
