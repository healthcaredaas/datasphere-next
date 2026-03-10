package cn.healthcaredaas.datasphere.svc.security.mask;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Component;

/**
 * 部分遮盖脱敏器
 * 示例: 13812345678 -> 138****5678
 *
 * @author chenpan
 */
@Component
public class MaskPartialMasker implements Masker {

    @Override
    public String mask(String value, String params) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        int prefix = 3;  // 默认保留前3位
        int suffix = 4;  // 默认保留后4位

        if (params != null && !params.isEmpty()) {
            try {
                JSONObject json = JSON.parseObject(params);
                prefix = json.getIntValue("prefix", 3);
                suffix = json.getIntValue("suffix", 4);
            } catch (Exception e) {
                // 使用默认值
            }
        }

        int length = value.length();
        if (length <= prefix + suffix) {
            return "*".repeat(length);
        }

        return value.substring(0, prefix) +
               "*".repeat(length - prefix - suffix) +
               value.substring(length - suffix);
    }
}
