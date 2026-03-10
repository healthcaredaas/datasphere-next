package cn.healthcaredaas.datasphere.svc.security.mask;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 脱敏引擎
 *
 * @author chenpan
 */
@Component
@RequiredArgsConstructor
public class MaskEngine {

    private final MaskAllMasker maskAllMasker;
    private final MaskPartialMasker maskPartialMasker;
    private final HashMasker hashMasker;
    private final ReplaceMasker replaceMasker;
    private final RandomMasker randomMasker;
    private final NullifyMasker nullifyMasker;

    private final Map<String, Masker> maskerMap = new HashMap<>();

    @PostConstruct
    public void init() {
        maskerMap.put("MASK_ALL", maskAllMasker);
        maskerMap.put("MASK_PARTIAL", maskPartialMasker);
        maskerMap.put("HASH", hashMasker);
        maskerMap.put("REPLACE", replaceMasker);
        maskerMap.put("RANDOM", randomMasker);
        maskerMap.put("NULLIFY", nullifyMasker);
    }

    /**
     * 执行脱敏
     *
     * @param value 原始值
     * @param algorithm 脱敏算法
     * @param params 算法参数
     * @return 脱敏后的值
     */
    public String mask(String value, String algorithm, String params) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        Masker masker = maskerMap.get(algorithm);
        if (masker == null) {
            return value;
        }

        return masker.mask(value, params);
    }

    /**
     * 执行脱敏(无参数)
     *
     * @param value 原始值
     * @param algorithm 脱敏算法
     * @return 脱敏后的值
     */
    public String mask(String value, String algorithm) {
        return mask(value, algorithm, null);
    }
}
