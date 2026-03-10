package cn.healthcaredaas.datasphere.svc.security.controller;

import cn.healthcaredaas.datasphere.svc.security.entity.SensitiveField;
import cn.healthcaredaas.datasphere.svc.security.service.SensitiveFieldService;
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
 * 敏感字段识别控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/security/sensitive-fields")
@RequiredArgsConstructor
@Tag(name = "敏感字段识别管理", description = "敏感字段识别管理相关接口")
public class SensitiveFieldController {

    private final SensitiveFieldService sensitiveFieldService;

    @Operation(summary = "分页查询敏感字段列表")
    @GetMapping
    public IPage<SensitiveField> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            SensitiveField params) {
        return sensitiveFieldService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "自动识别敏感字段")
    @PostMapping("/detect")
    public List<SensitiveField> autoDetect(
            @Parameter(description = "数据源ID") @RequestParam String datasourceId,
            @Parameter(description = "表名") @RequestParam String tableName) {
        return sensitiveFieldService.autoDetectSensitiveFields(datasourceId, tableName);
    }

    @Operation(summary = "标记字段为敏感")
    @PostMapping("/{fieldId}/mark")
    public void markAsSensitive(
            @PathVariable("fieldId") String fieldId,
            @Parameter(description = "敏感类型") @RequestParam String sensitiveType) {
        sensitiveFieldService.markAsSensitive(fieldId, sensitiveType);
    }

    @Operation(summary = "获取数据源的敏感字段")
    @GetMapping("/datasource/{datasourceId}")
    public List<SensitiveField> getByDatasource(@PathVariable("datasourceId") String datasourceId) {
        return sensitiveFieldService.getSensitiveFieldsByDatasource(datasourceId);
    }

    @Operation(summary = "获取敏感字段详情")
    @GetMapping("/{id}")
    public SensitiveField getById(@PathVariable("id") String id) {
        return sensitiveFieldService.getById(id);
    }

    @Operation(summary = "新增敏感字段")
    @PostMapping
    public SensitiveField save(@RequestBody @Validated SensitiveField field) {
        sensitiveFieldService.save(field);
        return field;
    }

    @Operation(summary = "更新敏感字段")
    @PutMapping("/{id}")
    public SensitiveField update(@PathVariable("id") String id, @RequestBody @Validated SensitiveField field) {
        field.setId(id);
        sensitiveFieldService.updateById(field);
        return sensitiveFieldService.getById(id);
    }

    @Operation(summary = "删除敏感字段")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        sensitiveFieldService.removeById(id);
    }
}
