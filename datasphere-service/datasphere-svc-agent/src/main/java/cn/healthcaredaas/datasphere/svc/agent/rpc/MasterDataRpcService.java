package cn.healthcaredaas.datasphere.svc.agent.rpc;

import com.alibaba.fastjson2.JSONObject;

import java.util.List;

/**
 * 主数据服务接口（RPC调用）
 *
 * @author chenpan
 */
public interface MasterDataRpcService {

    /**
     * 获取代码系统列表
     */
    List<JSONObject> listCodeSystems();

    /**
     * 获取代码系统概念
     */
    List<JSONObject> getCodeSystemConcepts(String codeSystemId);

    /**
     * 根据编码获取概念
     */
    JSONObject getConceptByCode(String codeSystemId, String code);

    /**
     * 获取组织机构
     */
    List<JSONObject> listOrganizations();

    /**
     * 获取科室列表
     */
    List<JSONObject> listDepartments(String orgId);

    /**
     * 获取人员列表
     */
    List<JSONObject> listPractitioners(String deptId);

    /**
     * 查询主数据映射
     */
    JSONObject getMapping(String sourceSystem, String targetType, String sourceCode);

    /**
     * 保存字段映射配置
     */
    void saveFieldMapping(String datasourceId, List<JSONObject> mappings);

    /**
     * 获取标准字段定义
     */
    List<JSONObject> getStandardFields(String standard, String domain);
}