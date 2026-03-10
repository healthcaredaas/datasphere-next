package cn.healthcaredaas.datasphere.svc.asset.mapper;

import cn.healthcaredaas.datasphere.svc.asset.entity.DataLineage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 数据血缘Mapper
 *
 * @author chenpan
 */
@Mapper
public interface DataLineageMapper extends BaseMapper<DataLineage> {

    /**
     * 查询资产的上游血缘
     */
    @Select("SELECT * FROM da_lineage WHERE asset_id = #{assetId} AND delete_flag = '0' ORDER BY create_time DESC")
    List<DataLineage> selectUpstreamByAssetId(@Param("assetId") String assetId);

    /**
     * 查询资产的下游血缘
     */
    @Select("SELECT * FROM da_lineage WHERE upstream_asset_id = #{assetId} AND delete_flag = '0' ORDER BY create_time DESC")
    List<DataLineage> selectDownstreamByAssetId(@Param("assetId") String assetId);
}
