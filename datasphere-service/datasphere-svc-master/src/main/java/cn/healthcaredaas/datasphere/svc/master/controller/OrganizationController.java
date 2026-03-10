package cn.healthcaredaas.datasphere.svc.master.controller;

import cn.healthcaredaas.datasphere.svc.master.entity.Organization;
import cn.healthcaredaas.datasphere.svc.master.service.OrganizationService;
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
 * 组织机构控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/master/organizations")
@RequiredArgsConstructor
@Tag(name = "组织机构管理", description = "组织机构管理相关接口")
public class OrganizationController {

    private final OrganizationService organizationService;

    @Operation(summary = "分页查询组织机构列表")
    @GetMapping
    public IPage<Organization> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            Organization params) {
        return organizationService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取组织机构树")
    @GetMapping("/tree")
    public List<Organization> listTree() {
        return organizationService.listTree();
    }

    @Operation(summary = "获取组织机构详情")
    @GetMapping("/{id}")
    public Organization getById(@PathVariable("id") String id) {
        return organizationService.getById(id);
    }

    @Operation(summary = "新增组织机构")
    @PostMapping
    public Organization save(@RequestBody @Validated Organization organization) {
        organizationService.save(organization);
        return organization;
    }

    @Operation(summary = "更新组织机构")
    @PutMapping("/{id}")
    public Organization update(@PathVariable("id") String id, @RequestBody @Validated Organization organization) {
        organization.setId(id);
        organizationService.updateById(organization);
        return organizationService.getById(id);
    }

    @Operation(summary = "删除组织机构")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        organizationService.removeById(id);
    }
}
