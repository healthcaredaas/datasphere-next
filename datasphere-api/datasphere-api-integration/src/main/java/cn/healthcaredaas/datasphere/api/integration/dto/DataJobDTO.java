package cn.healthcaredaas.datasphere.api.integration.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据作业DTO
 *
 * @author chenpan
 */
@Data
public class DataJobDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String jobCode;
    private String jobName;
    private String pipelineId;
    private String engineType;
    private String executeMode;
    private String cronExpression;
    private Integer isSchedule;
    private String configContent;
    private String runtimeParams;
    private Integer status;
    private LocalDateTime lastRunTime;
    private Integer lastRunStatus;
    private String description;
}
