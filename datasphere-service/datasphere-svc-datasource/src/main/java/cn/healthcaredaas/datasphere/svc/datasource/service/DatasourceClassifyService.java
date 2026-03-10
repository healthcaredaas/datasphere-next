package cn.healthcaredaas.datasphere.svc.datasource.service;

import cn.healthcaredaas.datasphere.svc.datasource.entity.DatasourceClassify;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 数据源分类服务接口
 *
 * @author chenpan
 */
public interface DatasourceClassifyService extends IService<DatasourceClassify> {

    /**
     * 分页查询数据源分类
     *
     * @param page   分页参数
     * @param params 查询参数
     * @return 分页结果
     */
    IPage<DatasourceClassify> pageQuery(IPage<DatasourceClassify> page, DatasourceClassify params);

    /**
     * 根据分类编码获取分类
     *
     * @param classifyCode 分类编码
     * @return 分类信息
     */
    DatasourceClassify getByClassifyCode(String classifyCode);

    /**
     * 查询所有分类（按序号排序）
     *
     * @return 分类列表
     */
    List<DatasourceClassify> listAllOrderByNo();
}
