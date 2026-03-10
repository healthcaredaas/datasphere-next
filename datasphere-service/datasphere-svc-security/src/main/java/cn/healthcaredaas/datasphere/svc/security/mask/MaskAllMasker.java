package cn.healthcaredaas.datasphere.svc.security.mask;

import org.springframework.stereotype.Component;

/**
 * 全遮盖脱敏器
 * 示例: 13812345678 -> ***********
 *
 * @author chenpan
 */
@Component
public class MaskAllMasker implements Masker {

    @Override
    public String mask(String value, String params) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return "*".repeat(value.length());
    }
}
