package cn.healthcaredaas.datasphere.svc.security.controller;

import cn.healthcaredaas.datasphere.svc.security.mask.MaskEngine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 脱敏服务控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/security/mask")
@RequiredArgsConstructor
@Tag(name = "脱敏服务", description = "数据脱敏服务接口")
public class MaskServiceController {

    private final MaskEngine maskEngine;

    @Operation(summary = "脱敏数据")
    @PostMapping
    public Map<String, Object> mask(
            @Parameter(description = "原始值") @RequestParam String value,
            @Parameter(description = "脱敏算法: MASK_ALL/MASK_PARTIAL/HASH/REPLACE/RANDOM/NULLIFY") @RequestParam String algorithm,
            @Parameter(description = "算法参数(JSON)") @RequestParam(required = false) String params) {
        String maskedValue = maskEngine.mask(value, algorithm, params);
        Map<String, Object> result = new HashMap<>();
        result.put("original", value);
        result.put("masked", maskedValue);
        result.put("algorithm", algorithm);
        return result;
    }

    @Operation(summary = "批量脱敏")
    @PostMapping("/batch")
    public Map<String, Object> maskBatch(@RequestBody Map<String, Object> request) {
        Map<String, Object> values = (Map<String, Object>) request.get("values");
        String algorithm = (String) request.get("algorithm");
        String params = (String) request.get("params");

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> maskedValues = new HashMap<>();

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String value = entry.getValue() != null ? entry.getValue().toString() : null;
            String masked = maskEngine.mask(value, algorithm, params);
            maskedValues.put(entry.getKey(), masked);
        }

        result.put("original", values);
        result.put("masked", maskedValues);
        result.put("algorithm", algorithm);
        return result;
    }

    @Operation(summary = "测试脱敏算法")
    @PostMapping("/test")
    public Map<String, Object> testMask(
            @Parameter(description = "原始值") @RequestParam String value,
            @Parameter(description = "脱敏算法") @RequestParam String algorithm) {
        Map<String, Object> result = new HashMap<>();

        // 测试所有算法
        if ("ALL".equals(algorithm)) {
            result.put("MASK_ALL", maskEngine.mask(value, "MASK_ALL", null));
            result.put("MASK_PARTIAL", maskEngine.mask(value, "MASK_PARTIAL", null));
            result.put("HASH(MD5)", maskEngine.mask(value, "HASH", "{\"algorithm\":\"md5\"}"));
            result.put("HASH(SHA256)", maskEngine.mask(value, "HASH", "{\"algorithm\":\"sha256\"}"));
            result.put("REPLACE", maskEngine.mask(value, "REPLACE", "{\"replacement\":\"[已隐藏]\"}"));
            result.put("RANDOM", maskEngine.mask(value, "RANDOM", null));
            result.put("NULLIFY", maskEngine.mask(value, "NULLIFY", null));
        } else {
            result.put("result", maskEngine.mask(value, algorithm, null));
        }

        result.put("original", value);
        return result;
    }
}
