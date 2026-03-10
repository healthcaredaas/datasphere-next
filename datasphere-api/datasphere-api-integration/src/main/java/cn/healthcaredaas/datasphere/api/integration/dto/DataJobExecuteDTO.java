package cn.healthcaredaas.datasphere.api.integration.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据作业执行记录DTO
 *
 * @author chenpan
 */
@Data
public class DataJobExecuteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String jobId;
    private String pipelineId;
    private String executeId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long readRows;
    private Long writeRows;
    private Long errorRows;
    private Long duration;
    private Integer status;
    private String errorMsg;
    private String executeLog;
    private Integer triggerType;
}
