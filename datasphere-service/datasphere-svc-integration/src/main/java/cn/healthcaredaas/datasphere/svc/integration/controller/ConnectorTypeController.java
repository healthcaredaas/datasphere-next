package cn.healthcaredaas.datasphere.svc.integration.controller;

import cn.healthcaredaas.datasphere.svc.integration.entity.ConnectorType;
import cn.healthcaredaas.datasphere.svc.integration.service.ConnectorTypeService;
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
 * Connector类型控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/integration/connector-types")
@RequiredArgsConstructor
@Tag(name = "SeaTunnel Connector类型管理", description = "SeaTunnel Connector类型管理相关接口")
public class ConnectorTypeController {

    private final ConnectorTypeService connectorTypeService;

    @Operation(summary = "分页查询Connector类型列表")
    @GetMapping
    public IPage<ConnectorType> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            ConnectorType params) {
        return connectorTypeService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "根据类型查询Connector列表")
    @GetMapping("/by-type/{connectorType}")
    public List<ConnectorType> listByType(@PathVariable("connectorType") String connectorType) {
        return connectorTypeService.listByType(connectorType);
    }

    @Operation(summary = "获取Connector类型详情")
    @GetMapping("/{id}")
    public ConnectorType getById(@PathVariable("id") String id) {
        return connectorTypeService.getById(id);
    }

    @Operation(summary = "新增Connector类型")
    @PostMapping
    public ConnectorType save(@RequestBody @Validated ConnectorType connectorType) {
        connectorTypeService.save(connectorType);
        return connectorType;
    }

    @Operation(summary = "更新Connector类型")
    @PutMapping("/{id}")
    public ConnectorType update(@PathVariable("id") String id, @RequestBody @Validated ConnectorType connectorType) {
        connectorType.setId(id);
        connectorTypeService.updateById(connectorType);
        return connectorTypeService.getById(id);
    }

    @Operation(summary = "删除Connector类型")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        connectorTypeService.removeById(id);
    }
}
