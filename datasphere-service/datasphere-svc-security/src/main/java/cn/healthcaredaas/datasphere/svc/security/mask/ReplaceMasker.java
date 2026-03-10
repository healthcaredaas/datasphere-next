package cn.healthcaredaas.datasphere.svc.security.mask;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * 替换脱敏器
 * 示例: value -> [已隐藏]
 *
 * @author chenpan
 */
@Component
public class ReplaceMasker implements Masker {

    @Override
    public String mask(String value, String params) {
        String replacement = "[已隐藏]";  // 默认替换值

        if (params != null && !params.isEmpty()) {
            try {
                JSONObject json = JSON.parseObject(params);
                replacement = json.getString("replacement");
            } catch (Exception e) {
                // 使用默认值
            }
        }

        return replacement;
    }
}
