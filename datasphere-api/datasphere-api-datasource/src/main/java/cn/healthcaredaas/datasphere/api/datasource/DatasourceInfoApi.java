package cn.healthcaredaas.datasphere.api.datasource;

import cn.healthcaredaas.datasphere.api.datasource.dto.DatasourceInfoDTO;

import java.util.List;

/**
 * 数据源服务 Dubbo 接口
 *
 * @author chenpan
 */
public interface DatasourceInfoApi {

    /**
     * 根据ID获取数据源
     *
     * @param id 数据源ID
     * @return 数据源信息
     */
    DatasourceInfoDTO getById(String id);

    /**
     * 获取所有数据源列表
     *
     * @return 数据源列表
     */
    List<DatasourceInfoDTO> listAll();

    /**
     * 测试数据源连接
     *
     * @param datasourceInfoDTO 数据源信息
     * @return 是否连接成功
     */
    boolean testConnection(DatasourceInfoDTO datasourceInfoDTO);
}
