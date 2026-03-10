package cn.healthcaredaas.datasphere.svc.integration.controller;

import cn.healthcaredaas.datasphere.svc.integration.entity.ConnectorOption;
import cn.healthcaredaas.datasphere.svc.integration.service.ConnectorOptionService;
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
 * Connector配置项控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/integration/connector-options")
@RequiredArgsConstructor
@Tag(name = "SeaTunnel Connector配置项管理", description = "SeaTunnel Connector配置项管理相关接口")
public class ConnectorOptionController {

    private final ConnectorOptionService connectorOptionService;

    @Operation(summary = "分页查询Connector配置项列表")
    @GetMapping
    public IPage<ConnectorOption> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            ConnectorOption params) {
        return connectorOptionService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "根据Connector类型查询配置项")
    @GetMapping("/by-connector/{connectorTypeId}")
    public List<ConnectorOption> listByConnectorType(@PathVariable("connectorTypeId") String connectorTypeId) {
        return connectorOptionService.listByConnectorType(connectorTypeId);
    }

    @Operation(summary = "获取Connector配置项详情")
    @GetMapping("/{id}")
    public ConnectorOption getById(@PathVariable("id") String id) {
        return connectorOptionService.getById(id);
    }

    @Operation(summary = "新增Connector配置项")
    @PostMapping
    public ConnectorOption save(@RequestBody @Validated ConnectorOption connectorOption) {
        connectorOptionService.save(connectorOption);
        return connectorOption;
    }

    @Operation(summary = "更新Connector配置项")
    @PutMapping("/{id}")
    public ConnectorOption update(@PathVariable("id") String id, @RequestBody @Validated ConnectorOption connectorOption) {
        connectorOption.setId(id);
        connectorOptionService.updateById(connectorOption);
        return connectorOptionService.getById(id);
    }

    @Operation(summary = "删除Connector配置项")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        connectorOptionService.removeById(id);
    }
}
