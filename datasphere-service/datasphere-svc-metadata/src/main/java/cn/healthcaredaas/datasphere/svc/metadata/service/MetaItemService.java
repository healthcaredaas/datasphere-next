package cn.healthcaredaas.datasphere.svc.metadata.service;

import cn.healthcaredaas.datasphere.svc.metadata.entity.MetaItem;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 元数据项服务接口
 *
 * @author chenpan
 */
public interface MetaItemService extends IService<MetaItem> {

    IPage<MetaItem> pageQuery(IPage<MetaItem> page, MetaItem params);

    List<MetaItem> listByDatasource(String datasourceId);
}
