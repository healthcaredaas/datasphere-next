package cn.healthcaredaas.datasphere.svc.master.controller;

import cn.healthcaredaas.datasphere.svc.master.entity.Person;
import cn.healthcaredaas.datasphere.svc.master.service.PersonService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 人员信息控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/master/persons")
@RequiredArgsConstructor
@Tag(name = "人员管理", description = "人员管理相关接口")
public class PersonController {

    private final PersonService personService;

    @Operation(summary = "分页查询人员列表")
    @GetMapping
    public IPage<Person> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size,
            Person params) {
        return personService.pageQuery(new Page<>(current, size), params);
    }

    @Operation(summary = "获取人员详情")
    @GetMapping("/{id}")
    public Person getById(@PathVariable("id") String id) {
        return personService.getById(id);
    }

    @Operation(summary = "新增人员")
    @PostMapping
    public Person save(@RequestBody @Validated Person person) {
        personService.save(person);
        return person;
    }

    @Operation(summary = "更新人员")
    @PutMapping("/{id}")
    public Person update(@PathVariable("id") String id, @RequestBody @Validated Person person) {
        person.setId(id);
        personService.updateById(person);
        return personService.getById(id);
    }

    @Operation(summary = "删除人员")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        personService.removeById(id);
    }
}
