package cn.healthcaredaas.datasphere.api.datasource.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 数据源信息DTO
 *
 * @author chenpan
 */
@Data
public class DatasourceInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String dsName;
    private String dsType;
    private String dsConfigJson;
    private String envProfile;
    private String note;
}
