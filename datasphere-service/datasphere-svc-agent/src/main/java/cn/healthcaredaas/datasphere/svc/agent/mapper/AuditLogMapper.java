package cn.healthcaredaas.datasphere.svc.agent.mapper;

import cn.healthcaredaas.datasphere.svc.agent.entity.AuditLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Agent审计日志 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {
}