package cn.healthcaredaas.datasphere.svc.quality.engine;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 规则执行结果
 *
 * @author chenpan
 */
@Data
@Builder
public class RuleExecuteResult {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 检测总行数
     */
    private Long totalRows;

    /**
     * 检测总行数(别名，用于兼容)
     */
    public Long getTotalCount() {
        return totalRows;
    }

    public void setTotalCount(Long totalCount) {
        this.totalRows = totalCount;
    }

    /**
     * 错误行数
     */
    private Long errorRows;

    /**
     * 错误率(0-100)
     */
    private Double errorRate;

    /**
     * 错误数据详情
     */
    private List<ErrorDetail> errorDetails;

    /**
     * 错误描述
     */
    private String errorMessage;

    /**
     * 执行耗时(ms)
     */
    private Long duration;

    /**
     * 错误详情
     */
    @Data
    @Builder
    public static class ErrorDetail {
        /**
         * 主键值(JSON格式)
         */
        private String primaryKeyValue;

        /**
         * 主键值(别名，用于兼容)
         */
        public String getPrimaryKey() {
            return primaryKeyValue;
        }

        public void setPrimaryKey(String primaryKey) {
            this.primaryKeyValue = primaryKey;
        }

        /**
         * 错误字段值
         */
        private String errorValue;

        /**
         * 期望值
         */
        private String expectedValue;

        /**
         * 错误描述
         */
        private String message;
    }
}
