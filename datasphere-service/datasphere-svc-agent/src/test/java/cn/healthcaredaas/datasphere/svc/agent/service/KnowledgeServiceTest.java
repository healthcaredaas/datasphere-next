package cn.healthcaredaas.datasphere.svc.agent.service;

import cn.healthcaredaas.datasphere.svc.agent.entity.Knowledge;
import cn.healthcaredaas.datasphere.svc.agent.mapper.KnowledgeMapper;
import cn.healthcaredaas.datasphere.svc.agent.service.impl.KnowledgeServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * KnowledgeService 单元测试
 *
 * @author chenpan
 */
@ExtendWith(MockitoExtension.class)
class KnowledgeServiceTest {

    @Mock
    private KnowledgeMapper knowledgeMapper;

    @InjectMocks
    private KnowledgeServiceImpl knowledgeService;

    private Knowledge testKnowledge;

    @BeforeEach
    void setUp() {
        testKnowledge = new Knowledge();
        testKnowledge.setId("knowledge-001");
        testKnowledge.setTitle("门诊数据表结构说明");
        testKnowledge.setKnowledgeType("METADATA");
        testKnowledge.setContent("门诊数据表(outp_visit)包含门诊挂号信息...");
        testKnowledge.setTenantId("tenant-001");
    }

    @Test
    @DisplayName("分页查询知识")
    void testPageQuery() {
        // Given
        Page<Knowledge> page = new Page<>(1, 10);
        when(knowledgeMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        Knowledge params = new Knowledge();
        params.setKnowledgeType("METADATA");
        params.setTenantId("tenant-001");

        // When
        IPage<Knowledge> result = knowledgeService.pageQuery(page, params);

        // Then
        assertNotNull(result);
        verify(knowledgeMapper, times(1)).selectPage(any(IPage.class), any());
    }

    @Test
    @DisplayName("按类型查询知识")
    void testListByType() {
        // Given
        when(knowledgeMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testKnowledge));

        // When
        List<Knowledge> result = knowledgeService.listByType("METADATA");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("METADATA", result.get(0).getKnowledgeType());
    }

    @Test
    @DisplayName("按类型查询知识 - 空结果")
    void testListByType_EmptyResult() {
        // Given
        when(knowledgeMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of());

        // When
        List<Knowledge> result = knowledgeService.listByType("UNKNOWN");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("语义检索知识")
    void testSearchKnowledge() {
        // Given
        Knowledge knowledge1 = new Knowledge();
        knowledge1.setId("k-001");
        knowledge1.setTitle("门诊数据");
        knowledge1.setContent("门诊数据表说明");

        Knowledge knowledge2 = new Knowledge();
        knowledge2.setId("k-002");
        knowledge2.setTitle("住院数据");
        knowledge2.setContent("住院数据表说明");

        when(knowledgeMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(knowledge1, knowledge2));

        // When
        List<Knowledge> result = knowledgeService.searchKnowledge("门诊", 10);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("语义检索知识 - 限制数量")
    void testSearchKnowledge_WithLimit() {
        // Given
        when(knowledgeMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testKnowledge));

        // When
        List<Knowledge> result = knowledgeService.searchKnowledge("测试", 5);

        // Then
        assertNotNull(result);
        verify(knowledgeMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("按标签查询知识")
    void testListByTags() {
        // Given
        when(knowledgeMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testKnowledge));

        // When
        List<Knowledge> result = knowledgeService.listByTags(List.of("医疗", "门诊"));

        // Then
        assertNotNull(result);
        verify(knowledgeMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
    }
}