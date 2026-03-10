package cn.healthcaredaas.datasphere.core.util;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 加密工具类
 *
 * @author chenpan
 */
public class EncryptUtils {

    private static final String MD5 = "MD5";
    private static final String SHA256 = "SHA-256";

    /**
     * MD5加密
     */
    public static String md5(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * MD5加密（加盐）
     */
    public static String md5(String str, String salt) {
        if (str == null) {
            return null;
        }
        return DigestUtils.md5DigestAsHex((str + salt).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * SHA256加密
     */
    public static String sha256(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(SHA256);
            byte[] hash = digest.digest(str.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA256 algorithm not found", e);
        }
    }

    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 生成UUID（无横线）
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成UUID（有横线）
     */
    public static String uuidWithDash() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成随机盐值
     */
    public static String generateSalt() {
        return uuid().substring(0, 16);
    }

    /**
     * 简单脱敏（保留前3后4）
     */
    public static String simpleMask(String str) {
        if (str == null || str.length() < 8) {
            return str;
        }
        return str.substring(0, 3) + "****" + str.substring(str.length() - 4);
    }
}
