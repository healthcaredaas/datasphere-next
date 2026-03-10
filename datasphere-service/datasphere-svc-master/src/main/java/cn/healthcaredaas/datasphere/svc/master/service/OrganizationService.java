package cn.healthcaredaas.datasphere.svc.master.service;

import cn.healthcaredaas.datasphere.svc.master.entity.Organization;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 组织机构服务接口
 *
 * @author chenpan
 */
public interface OrganizationService extends IService<Organization> {

    IPage<Organization> pageQuery(IPage<Organization> page, Organization params);

    List<Organization> listTree();
}
