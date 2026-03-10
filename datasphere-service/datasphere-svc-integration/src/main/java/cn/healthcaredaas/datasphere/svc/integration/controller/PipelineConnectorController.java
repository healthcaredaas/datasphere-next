package cn.healthcaredaas.datasphere.svc.integration.controller;

import cn.healthcaredaas.datasphere.api.integration.dto.PipelineConnectorDTO;
import cn.healthcaredaas.datasphere.svc.integration.entity.PipelineConnector;
import cn.healthcaredaas.datasphere.svc.integration.service.PipelineConnectorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 管道连接器控制器
 *
 * @author chenpan
 */
@RestController
@RequestMapping("/api/v1/integration/pipeline-connectors")
@RequiredArgsConstructor
@Tag(name = "管道连接器管理", description = "管道连接器配置管理")
public class PipelineConnectorController {

    private final PipelineConnectorService pipelineConnectorService;

    @Operation(summary = "根据管道ID查询连接器列表")
    @GetMapping("/by-pipeline/{pipelineId}")
    public List<PipelineConnectorDTO> listByPipeline(
            @Parameter(description = "管道ID") @PathVariable("pipelineId") String pipelineId) {
        List<PipelineConnector> list = pipelineConnectorService.lambdaQuery()
                .eq(PipelineConnector::getPipelineId, pipelineId)
                .orderByAsc(PipelineConnector::getOrderNo)
                .list();

        return list.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "获取连接器详情")
    @GetMapping("/{id}")
    public PipelineConnector getById(@PathVariable("id") String id) {
        return pipelineConnectorService.getById(id);
    }

    @Operation(summary = "新增连接器")
    @PostMapping
    public PipelineConnector save(@RequestBody @Validated PipelineConnector connector) {
        pipelineConnectorService.save(connector);
        return connector;
    }

    @Operation(summary = "更新连接器")
    @PutMapping("/{id}")
    public PipelineConnector update(@PathVariable("id") String id,
                                    @RequestBody @Validated PipelineConnector connector) {
        connector.setId(id);
        pipelineConnectorService.updateById(connector);
        return pipelineConnectorService.getById(id);
    }

    @Operation(summary = "删除连接器")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        pipelineConnectorService.removeById(id);
    }

    /**
     * 转换为DTO
     */
    private PipelineConnectorDTO convertToDTO(PipelineConnector entity) {
        PipelineConnectorDTO dto = new PipelineConnectorDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
