package cn.healthcaredaas.datasphere.svc.agent.rpc.impl;

import cn.healthcaredaas.datasphere.svc.agent.rpc.MasterDataRpcService;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 主数据服务本地实现
 *
 * @author chenpan
 */
@Slf4j
@Component
public class MasterDataRpcServiceImpl implements MasterDataRpcService {

    @Override
    public List<JSONObject> listCodeSystems() {
        log.info("RPC调用: listCodeSystems");
        return List.of(
                createCodeSystem("cs_001", "性别代码", "GB/T 2261.1", "性别代码标准"),
                createCodeSystem("cs_002", "婚姻状况代码", "GB/T 2261.2", "婚姻状况代码标准"),
                createCodeSystem("cs_003", "民族代码", "GB/T 3304", "民族代码标准"),
                createCodeSystem("cs_004", "ICD-10", "WHO", "疾病诊断编码")
        );
    }

    @Override
    public List<JSONObject> getCodeSystemConcepts(String codeSystemId) {
        log.info("RPC调用: getCodeSystemConcepts, codeSystemId={}", codeSystemId);

        // 返回示例概念
        if ("cs_001".equals(codeSystemId)) {
            return List.of(
                    createConcept("1", "男性", "male"),
                    createConcept("2", "女性", "female"),
                    createConcept("9", "未说明", "unknown")
            );
        } else if ("cs_004".equals(codeSystemId)) {
            return List.of(
                    createConcept("A00", "霍乱", "Cholera"),
                    createConcept("A01", "伤寒和副伤寒", "Typhoid and paratyphoid fevers")
            );
        }

        return List.of();
    }

    @Override
    public JSONObject getConceptByCode(String codeSystemId, String code) {
        log.info("RPC调用: getConceptByCode, codeSystemId={}, code={}", codeSystemId, code);

        JSONObject concept = new JSONObject();
        concept.put("codeSystemId", codeSystemId);
        concept.put("code", code);
        concept.put("display", "概念显示名");
        concept.put("description", "概念描述");
        return concept;
    }

    @Override
    public List<JSONObject> listOrganizations() {
        log.info("RPC调用: listOrganizations");
        return List.of(
                createOrganization("org_001", "XX医院", "三级甲等"),
                createOrganization("org_002", "XX社区医院", "一级医院")
        );
    }

    @Override
    public List<JSONObject> listDepartments(String orgId) {
        log.info("RPC调用: listDepartments, orgId={}", orgId);
        return List.of(
                createDepartment("dept_001", "内科", orgId),
                createDepartment("dept_002", "外科", orgId),
                createDepartment("dept_003", "妇产科", orgId)
        );
    }

    @Override
    public List<JSONObject> listPractitioners(String deptId) {
        log.info("RPC调用: listPractitioners, deptId={}", deptId);
        return List.of(
                createPractitioner("pract_001", "张医生", "主任医师", deptId),
                createPractitioner("pract_002", "李医生", "副主任医师", deptId)
        );
    }

    @Override
    public JSONObject getMapping(String sourceSystem, String targetType, String sourceCode) {
        log.info("RPC调用: getMapping, sourceSystem={}, targetType={}, sourceCode={}",
                sourceSystem, targetType, sourceCode);

        JSONObject mapping = new JSONObject();
        mapping.put("sourceSystem", sourceSystem);
        mapping.put("targetType", targetType);
        mapping.put("sourceCode", sourceCode);
        mapping.put("targetCode", "standard_" + sourceCode);
        mapping.put("targetDisplay", "标准显示名");
        return mapping;
    }

    @Override
    public void saveFieldMapping(String datasourceId, List<JSONObject> mappings) {
        log.info("RPC调用: saveFieldMapping, datasourceId={}, mappingCount={}", datasourceId, mappings.size());
        // TODO: 调用主数据服务保存映射配置
    }

    @Override
    public List<JSONObject> getStandardFields(String standard, String domain) {
        log.info("RPC调用: getStandardFields, standard={}, domain={}", standard, domain);

        // 返回标准字段定义
        if ("FHIR".equals(standard) && "PATIENT".equals(domain)) {
            return List.of(
                    createStandardField("Patient.id", "string", "患者唯一标识"),
                    createStandardField("Patient.identifier", "Identifier", "患者标识符"),
                    createStandardField("Patient.name", "HumanName", "患者姓名"),
                    createStandardField("Patient.gender", "code", "性别"),
                    createStandardField("Patient.birthDate", "date", "出生日期"),
                    createStandardField("Patient.telecom", "ContactPoint", "联系方式"),
                    createStandardField("Patient.address", "Address", "地址")
            );
        }

        return List.of();
    }

    // ========== 辅助方法 ==========

    private JSONObject createCodeSystem(String id, String name, String oid, String description) {
        JSONObject cs = new JSONObject();
        cs.put("id", id);
        cs.put("name", name);
        cs.put("oid", oid);
        cs.put("description", description);
        return cs;
    }

    private JSONObject createConcept(String code, String display, String definition) {
        JSONObject concept = new JSONObject();
        concept.put("code", code);
        concept.put("display", display);
        concept.put("definition", definition);
        return concept;
    }

    private JSONObject createOrganization(String id, String name, String level) {
        JSONObject org = new JSONObject();
        org.put("id", id);
        org.put("name", name);
        org.put("level", level);
        return org;
    }

    private JSONObject createDepartment(String id, String name, String orgId) {
        JSONObject dept = new JSONObject();
        dept.put("id", id);
        dept.put("name", name);
        dept.put("orgId", orgId);
        return dept;
    }

    private JSONObject createPractitioner(String id, String name, String title, String deptId) {
        JSONObject pract = new JSONObject();
        pract.put("id", id);
        pract.put("name", name);
        pract.put("title", title);
        pract.put("deptId", deptId);
        return pract;
    }

    private JSONObject createStandardField(String path, String type, String description) {
        JSONObject field = new JSONObject();
        field.put("path", path);
        field.put("type", type);
        field.put("description", description);
        return field;
    }
}