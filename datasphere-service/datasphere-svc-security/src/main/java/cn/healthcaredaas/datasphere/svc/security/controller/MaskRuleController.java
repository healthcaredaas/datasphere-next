package cn.healthcaredaas.datasphere.svc.security.controller;

import cn.healthcaredaas.datasphere.svc.security.entity.MaskRule;
import cn.healthcaredaas.datasphere.svc.security.service.MaskRuleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 脱敏规则控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/security/mask-rules")
@RequiredArgsConstructor
@Tag(name = "脱敏规则管理", description = "脱敏规则管理相关接口")
public class MaskRuleController {

    private final MaskRuleService maskRuleService;

    @Operation(summary = "分页查询脱敏规则列表")
    @GetMapping
    public IPage<MaskRule> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            MaskRule params) {
        return maskRuleService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取脱敏规则详情")
    @GetMapping("/{id}")
    public MaskRule getById(@PathVariable("id") String id) {
        return maskRuleService.getById(id);
    }

    @Operation(summary = "新增脱敏规则")
    @PostMapping
    public MaskRule save(@RequestBody @Validated MaskRule maskRule) {
        maskRuleService.save(maskRule);
        return maskRule;
    }

    @Operation(summary = "更新脱敏规则")
    @PutMapping("/{id}")
    public MaskRule update(@PathVariable("id") String id, @RequestBody @Validated MaskRule maskRule) {
        maskRule.setId(id);
        maskRuleService.updateById(maskRule);
        return maskRuleService.getById(id);
    }

    @Operation(summary = "删除脱敏规则")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        maskRuleService.removeById(id);
    }
}
