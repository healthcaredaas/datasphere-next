package cn.healthcaredaas.datasphere.svc.quality.controller;

import cn.healthcaredaas.datasphere.svc.quality.entity.QualityRuleTemplate;
import cn.healthcaredaas.datasphere.svc.quality.mapper.QualityRuleTemplateMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 质量规则模板控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/quality/templates")
@RequiredArgsConstructor
@Tag(name = "质量规则模板管理", description = "质量规则模板管理相关接口")
public class QualityRuleTemplateController {

    private final QualityRuleTemplateMapper templateMapper;

    @Operation(summary = "分页查询规则模板列表")
    @GetMapping
    public IPage<QualityRuleTemplate> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String ruleType) {
        LambdaQueryWrapper<QualityRuleTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QualityRuleTemplate::getStatus, 1);
        if (StringUtils.isNotBlank(ruleType)) {
            wrapper.eq(QualityRuleTemplate::getRuleType, ruleType);
        }
        wrapper.orderByAsc(QualityRuleTemplate::getSortNo);
        return templateMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Operation(summary = "获取规则模板详情")
    @GetMapping("/{id}")
    public QualityRuleTemplate getById(@PathVariable("id") String id) {
        return templateMapper.selectById(id);
    }

    @Operation(summary = "新增规则模板")
    @PostMapping
    public QualityRuleTemplate save(@RequestBody @Validated QualityRuleTemplate template) {
        templateMapper.insert(template);
        return template;
    }

    @Operation(summary = "更新规则模板")
    @PutMapping("/{id}")
    public QualityRuleTemplate update(@PathVariable("id") String id, @RequestBody @Validated QualityRuleTemplate template) {
        template.setId(id);
        templateMapper.updateById(template);
        return templateMapper.selectById(id);
    }

    @Operation(summary = "删除规则模板")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        templateMapper.deleteById(id);
    }
}
