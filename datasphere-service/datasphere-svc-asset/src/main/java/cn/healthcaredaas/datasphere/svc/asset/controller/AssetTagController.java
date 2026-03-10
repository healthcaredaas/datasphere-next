package cn.healthcaredaas.datasphere.svc.asset.controller;

import cn.healthcaredaas.datasphere.svc.asset.entity.AssetTag;
import cn.healthcaredaas.datasphere.svc.asset.service.AssetTagService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 资产标签控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/asset/tags")
@RequiredArgsConstructor
@Tag(name = "资产标签管理", description = "资产标签管理相关接口")
public class AssetTagController {

    private final AssetTagService tagService;

    @Operation(summary = "分页查询资产标签列表")
    @GetMapping
    public IPage<AssetTag> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            AssetTag params) {
        return tagService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取资产标签详情")
    @GetMapping("/{id}")
    public AssetTag getById(@PathVariable("id") String id) {
        return tagService.getById(id);
    }

    @Operation(summary = "新增资产标签")
    @PostMapping
    public AssetTag save(@RequestBody @Validated AssetTag tag) {
        tagService.save(tag);
        return tag;
    }

    @Operation(summary = "更新资产标签")
    @PutMapping("/{id}")
    public AssetTag update(@PathVariable("id") String id, @RequestBody @Validated AssetTag tag) {
        tag.setId(id);
        tagService.updateById(tag);
        return tagService.getById(id);
    }

    @Operation(summary = "删除资产标签")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        tagService.removeById(id);
    }
}
