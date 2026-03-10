package cn.healthcaredaas.datasphere.svc.datasource.service;

import cn.healthcaredaas.datasphere.svc.datasource.entity.DatasourceType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 数据源类型服务接口
 *
 * @author chenpan
 */
public interface DatasourceTypeService extends IService<DatasourceType> {

    /**
     * 根据数据源类型获取类型信息
     *
     * @param dataType 数据源类型
     * @return 数据源类型信息
     */
    DatasourceType getByDataType(String dataType);

    /**
     * 分页查询数据源类型
     *
     * @param page   分页参数
     * @param params 查询参数
     * @return 分页结果
     */
    IPage<DatasourceType> pageQuery(IPage<DatasourceType> page, DatasourceType params);

    /**
     * 根据分类查询数据源类型
     *
     * @param classifyCode 分类编码
     * @return 类型列表
     */
    List<DatasourceType> listByClassify(String classifyCode);
}
