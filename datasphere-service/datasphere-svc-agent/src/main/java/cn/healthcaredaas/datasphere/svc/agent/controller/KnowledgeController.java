package cn.healthcaredaas.datasphere.svc.agent.controller;

import cn.healthcaredaas.datasphere.core.web.BaseController;
import cn.healthcaredaas.datasphere.svc.agent.entity.Knowledge;
import cn.healthcaredaas.datasphere.svc.agent.service.KnowledgeService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/agent/knowledge")
@RequiredArgsConstructor
@Tag(name = "知识库管理", description = "知识库管理相关接口")
public class KnowledgeController extends BaseController {

    private final KnowledgeService knowledgeService;

    @Operation(summary = "分页查询知识")
    @GetMapping
    public IPage<Knowledge> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            Knowledge params) {
        return knowledgeService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "按类型查询知识")
    @GetMapping("/type/{type}")
    public List<Knowledge> listByType(@PathVariable("type") String type) {
        return knowledgeService.listByType(type);
    }

    @Operation(summary = "语义检索知识")
    @GetMapping("/search")
    public List<Knowledge> search(
            @Parameter(description = "查询关键词") @RequestParam String query,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") int limit) {
        return knowledgeService.searchKnowledge(query, limit);
    }

    @Operation(summary = "获取知识详情")
    @GetMapping("/{id}")
    public Knowledge getById(@PathVariable("id") String id) {
        return knowledgeService.getById(id);
    }

    @Operation(summary = "新增知识")
    @PostMapping
    public Knowledge save(@RequestBody Knowledge knowledge) {
        knowledgeService.save(knowledge);
        return knowledge;
    }

    @Operation(summary = "更新知识")
    @PutMapping("/{id}")
    public Knowledge update(@PathVariable("id") String id, @RequestBody Knowledge knowledge) {
        knowledge.setId(id);
        knowledgeService.updateById(knowledge);
        return knowledgeService.getById(id);
    }

    @Operation(summary = "删除知识")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        knowledgeService.removeById(id);
    }

    @Operation(summary = "批量导入知识")
    @PostMapping("/batch")
    public List<Knowledge> batchSave(@RequestBody List<Knowledge> knowledgeList) {
        knowledgeService.saveBatch(knowledgeList);
        return knowledgeList;
    }
}