package cn.healthcaredaas.datasphere.svc.security.mask;

/**
 * 脱敏算法接口
 *
 * @author chenpan
 */
public interface Masker {

    /**
     * 执行脱敏
     *
     * @param value 原始值
     * @param params 算法参数
     * @return 脱敏后的值
     */
    String mask(String value, String params);
}
