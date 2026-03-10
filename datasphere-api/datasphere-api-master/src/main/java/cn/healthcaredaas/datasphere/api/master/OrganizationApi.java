package cn.healthcaredaas.datasphere.api.master;

import cn.healthcaredaas.datasphere.api.master.dto.OrganizationDTO;

import java.util.List;

/**
 * 组织机构 Dubbo 接口
 *
 * @author chenpan
 */
public interface OrganizationApi {

    /**
     * 根据ID获取组织机构
     *
     * @param id 机构ID
     * @return 机构信息
     */
    OrganizationDTO getById(String id);

    /**
     * 获取所有组织机构树
     *
     * @return 机构树列表
     */
    List<OrganizationDTO> listTree();

    /**
     * 根据编码获取机构
     *
     * @param orgCode 机构编码
     * @return 机构信息
     */
    OrganizationDTO getByCode(String orgCode);
}
