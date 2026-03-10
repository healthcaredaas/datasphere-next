package cn.healthcaredaas.datasphere.api.integration.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 数据管道DTO
 *
 * @author chenpan
 */
@Data
public class DataPipelineDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String pipelineCode;
    private String pipelineName;
    private String projectId;
    private String sourceDsId;
    private String targetDsId;
    private String engineType;
    private String cronExpression;
    private Integer status;
}
