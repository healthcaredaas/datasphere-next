package cn.healthcaredaas.datasphere.hie.gateway.controller;

import cn.healthcaredaas.datasphere.hie.gateway.entity.HieService;
import cn.healthcaredaas.datasphere.hie.gateway.service.HieServiceService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 交互服务控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/hie/services")
@RequiredArgsConstructor
@Tag(name = "HIE交互服务管理", description = "HIE交互服务管理相关接口")
public class HieServiceController {

    private final HieServiceService hieServiceService;

    @Operation(summary = "分页查询交互服务列表")
    @GetMapping
    public IPage<HieService> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            HieService params) {
        return hieServiceService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取交互服务详情")
    @GetMapping("/{id}")
    public HieService getById(@PathVariable("id") String id) {
        return hieServiceService.getById(id);
    }

    @Operation(summary = "新增交互服务")
    @PostMapping
    public HieService save(@RequestBody @Validated HieService hieService) {
        hieServiceService.save(hieService);
        return hieService;
    }

    @Operation(summary = "更新交互服务")
    @PutMapping("/{id}")
    public HieService update(@PathVariable("id") String id, @RequestBody @Validated HieService hieService) {
        hieService.setId(id);
        hieServiceService.updateById(hieService);
        return hieServiceService.getById(id);
    }

    @Operation(summary = "删除交互服务")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        hieServiceService.removeById(id);
    }
}
