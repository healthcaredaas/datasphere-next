package cn.healthcaredaas.datasphere.svc.security.mask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 脱敏引擎测试
 *
 * @author chenpan
 */
class MaskEngineTest {

    private MaskEngine maskEngine;
    private MaskAllMasker maskAllMasker;
    private MaskPartialMasker maskPartialMasker;
    private HashMasker hashMasker;
    private ReplaceMasker replaceMasker;
    private RandomMasker randomMasker;
    private NullifyMasker nullifyMasker;

    @BeforeEach
    void setUp() {
        maskAllMasker = new MaskAllMasker();
        maskPartialMasker = new MaskPartialMasker();
        hashMasker = new HashMasker();
        replaceMasker = new ReplaceMasker();
        randomMasker = new RandomMasker();
        nullifyMasker = new NullifyMasker();

        maskEngine = new MaskEngine(
            maskAllMasker,
            maskPartialMasker,
            hashMasker,
            replaceMasker,
            randomMasker,
            nullifyMasker
        );
        maskEngine.init();
    }

    @Test
    void testMaskAll() {
        // When
        String result = maskEngine.mask("13812345678", "MASK_ALL");

        // Then
        assertEquals("***********", result);
    }

    @Test
    void testMaskPartial_Phone() {
        // When
        String result = maskEngine.mask("13812345678", "MASK_PARTIAL");

        // Then
        assertEquals("138****5678", result);
    }

    @Test
    void testMaskPartial_WithParams() {
        // When
        String result = maskEngine.mask("chenpan@example.com", "MASK_PARTIAL", "{\"prefix\":2,\"suffix\":4}");

        // Then
        assertTrue(result.startsWith("ch"));
        assertTrue(result.endsWith(".com"));
        assertTrue(result.contains("***"));
    }

    @Test
    void testHash_MD5() {
        // When
        String result = maskEngine.mask("password123", "HASH");

        // Then
        assertNotNull(result);
        assertEquals(32, result.length()); // MD5长度为32
    }

    @Test
    void testHash_SHA256() {
        // When
        String result = maskEngine.mask("password123", "HASH", "{\"algorithm\":\"sha256\"}");

        // Then
        assertNotNull(result);
        assertEquals(64, result.length()); // SHA256长度为64
    }

    @Test
    void testReplace() {
        // When
        String result = maskEngine.mask("敏感数据", "REPLACE");

        // Then
        assertEquals("[已隐藏]", result);
    }

    @Test
    void testReplace_WithCustomValue() {
        // When
        String result = maskEngine.mask("敏感数据", "REPLACE", "{\"replacement\":\"***\"}");

        // Then
        assertEquals("***", result);
    }

    @Test
    void testRandom() {
        // When
        String original = "ABC123";
        String result = maskEngine.mask(original, "RANDOM");

        // Then
        assertNotNull(result);
        assertEquals(original.length(), result.length());
        assertNotEquals(original, result);
    }

    @Test
    void testNullify() {
        // When
        String result = maskEngine.mask("任何数据", "NULLIFY");

        // Then
        assertNull(result);
    }

    @Test
    void testMask_NullValue() {
        // When
        String result = maskEngine.mask(null, "MASK_ALL");

        // Then
        assertNull(result);
    }

    @Test
    void testMask_EmptyValue() {
        // When
        String result = maskEngine.mask("", "MASK_ALL");

        // Then
        assertEquals("", result);
    }

    @Test
    void testMask_UnknownAlgorithm() {
        // When
        String result = maskEngine.mask("test", "UNKNOWN");

        // Then
        assertEquals("test", result); // 未知算法返回原值
    }
}
