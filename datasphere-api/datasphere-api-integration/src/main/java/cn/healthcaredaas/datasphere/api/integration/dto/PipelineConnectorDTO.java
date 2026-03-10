package cn.healthcaredaas.datasphere.api.integration.dto;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * 管道连接器DTO
 *
 * @author chenpan
 */
@Data
public class PipelineConnectorDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String pipelineId;
    private String connectorType;
    private String connectorName;
    private String pluginType;
    private JSONObject config;
    private Integer orderNo;
}
