package cn.healthcaredaas.datasphere.svc.asset.controller;

import cn.healthcaredaas.datasphere.svc.asset.entity.DataAsset;
import cn.healthcaredaas.datasphere.svc.asset.service.DataAssetService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 数据资产控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/asset/assets")
@RequiredArgsConstructor
@Tag(name = "数据资产管理", description = "数据资产管理相关接口")
public class DataAssetController {

    private final DataAssetService dataAssetService;

    @Operation(summary = "分页查询数据资产列表")
    @GetMapping
    public IPage<DataAsset> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            DataAsset params) {
        return dataAssetService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取数据资产详情")
    @GetMapping("/{id}")
    public DataAsset getById(@PathVariable("id") String id) {
        return dataAssetService.getById(id);
    }

    @Operation(summary = "新增数据资产")
    @PostMapping
    public DataAsset save(@RequestBody @Validated DataAsset dataAsset) {
        dataAssetService.save(dataAsset);
        return dataAsset;
    }

    @Operation(summary = "更新数据资产")
    @PutMapping("/{id}")
    public DataAsset update(@PathVariable("id") String id, @RequestBody @Validated DataAsset dataAsset) {
        dataAsset.setId(id);
        dataAssetService.updateById(dataAsset);
        return dataAssetService.getById(id);
    }

    @Operation(summary = "删除数据资产")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        dataAssetService.removeById(id);
    }
}
