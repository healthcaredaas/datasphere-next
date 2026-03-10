package cn.healthcaredaas.datasphere.svc.datasource.service.impl;

import cn.healthcaredaas.datasphere.core.dto.ColumnMeta;
import cn.healthcaredaas.datasphere.core.util.RdbmsMetaUtils;
import cn.healthcaredaas.datasphere.core.util.RdbmsUtils;
import cn.healthcaredaas.datasphere.svc.datasource.entity.DatasourceInfo;
import cn.healthcaredaas.datasphere.svc.datasource.entity.DatasourceType;
import cn.healthcaredaas.datasphere.svc.datasource.mapper.DatasourceInfoMapper;
import cn.healthcaredaas.datasphere.svc.datasource.service.DatasourceInfoService;
import cn.healthcaredaas.datasphere.svc.datasource.service.DatasourceTypeService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.List;

/**
 * 数据源信息服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatasourceInfoServiceImpl extends ServiceImpl<DatasourceInfoMapper, DatasourceInfo>
        implements DatasourceInfoService {

    private final DatasourceTypeService datasourceTypeService;

    @Override
    public IPage<DatasourceInfo> pageQuery(IPage<DatasourceInfo> page, DatasourceInfo params) {
        LambdaQueryWrapper<DatasourceInfo> wrapper = new LambdaQueryWrapper<>();

        // 数据源名称模糊查询
        if (StringUtils.isNotBlank(params.getDsName())) {
            wrapper.like(DatasourceInfo::getDsName, params.getDsName());
        }

        // 数据源类型精确查询
        if (StringUtils.isNotBlank(params.getDsType())) {
            wrapper.eq(DatasourceInfo::getDsType, params.getDsType());
        }

        // 环境配置查询
        if (StringUtils.isNotBlank(params.getEnvProfile())) {
            wrapper.eq(DatasourceInfo::getEnvProfile, params.getEnvProfile());
        }

        // 按创建时间降序
        wrapper.orderByDesc(DatasourceInfo::getCreateTime);

        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public Connection getConnection(String id) {
        DatasourceInfo info = getById(id);
        return getConnection(info);
    }

    private Connection getConnection(DatasourceInfo info) {
        if (info == null) {
            throw new RuntimeException("数据源不存在");
        }
        DatasourceType type = datasourceTypeService.getByDataType(info.getDsType());
        if (type == null) {
            throw new RuntimeException("不支持的数据库类型: " + info.getDsType());
        }
        JSONObject dsConfig = info.getDsConfig();
        return RdbmsUtils.getConnection(type.getDriver(), dsConfig);
    }

    @Override
    public void testJdbcConnection(DatasourceInfo info) {
        Connection conn = getConnection(info);
        RdbmsUtils.closeDBResources(conn);
    }

    @Override
    public List<String> getTables(String dsId) {
        Connection conn = getConnection(dsId);
        return RdbmsMetaUtils.getTables(conn);
    }

    @Override
    public List<ColumnMeta> getColumns(String dsId, String tableName) {
        Connection conn = getConnection(dsId);
        return RdbmsMetaUtils.getColumnMetaData(conn, tableName, "*");
    }

    @Override
    public List<ColumnMeta> getSqlColumns(String dsId, String sql) {
        Connection conn = getConnection(dsId);
        return RdbmsMetaUtils.getColumnMetaData(conn, sql);
    }

    @Override
    public JSONObject getDsConfig(String id) {
        DatasourceInfo info = getById(id);
        return info != null ? info.getDsConfig() : null;
    }
}
