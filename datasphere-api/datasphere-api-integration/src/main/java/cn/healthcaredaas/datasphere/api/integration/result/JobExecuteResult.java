package cn.healthcaredaas.datasphere.api.integration.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 作业执行结果
 *
 * @author chenpan
 */
@Data
public class JobExecuteResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private String executeId;

    public static JobExecuteResult ok(String executeId) {
        JobExecuteResult result = new JobExecuteResult();
        result.setSuccess(true);
        result.setExecuteId(executeId);
        return result;
    }

    public static JobExecuteResult fail(String message) {
        JobExecuteResult result = new JobExecuteResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }
}
