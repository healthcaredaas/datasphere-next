package cn.healthcaredaas.datasphere.svc.quality.controller;

import cn.healthcaredaas.datasphere.svc.quality.entity.QualityRule;
import cn.healthcaredaas.datasphere.svc.quality.service.QualityRuleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 质量规则控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/quality/rules")
@RequiredArgsConstructor
@Tag(name = "质量规则管理", description = "质量规则管理相关接口")
public class QualityRuleController {

    private final QualityRuleService qualityRuleService;

    @Operation(summary = "分页查询质量规则列表")
    @GetMapping
    public IPage<QualityRule> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            QualityRule params) {
        return qualityRuleService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取质量规则详情")
    @GetMapping("/{id}")
    public QualityRule getById(@PathVariable("id") String id) {
        return qualityRuleService.getById(id);
    }

    @Operation(summary = "新增质量规则")
    @PostMapping
    public QualityRule save(@RequestBody @Validated QualityRule qualityRule) {
        qualityRuleService.save(qualityRule);
        return qualityRule;
    }

    @Operation(summary = "更新质量规则")
    @PutMapping("/{id}")
    public QualityRule update(@PathVariable("id") String id, @RequestBody @Validated QualityRule qualityRule) {
        qualityRule.setId(id);
        qualityRuleService.updateById(qualityRule);
        return qualityRuleService.getById(id);
    }

    @Operation(summary = "删除质量规则")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        qualityRuleService.removeById(id);
    }

    @Operation(summary = "测试规则SQL")
    @PostMapping("/{id}/test")
    public Map<String, Object> testRule(@PathVariable("id") String id) {
        boolean valid = qualityRuleService.testRule(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", valid);
        result.put("message", valid ? "规则验证通过" : "规则验证失败");
        return result;
    }

    @Operation(summary = "从模板创建规则")
    @PostMapping("/create-from-template")
    public Map<String, Object> createFromTemplate(
            @RequestParam String templateId,
            @RequestBody Map<String, Object> params) {
        String ruleId = qualityRuleService.createRuleFromTemplate(templateId, params);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("ruleId", ruleId);
        return result;
    }
}
