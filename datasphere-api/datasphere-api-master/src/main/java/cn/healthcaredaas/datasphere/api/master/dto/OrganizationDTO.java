package cn.healthcaredaas.datasphere.api.master.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 组织机构DTO
 *
 * @author chenpan
 */
@Data
public class OrganizationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String orgCode;
    private String orgName;
    private String parentId;
    private Integer orgLevel;
    private String orgType;
    private String address;
    private String phone;
}
