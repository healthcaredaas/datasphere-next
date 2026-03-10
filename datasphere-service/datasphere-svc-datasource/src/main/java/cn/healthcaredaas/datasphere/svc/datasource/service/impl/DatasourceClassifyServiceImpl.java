package cn.healthcaredaas.datasphere.svc.datasource.service.impl;

import cn.healthcaredaas.datasphere.svc.datasource.entity.DatasourceClassify;
import cn.healthcaredaas.datasphere.svc.datasource.mapper.DatasourceClassifyMapper;
import cn.healthcaredaas.datasphere.svc.datasource.service.DatasourceClassifyService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据源分类服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatasourceClassifyServiceImpl extends ServiceImpl<DatasourceClassifyMapper, DatasourceClassify>
        implements DatasourceClassifyService {

    @Override
    public IPage<DatasourceClassify> pageQuery(IPage<DatasourceClassify> page, DatasourceClassify params) {
        LambdaQueryWrapper<DatasourceClassify> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getClassifyCode())) {
            wrapper.like(DatasourceClassify::getClassifyCode, params.getClassifyCode());
        }

        if (StringUtils.isNotBlank(params.getClassifyName())) {
            wrapper.like(DatasourceClassify::getClassifyName, params.getClassifyName());
        }

        wrapper.orderByAsc(DatasourceClassify::getOrderNo);

        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public DatasourceClassify getByClassifyCode(String classifyCode) {
        return lambdaQuery()
                .eq(DatasourceClassify::getClassifyCode, classifyCode)
                .one();
    }

    @Override
    public List<DatasourceClassify> listAllOrderByNo() {
        return lambdaQuery()
                .orderByAsc(DatasourceClassify::getOrderNo)
                .list();
    }
}
