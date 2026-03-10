package cn.healthcaredaas.datasphere.svc.master.controller;

import cn.healthcaredaas.datasphere.svc.master.entity.Department;
import cn.healthcaredaas.datasphere.svc.master.service.DepartmentService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 科室控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/master/departments")
@RequiredArgsConstructor
@Tag(name = "科室管理", description = "科室管理相关接口")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(summary = "分页查询科室列表")
    @GetMapping
    public IPage<Department> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            Department params) {
        return departmentService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取科室详情")
    @GetMapping("/{id}")
    public Department getById(@PathVariable("id") String id) {
        return departmentService.getById(id);
    }

    @Operation(summary = "新增科室")
    @PostMapping
    public Department save(@RequestBody @Validated Department department) {
        departmentService.save(department);
        return department;
    }

    @Operation(summary = "更新科室")
    @PutMapping("/{id}")
    public Department update(@PathVariable("id") String id, @RequestBody @Validated Department department) {
        department.setId(id);
        departmentService.updateById(department);
        return departmentService.getById(id);
    }

    @Operation(summary = "删除科室")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        departmentService.removeById(id);
    }
}
