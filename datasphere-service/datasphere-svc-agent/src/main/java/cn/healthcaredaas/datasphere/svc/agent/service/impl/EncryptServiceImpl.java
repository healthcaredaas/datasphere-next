package cn.healthcaredaas.datasphere.svc.agent.service.impl;

import cn.healthcaredaas.datasphere.svc.agent.service.EncryptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 加密服务实现
 * 使用AES-256-GCM加密算法
 *
 * @author chenpan
 */
@Slf4j
@Service
public class EncryptServiceImpl implements EncryptService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    @Value("${agent.encryption.key:DataSphere2026SecretKeyForAgent}")
    private String encryptionKey;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }

        try {
            // 生成随机IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            // 创建密钥
            SecretKeySpec keySpec = new SecretKeySpec(
                    padKey(encryptionKey).getBytes(StandardCharsets.UTF_8), ALGORITHM);

            // 创建GCM参数
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            // 初始化加密器
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

            // 加密
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // 组合IV和密文: IV(12字节) + 密文 + Tag(16字节)
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            log.error("加密失败: {}", e.getMessage(), e);
            throw new RuntimeException("加密失败", e);
        }
    }

    @Override
    public String decrypt(String cipherText) {
        if (cipherText == null || cipherText.isEmpty()) {
            return cipherText;
        }

        try {
            // 解码Base64
            byte[] combined = Base64.getDecoder().decode(cipherText);

            // 提取IV和密文
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(combined, GCM_IV_LENGTH, encrypted, 0, encrypted.length);

            // 创建密钥
            SecretKeySpec keySpec = new SecretKeySpec(
                    padKey(encryptionKey).getBytes(StandardCharsets.UTF_8), ALGORITHM);

            // 创建GCM参数
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            // 初始化解密器
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

            // 解密
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("解密失败: {}", e.getMessage(), e);
            throw new RuntimeException("解密失败", e);
        }
    }

    @Override
    public boolean matches(String plainText, String cipherText) {
        if (plainText == null || cipherText == null) {
            return false;
        }

        try {
            String decrypted = decrypt(cipherText);
            return plainText.equals(decrypted);
        } catch (Exception e) {
            log.warn("匹配检查失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 填充密钥到32字节(AES-256需要32字节密钥)
     */
    private String padKey(String key) {
        if (key == null) {
            key = "DefaultSecretKey";
        }

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] paddedKey = new byte[32];

        if (keyBytes.length >= 32) {
            System.arraycopy(keyBytes, 0, paddedKey, 0, 32);
        } else {
            System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
            // 用原密钥循环填充剩余部分
            for (int i = keyBytes.length; i < 32; i++) {
                paddedKey[i] = keyBytes[i % keyBytes.length];
            }
        }

        return new String(paddedKey, StandardCharsets.UTF_8);
    }
}