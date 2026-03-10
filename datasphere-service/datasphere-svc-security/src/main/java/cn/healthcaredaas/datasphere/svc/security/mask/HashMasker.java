package cn.healthcaredaas.datasphere.svc.security.mask;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Component;

/**
 * 哈希脱敏器
 * 示例: value -> md5(value)
 *
 * @author chenpan
 */
@Component
public class HashMasker implements Masker {

    @Override
    public String mask(String value, String params) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        String algorithm = "md5";  // 默认MD5

        if (params != null && !params.isEmpty()) {
            try {
                JSONObject json = JSON.parseObject(params);
                algorithm = json.getString("algorithm");
            } catch (Exception e) {
                // 使用默认值
            }
        }

        return switch (algorithm.toLowerCase()) {
            case "sha256" -> DigestUtil.sha256Hex(value);
            case "sha1" -> DigestUtil.sha1Hex(value);
            default -> DigestUtil.md5Hex(value);
        };
    }
}
