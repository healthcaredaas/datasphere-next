package cn.healthcaredaas.datasphere.svc.security.service;

import cn.healthcaredaas.datasphere.svc.security.entity.SensitiveField;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 敏感字段识别服务接口
 *
 * @author chenpan
 */
public interface SensitiveFieldService extends IService<SensitiveField> {

    IPage<SensitiveField> pageQuery(IPage<SensitiveField> page, SensitiveField params);

    /**
     * 自动识别敏感字段
     */
    List<SensitiveField> autoDetectSensitiveFields(String datasourceId, String tableName);

    /**
     * 标记字段为敏感
     */
    void markAsSensitive(String fieldId, String sensitiveType);

    /**
     * 获取数据源的敏感字段
     */
    List<SensitiveField> getSensitiveFieldsByDatasource(String datasourceId);
}
