package cn.healthcaredaas.datasphere.svc.metadata.service.impl;

import cn.healthcaredaas.datasphere.svc.metadata.entity.MetaItem;
import cn.healthcaredaas.datasphere.svc.metadata.mapper.MetaItemMapper;
import cn.healthcaredaas.datasphere.svc.metadata.service.MetaItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 元数据项服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetaItemServiceImpl extends ServiceImpl<MetaItemMapper, MetaItem>
        implements MetaItemService {

    @Override
    public IPage<MetaItem> pageQuery(IPage<MetaItem> page, MetaItem params) {
        LambdaQueryWrapper<MetaItem> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getItemName())) {
            wrapper.like(MetaItem::getItemName, params.getItemName());
        }

        if (StringUtils.isNotBlank(params.getDatasourceId())) {
            wrapper.eq(MetaItem::getDatasourceId, params.getDatasourceId());
        }

        wrapper.orderByDesc(MetaItem::getCreateTime);

        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<MetaItem> listByDatasource(String datasourceId) {
        return lambdaQuery()
                .eq(MetaItem::getDatasourceId, datasourceId)
                .list();
    }
}
