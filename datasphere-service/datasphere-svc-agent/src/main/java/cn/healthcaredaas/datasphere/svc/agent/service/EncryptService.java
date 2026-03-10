package cn.healthcaredaas.datasphere.svc.agent.service;

/**
 * 加密服务接口
 * 提供AES-256加密/解密功能
 *
 * @author chenpan
 */
public interface EncryptService {

    /**
     * 加密字符串
     *
     * @param plainText 明文
     * @return 密文(Base64编码)
     */
    String encrypt(String plainText);

    /**
     * 解密字符串
     *
     * @param cipherText 密文(Base64编码)
     * @return 明文
     */
    String decrypt(String cipherText);

    /**
     * 检查是否匹配
     *
     * @param plainText  明文
     * @param cipherText 密文
     * @return 是否匹配
     */
    boolean matches(String plainText, String cipherText);
}