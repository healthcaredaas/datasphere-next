package cn.healthcaredaas.datasphere.svc.security.service.impl;

import cn.healthcaredaas.datasphere.svc.security.entity.AccessLog;
import cn.healthcaredaas.datasphere.svc.security.mapper.AccessLogMapper;
import cn.healthcaredaas.datasphere.svc.security.service.AccessLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 访问审计日志服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccessLogServiceImpl extends ServiceImpl<AccessLogMapper, AccessLog>
        implements AccessLogService {

    @Override
    public IPage<AccessLog> pageQuery(IPage<AccessLog> page, AccessLog params) {
        LambdaQueryWrapper<AccessLog> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getUserId())) {
            wrapper.eq(AccessLog::getUserId, params.getUserId());
        }

        if (StringUtils.isNotBlank(params.getResourceType())) {
            wrapper.eq(AccessLog::getResourceType, params.getResourceType());
        }

        if (StringUtils.isNotBlank(params.getOperationType())) {
            wrapper.eq(AccessLog::getOperationType, params.getOperationType());
        }

        if (params.getAccessResult() != null) {
            wrapper.eq(AccessLog::getAccessResult, params.getAccessResult());
        }

        wrapper.orderByDesc(AccessLog::getAccessTime);

        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public void recordAccess(AccessLog accessLog) {
        accessLog.setAccessTime(LocalDateTime.now());
        save(accessLog);
    }

    @Override
    public List<Map<String, Object>> getAccessTrend(LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.selectAccessTrend(startTime, endTime);
    }

    @Override
    public Map<String, Object> getAccessStats(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> stats = new HashMap<>();

        LambdaQueryWrapper<AccessLog> wrapper = new LambdaQueryWrapper<>();
        if (startTime != null) {
            wrapper.ge(AccessLog::getAccessTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(AccessLog::getAccessTime, endTime);
        }

        long totalAccess = count(wrapper);
        stats.put("totalAccess", totalAccess);

        // 成功/失败统计
        wrapper.eq(AccessLog::getAccessResult, 1);
        long successCount = count(wrapper);
        stats.put("successCount", successCount);
        stats.put("failCount", totalAccess - successCount);

        // 按资源类型统计
        List<AccessLog> logs = list();
        Map<String, Long> resourceTypeStats = new HashMap<>();
        for (AccessLog accessLog : logs) {
            resourceTypeStats.merge(accessLog.getResourceType(), 1L, Long::sum);
        }
        stats.put("resourceTypeStats", resourceTypeStats);

        return stats;
    }
}
