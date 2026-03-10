package cn.healthcaredaas.datasphere.core.dto;

import lombok.Data;

/**
 * 列元数据DTO
 *
 * @author chenpan
 */
@Data
public class ColumnMeta {

    private String label;
    private String name;
    private Integer type;
    private String typeName;
    private Boolean autoIncrement;
}
