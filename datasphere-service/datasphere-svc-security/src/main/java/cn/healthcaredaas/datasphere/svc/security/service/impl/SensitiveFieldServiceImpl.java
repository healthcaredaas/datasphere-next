package cn.healthcaredaas.datasphere.svc.security.service.impl;

import cn.healthcaredaas.datasphere.svc.security.entity.SensitiveField;
import cn.healthcaredaas.datasphere.svc.security.mapper.SensitiveFieldMapper;
import cn.healthcaredaas.datasphere.svc.security.service.SensitiveFieldService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 敏感字段识别服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SensitiveFieldServiceImpl extends ServiceImpl<SensitiveFieldMapper, SensitiveField>
        implements SensitiveFieldService {

    // 敏感字段名称模式
    private static final List<Pattern> SENSITIVE_PATTERNS = Arrays.asList(
            Pattern.compile(".*(phone|mobile|tel).*", Pattern.CASE_INSENSITIVE),      // 手机号
            Pattern.compile(".*(email|mail).*", Pattern.CASE_INSENSITIVE),           // 邮箱
            Pattern.compile(".*(id_card|idcard|identity).*", Pattern.CASE_INSENSITIVE), // 身份证
            Pattern.compile(".*(name|username).*", Pattern.CASE_INSENSITIVE),        // 姓名
            Pattern.compile(".*(address|addr).*", Pattern.CASE_INSENSITIVE),         // 地址
            Pattern.compile(".*(bank|card_no|cardnum).*", Pattern.CASE_INSENSITIVE), // 银行卡
            Pattern.compile(".*(password|pwd|passwd).*", Pattern.CASE_INSENSITIVE),  // 密码
            Pattern.compile(".*(salary|income).*", Pattern.CASE_INSENSITIVE),        // 薪资
            Pattern.compile(".*(medical|health).*", Pattern.CASE_INSENSITIVE)        // 医疗信息
    );

    // 敏感类型映射
    private static final List<String> SENSITIVE_TYPES = Arrays.asList(
            "PHONE", "EMAIL", "ID_CARD", "NAME", "ADDRESS", "BANK_CARD", "PASSWORD", "SALARY", "MEDICAL"
    );

    @Override
    public IPage<SensitiveField> pageQuery(IPage<SensitiveField> page, SensitiveField params) {
        LambdaQueryWrapper<SensitiveField> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getDatasourceId())) {
            wrapper.eq(SensitiveField::getDatasourceId, params.getDatasourceId());
        }

        if (StringUtils.isNotBlank(params.getTableName())) {
            wrapper.like(SensitiveField::getTableName, params.getTableName());
        }

        if (StringUtils.isNotBlank(params.getSensitiveType())) {
            wrapper.eq(SensitiveField::getSensitiveType, params.getSensitiveType());
        }

        if (params.getIsConfirmed() != null) {
            wrapper.eq(SensitiveField::getIsConfirmed, params.getIsConfirmed());
        }

        wrapper.orderByDesc(SensitiveField::getCreateTime);

        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<SensitiveField> autoDetectSensitiveFields(String datasourceId, String tableName) {
        List<SensitiveField> detectedFields = new ArrayList<>();

        // 模拟字段列表 - 实际应从元数据服务获取
        List<String> fieldNames = Arrays.asList("user_name", "phone", "email", "address", "id_card", "create_time");

        for (String fieldName : fieldNames) {
            for (int i = 0; i < SENSITIVE_PATTERNS.size(); i++) {
                if (SENSITIVE_PATTERNS.get(i).matcher(fieldName).matches()) {
                    SensitiveField field = new SensitiveField();
                    field.setDatasourceId(datasourceId);
                    field.setTableName(tableName);
                    field.setColumnName(fieldName);
                    field.setSensitiveType(SENSITIVE_TYPES.get(i));
                    field.setSensitivityLevel(3); // 高敏感度
                    field.setIsConfirmed(0);
                    field.setDetectMethod("AUTO");

                    detectedFields.add(field);
                    break;
                }
            }
        }

        return detectedFields;
    }

    @Override
    public void markAsSensitive(String fieldId, String sensitiveType) {
        SensitiveField field = getById(fieldId);
        if (field != null) {
            field.setSensitiveType(sensitiveType);
            field.setIsConfirmed(1);
            updateById(field);
        }
    }

    @Override
    public List<SensitiveField> getSensitiveFieldsByDatasource(String datasourceId) {
        LambdaQueryWrapper<SensitiveField> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SensitiveField::getDatasourceId, datasourceId);
        wrapper.eq(SensitiveField::getIsConfirmed, 1);
        return baseMapper.selectList(wrapper);
    }
}
