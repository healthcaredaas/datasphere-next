package cn.healthcaredaas.datasphere.svc.security.mapper;

import cn.healthcaredaas.datasphere.svc.security.entity.AccessLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 访问审计日志Mapper
 *
 * @author chenpan
 */
@Mapper
public interface AccessLogMapper extends BaseMapper<AccessLog> {

    /**
     * 统计访问趋势
     */
    @Select("SELECT DATE_FORMAT(access_time, '%Y-%m-%d') as date, COUNT(*) as count " +
            "FROM ds_access_log WHERE access_time >= #{startTime} AND access_time <= #{endTime} " +
            "AND delete_flag = '0' GROUP BY DATE_FORMAT(access_time, '%Y-%m-%d') ORDER BY date")
    List<Map<String, Object>> selectAccessTrend(@Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);
}
