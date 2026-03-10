package cn.healthcaredaas.datasphere.svc.datasource.service.impl;

import cn.healthcaredaas.datasphere.api.datasource.DatasourceInfoApi;
import cn.healthcaredaas.datasphere.api.datasource.dto.DatasourceInfoDTO;
import cn.healthcaredaas.datasphere.svc.datasource.entity.DatasourceInfo;
import cn.healthcaredaas.datasphere.svc.datasource.service.DatasourceInfoService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据源服务 Dubbo 实现
 *
 * @author chenpan
 */
@DubboService
@RequiredArgsConstructor
public class DatasourceInfoApiImpl implements DatasourceInfoApi {

    private final DatasourceInfoService datasourceInfoService;

    @Override
    public DatasourceInfoDTO getById(String id) {
        DatasourceInfo entity = datasourceInfoService.getById(id);
        if (entity == null) {
            return null;
        }
        return convertToDTO(entity);
    }

    @Override
    public List<DatasourceInfoDTO> listAll() {
        return datasourceInfoService.list().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean testConnection(DatasourceInfoDTO datasourceInfoDTO) {
        try {
            DatasourceInfo entity = new DatasourceInfo();
            BeanUtils.copyProperties(datasourceInfoDTO, entity);
            datasourceInfoService.testJdbcConnection(entity);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private DatasourceInfoDTO convertToDTO(DatasourceInfo entity) {
        DatasourceInfoDTO dto = new DatasourceInfoDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getDsConfig() != null) {
            dto.setDsConfigJson(entity.getDsConfig().toJSONString());
        }
        return dto;
    }
}
