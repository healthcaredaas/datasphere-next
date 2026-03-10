package cn.healthcaredaas.datasphere.svc.asset.controller;

import cn.healthcaredaas.datasphere.svc.asset.entity.AssetCategory;
import cn.healthcaredaas.datasphere.svc.asset.service.AssetCategoryService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资产分类控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/asset/categories")
@RequiredArgsConstructor
@Tag(name = "资产分类管理", description = "资产分类管理相关接口")
public class AssetCategoryController {

    private final AssetCategoryService categoryService;

    @Operation(summary = "分页查询资产分类列表")
    @GetMapping
    public IPage<AssetCategory> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            AssetCategory params) {
        return categoryService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取资产分类树")
    @GetMapping("/tree")
    public List<AssetCategory> getTree() {
        return categoryService.getCategoryTree();
    }

    @Operation(summary = "获取子分类")
    @GetMapping("/{parentId}/children")
    public List<AssetCategory> getChildren(@PathVariable("parentId") String parentId) {
        return categoryService.getChildren(parentId);
    }

    @Operation(summary = "获取资产分类详情")
    @GetMapping("/{id}")
    public AssetCategory getById(@PathVariable("id") String id) {
        return categoryService.getById(id);
    }

    @Operation(summary = "新增资产分类")
    @PostMapping
    public AssetCategory save(@RequestBody @Validated AssetCategory category) {
        categoryService.save(category);
        return category;
    }

    @Operation(summary = "更新资产分类")
    @PutMapping("/{id}")
    public AssetCategory update(@PathVariable("id") String id, @RequestBody @Validated AssetCategory category) {
        category.setId(id);
        categoryService.updateById(category);
        return categoryService.getById(id);
    }

    @Operation(summary = "删除资产分类")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        categoryService.removeById(id);
    }
}
