package cn.healthcaredaas.datasphere.svc.security.mask;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * 随机化脱敏器
 * 保留格式，内容随机化
 * 示例: 13812345678 -> 15987654321
 *
 * @author chenpan
 */
@Component
public class RandomMasker implements Masker {

    private final SecureRandom random = new SecureRandom();

    @Override
    public String mask(String value, String params) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        StringBuilder result = new StringBuilder();
        for (char c : value.toCharArray()) {
            if (Character.isDigit(c)) {
                result.append(random.nextInt(10));
            } else if (Character.isLetter(c)) {
                if (Character.isUpperCase(c)) {
                    result.append((char) ('A' + random.nextInt(26)));
                } else {
                    result.append((char) ('a' + random.nextInt(26)));
                }
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}
