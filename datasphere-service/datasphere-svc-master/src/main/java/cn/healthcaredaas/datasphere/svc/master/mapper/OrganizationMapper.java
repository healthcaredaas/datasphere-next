package cn.healthcaredaas.datasphere.svc.master.mapper;

import cn.healthcaredaas.datasphere.svc.master.entity.Organization;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 组织机构 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface OrganizationMapper extends BaseMapper<Organization> {
}
