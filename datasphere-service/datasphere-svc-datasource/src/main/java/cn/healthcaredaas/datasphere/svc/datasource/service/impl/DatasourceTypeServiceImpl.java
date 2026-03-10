package cn.healthcaredaas.datasphere.svc.datasource.service.impl;

import cn.healthcaredaas.datasphere.svc.datasource.entity.DatasourceType;
import cn.healthcaredaas.datasphere.svc.datasource.mapper.DatasourceTypeMapper;
import cn.healthcaredaas.datasphere.svc.datasource.service.DatasourceTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据源类型服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatasourceTypeServiceImpl extends ServiceImpl<DatasourceTypeMapper, DatasourceType>
        implements DatasourceTypeService {

    @Override
    public DatasourceType getByDataType(String dataType) {
        return lambdaQuery()
                .eq(DatasourceType::getDataType, dataType)
                .one();
    }

    @Override
    public IPage<DatasourceType> pageQuery(IPage<DatasourceType> page, DatasourceType params) {
        LambdaQueryWrapper<DatasourceType> wrapper = new LambdaQueryWrapper<>();

        // 类型编码模糊查询
        if (StringUtils.isNotBlank(params.getDataType())) {
            wrapper.like(DatasourceType::getDataType, params.getDataType());
        }

        // 类型名称模糊查询
        if (StringUtils.isNotBlank(params.getDataTypeName())) {
            wrapper.like(DatasourceType::getDataTypeName, params.getDataTypeName());
        }

        // 分类编码精确查询
        if (StringUtils.isNotBlank(params.getClassifyCode())) {
            wrapper.eq(DatasourceType::getClassifyCode, params.getClassifyCode());
        }

        // 按序号升序
        wrapper.orderByAsc(DatasourceType::getOrderNo);

        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<DatasourceType> listByClassify(String classifyCode) {
        return lambdaQuery()
                .eq(DatasourceType::getClassifyCode, classifyCode)
                .orderByAsc(DatasourceType::getOrderNo)
                .list();
    }
}
