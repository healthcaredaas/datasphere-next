package cn.healthcaredaas.datasphere.svc.security.mask;

import org.springframework.stereotype.Component;

/**
 * 置空脱敏器
 * 返回null
 *
 * @author chenpan
 */
@Component
public class NullifyMasker implements Masker {

    @Override
    public String mask(String value, String params) {
        return null;
    }
}
