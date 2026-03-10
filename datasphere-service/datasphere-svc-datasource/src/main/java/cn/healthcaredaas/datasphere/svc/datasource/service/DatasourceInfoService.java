package cn.healthcaredaas.datasphere.svc.datasource.service;

import cn.healthcaredaas.datasphere.core.dto.ColumnMeta;
import cn.healthcaredaas.datasphere.svc.datasource.entity.DatasourceInfo;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.sql.Connection;
import java.util.List;

/**
 * 数据源信息服务接口
 *
 * @author chenpan
 */
public interface DatasourceInfoService extends IService<DatasourceInfo> {

    /**
     * 分页查询数据源列表
     *
     * @param page   分页参数
     * @param params 查询参数
     * @return 分页结果
     */
    IPage<DatasourceInfo> pageQuery(IPage<DatasourceInfo> page, DatasourceInfo params);

    /**
     * 获取数据源连接
     *
     * @param id 数据源ID
     * @return 数据库连接
     */
    Connection getConnection(String id);

    /**
     * 测试JDBC数据库连接
     *
     * @param info 数据源信息
     */
    void testJdbcConnection(DatasourceInfo info);

    /**
     * 获取数据源所有表和视图
     *
     * @param dsId 数据源ID
     * @return 表名列表
     */
    List<String> getTables(String dsId);

    /**
     * 获取表的所有字段
     *
     * @param dsId      数据源ID
     * @param tableName 表名
     * @return 字段元数据列表
     */
    List<ColumnMeta> getColumns(String dsId, String tableName);

    /**
     * 获取SQL查询的列信息
     *
     * @param dsId 数据源ID
     * @param sql  SQL语句
     * @return 字段元数据列表
     */
    List<ColumnMeta> getSqlColumns(String dsId, String sql);

    /**
     * 获取数据源配置
     *
     * @param id 数据源ID
     * @return 数据源配置JSON
     */
    JSONObject getDsConfig(String id);
}
