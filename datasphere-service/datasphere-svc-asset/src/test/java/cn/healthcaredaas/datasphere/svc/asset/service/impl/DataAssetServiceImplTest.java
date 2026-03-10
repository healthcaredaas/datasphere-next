package cn.healthcaredaas.datasphere.svc.asset.service.impl;

import cn.healthcaredaas.datasphere.svc.asset.entity.DataAsset;
import cn.healthcaredaas.datasphere.svc.asset.mapper.DataAssetMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 数据资产服务测试
 *
 * @author chenpan
 */
@ExtendWith(MockitoExtension.class)
class DataAssetServiceImplTest {

    @Mock
    private DataAssetMapper assetMapper;

    @InjectMocks
    private DataAssetServiceImpl assetService;

    private DataAsset testAsset;

    @BeforeEach
    void setUp() {
        testAsset = new DataAsset();
        testAsset.setId("1");
        testAsset.setAssetCode("ASSET_001");
        testAsset.setAssetName("测试资产");
        testAsset.setAssetType("TABLE");
        testAsset.setTableName("test_table");
        testAsset.setStatus(1);
    }

    @Test
    void testPageQuery_WithAssetName() {
        // Given
        DataAsset params = new DataAsset();
        params.setAssetName("测试");
        Page<DataAsset> page = new Page<>(1, 10);

        when(assetMapper.selectPage(any(), any())).thenReturn(page);

        // When
        IPage<DataAsset> result = assetService.pageQuery(page, params);

        // Then
        assertNotNull(result);
        verify(assetMapper).selectPage(any(), any());
    }

    @Test
    void testPageQuery_WithAssetType() {
        // Given
        DataAsset params = new DataAsset();
        params.setAssetType("TABLE");
        Page<DataAsset> page = new Page<>(1, 10);

        when(assetMapper.selectPage(any(), any())).thenReturn(page);

        // When
        IPage<DataAsset> result = assetService.pageQuery(page, params);

        // Then
        assertNotNull(result);
        verify(assetMapper).selectPage(any(), any());
    }

    @Test
    void testSaveAsset() {
        // Given
        when(assetMapper.insert(any(DataAsset.class))).thenReturn(1);

        // When
        boolean result = assetService.save(testAsset);

        // Then
        assertTrue(result);
        verify(assetMapper).insert(testAsset);
    }

    @Test
    void testUpdateAsset() {
        // Given
        when(assetMapper.updateById(any(DataAsset.class))).thenReturn(1);

        // When
        boolean result = assetService.updateById(testAsset);

        // Then
        assertTrue(result);
        verify(assetMapper).updateById(testAsset);
    }

    @Test
    void testDeleteAsset() {
        // Given
        when(assetMapper.deleteById("1")).thenReturn(1);

        // When
        boolean result = assetService.removeById("1");

        // Then
        assertTrue(result);
        verify(assetMapper).deleteById("1");
    }

    @Test
    void testGetAssetById() {
        // Given
        when(assetMapper.selectById("1")).thenReturn(testAsset);

        // When
        DataAsset result = assetService.getById("1");

        // Then
        assertNotNull(result);
        assertEquals("ASSET_001", result.getAssetCode());
        assertEquals("测试资产", result.getAssetName());
    }

    @Test
    void testGetAssetById_NotFound() {
        // Given
        when(assetMapper.selectById("999")).thenReturn(null);

        // When
        DataAsset result = assetService.getById("999");

        // Then
        assertNull(result);
    }
}
