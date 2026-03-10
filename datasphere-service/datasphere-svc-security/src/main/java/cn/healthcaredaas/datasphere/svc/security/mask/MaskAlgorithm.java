package cn.healthcaredaas.datasphere.svc.security.mask;

/**
 * 脱敏算法枚举
 *
 * @author chenpan
 */
public enum MaskAlgorithm {
    /**
     * 全遮盖: 138****8888
     */
    MASK_ALL,

    /**
     * 部分遮盖: 138****1234
     */
    MASK_PARTIAL,

    /**
     * 哈希: md5/sha256
     */
    HASH,

    /**
     * 替换: 固定值替换
     */
    REPLACE,

    /**
     * 随机化: 保留格式随机生成
     */
    RANDOM,

    /**
     * 置空: 返回null
     */
    NULLIFY
}
