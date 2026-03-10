package cn.healthcaredaas.datasphere.svc.security.service;

import cn.healthcaredaas.datasphere.svc.security.entity.AccessLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 访问审计日志服务接口
 *
 * @author chenpan
 */
public interface AccessLogService extends IService<AccessLog> {

    IPage<AccessLog> pageQuery(IPage<AccessLog> page, AccessLog params);

    /**
     * 记录访问日志
     */
    void recordAccess(AccessLog log);

    /**
     * 获取访问趋势
     */
    List<Map<String, Object>> getAccessTrend(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取访问统计
     */
    Map<String, Object> getAccessStats(LocalDateTime startTime, LocalDateTime endTime);
}
