package cn.healthcaredaas.datasphere.svc.agent.tools.executor;

import cn.healthcaredaas.datasphere.svc.agent.entity.ModelConfig;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapter;
import cn.healthcaredaas.datasphere.svc.agent.llm.LlmAdapterFactory;
import cn.healthcaredaas.datasphere.svc.agent.rpc.MasterDataRpcService;
import cn.healthcaredaas.datasphere.svc.agent.service.ModelConfigService;
import cn.healthcaredaas.datasphere.svc.agent.tools.Tool;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolContext;
import cn.healthcaredaas.datasphere.svc.agent.tools.ToolResult;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 标准映射生成工具
 * 生成数据标准映射配置，将源字段映射到标准字段
 *
 * @author chenpan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StandardMappingTool implements Tool {

    private final MasterDataRpcService masterDataRpcService;
    private final LlmAdapterFactory llmAdapterFactory;
    private final ModelConfigService modelConfigService;

    /**
     * 标准映射系统提示词
     */
    private static final String MAPPING_SYSTEM_PROMPT = """
            你是一位医疗数据标准化专家，负责将医院的源数据字段映射到标准字段。

            ## 支持的标准
            - HL7 FHIR: 国际医疗数据交换标准
            - HL7 V2/V3: 传统医疗数据交换标准
            - GBDC: 国家健康医疗大数据标准
            - WS/T: 卫生行业标准
            - CUSTOM: 自定义标准

            ## 映射规则
            1. 精确匹配: 字段名完全匹配标准字段
            2. 语义匹配: 字段含义相同但名称不同
            3. 转换匹配: 需要数据转换后才能匹配

            ## 输出格式
            请以JSON数组格式输出映射配置：
            ```json
            [
              {
                "sourceField": "源字段名",
                "sourceType": "源字段类型",
                "targetField": "目标标准字段路径",
                "targetType": "目标字段类型",
                "transformType": "DIRECT/MAP/CONVERT/MASK/COMPUTE",
                "transformConfig": {
                  "mapTable": "映射表名(如果是MAP类型)",
                  "format": "格式化规则(如果是CONVERT类型)",
                  "algorithm": "脱敏算法(如果是MASK类型)"
                },
                "confidence": 0.95,
                "comment": "映射说明"
              }
            ]
            ```

            ## 常见字段映射参考
            %s
            """;

    @Override
    public String getName() {
        return "standard_mapping_generator";
    }

    @Override
    public String getDescription() {
        return "生成数据标准映射配置，支持HL7 FHIR、国家标准等，自动分析字段语义并生成映射规则。";
    }

    @Override
    public JSONObject getInputSchema() {
        JSONObject schema = new JSONObject();
        schema.put("type", "object");

        JSONObject properties = new JSONObject();
        properties.put("source_fields", new JSONObject()
                .fluentPut("type", "array")
                .fluentPut("description", "源字段列表，每个字段包含name、type、comment")
                .fluentPut("items", new JSONObject()
                        .fluentPut("type", "object")
                        .fluentPut("properties", new JSONObject()
                                .fluentPut("name", new JSONObject().fluentPut("type", "string"))
                                .fluentPut("type", new JSONObject().fluentPut("type", "string"))
                                .fluentPut("comment", new JSONObject().fluentPut("type", "string")))));
        properties.put("target_standard", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "目标标准")
                .fluentPut("enum", List.of("FHIR", "HL7", "GBDC", "WS_T", "CUSTOM")));
        properties.put("domain", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "业务域")
                .fluentPut("enum", List.of("PATIENT", "ENCOUNTER", "OBSERVATION", "MEDICATION", "PROCEDURE", "DIAGNOSIS")));
        properties.put("datasource_id", new JSONObject()
                .fluentPut("type", "string")
                .fluentPut("description", "数据源ID(可选，用于获取码表映射)"));
        properties.put("auto_apply", new JSONObject()
                .fluentPut("type", "boolean")
                .fluentPut("description", "是否自动应用映射配置")
                .fluentPut("default", false));

        schema.put("properties", properties);
        schema.put("required", List.of("source_fields", "target_standard"));
        return schema;
    }

    @Override
    public ToolResult execute(JSONObject params, ToolContext context) {
        long startTime = System.currentTimeMillis();

        try {
            JSONArray sourceFieldsArray = params.getJSONArray("source_fields");
            String targetStandard = params.getString("target_standard");
            String domain = params.getString("domain");
            String datasourceId = params.getString("datasource_id");
            boolean autoApply = params.getBooleanValue("auto_apply");

            log.info("生成标准映射 - targetStandard: {}, domain: {}", targetStandard, domain);

            // 转换源字段列表
            List<JSONObject> sourceFields = new ArrayList<>();
            if (sourceFieldsArray != null) {
                for (int i = 0; i < sourceFieldsArray.size(); i++) {
                    sourceFields.add(sourceFieldsArray.getJSONObject(i));
                }
            }

            // 1. 获取标准字段参考
            String referenceInfo = buildReferenceInfo(targetStandard, domain);

            // 2. 获取模型配置
            ModelConfig modelConfig = modelConfigService.getDefaultModel();
            if (modelConfig == null) {
                // 使用规则匹配
                List<JSONObject> mappings = generateRuleBasedMappings(sourceFields, targetStandard, domain);
                return buildResult(mappings, targetStandard, domain, startTime, false, "使用规则匹配");
            }

            // 3. 构建提示词
            String userPrompt = buildUserPrompt(sourceFields, targetStandard, domain);

            // 4. 调用LLM生成映射
            LlmAdapter adapter = llmAdapterFactory.getAdapter(modelConfig);
            String systemPrompt = String.format(MAPPING_SYSTEM_PROMPT, referenceInfo);
            String mappingResponse = adapter.chat(systemPrompt, userPrompt, modelConfig);

            // 5. 解析映射结果
            List<JSONObject> mappings = parseMappings(mappingResponse);

            // 6. 补充映射信息
            enrichMappings(mappings, sourceFields);

            // 7. 如果自动应用，保存映射配置
            if (autoApply && datasourceId != null) {
                try {
                    masterDataRpcService.saveFieldMapping(datasourceId, mappings);
                    log.info("自动应用映射配置成功");
                } catch (Exception e) {
                    log.warn("自动应用映射配置失败: {}", e.getMessage());
                }
            }

            return buildResult(mappings, targetStandard, domain, startTime, true, "使用LLM生成");

        } catch (Exception e) {
            log.error("标准映射生成失败: {}", e.getMessage(), e);
            return ToolResult.error("标准映射生成失败: " + e.getMessage());
        }
    }

    /**
     * 构建参考信息
     */
    private String buildReferenceInfo(String standard, String domain) {
        StringBuilder info = new StringBuilder();

        // 根据标准类型提供参考
        if ("FHIR".equals(standard)) {
            info.append("FHIR标准字段参考:\n");
            if ("PATIENT".equals(domain) || domain == null) {
                info.append("- Patient.id: 患者唯一标识\n");
                info.append("- Patient.identifier: 患者标识符(身份证、医保卡等)\n");
                info.append("- Patient.name: 患者姓名\n");
                info.append("- Patient.telecom: 联系方式\n");
                info.append("- Patient.gender: 性别(male/female/other/unknown)\n");
                info.append("- Patient.birthDate: 出生日期\n");
                info.append("- Patient.address: 地址\n");
            }
        } else if ("GBDC".equals(standard)) {
            info.append("国家健康医疗大数据标准字段参考:\n");
            info.append("- AIC_ID: 居民健康卡号\n");
            info.append("- AIC_NAME: 姓名\n");
            info.append("- AIC_GENDER_CODE: 性别代码\n");
            info.append("- AIC_BIRTH_DATE: 出生日期\n");
            info.append("- AIC_IDCARD: 身份证号\n");
            info.append("- AIC_MOBILE: 手机号码\n");
        }

        return info.toString();
    }

    /**
     * 构建用户提示词
     */
    private String buildUserPrompt(List<JSONObject> sourceFields, String standard, String domain) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请将以下源字段映射到").append(standard).append("标准");
        if (domain != null) {
            prompt.append("(").append(domain).append("域)");
        }
        prompt.append("：\n\n");

        prompt.append("源字段列表:\n");
        for (JSONObject field : sourceFields) {
            prompt.append("  - ").append(field.getString("name"));
            String type = field.getString("type");
            if (type != null) {
                prompt.append(" (").append(type).append(")");
            }
            String comment = field.getString("comment");
            if (comment != null && !comment.isEmpty()) {
                prompt.append(" -- ").append(comment);
            }
            prompt.append("\n");
        }

        prompt.append("\n请输出JSON格式的映射配置：");
        return prompt.toString();
    }

    /**
     * 解析映射结果
     */
    private List<JSONObject> parseMappings(String response) {
        List<JSONObject> mappings = new ArrayList<>();

        if (response == null || response.isEmpty()) {
            return mappings;
        }

        try {
            String jsonStr = response;

            if (jsonStr.contains("```json")) {
                int start = jsonStr.indexOf("```json") + 7;
                int end = jsonStr.indexOf("```", start);
                if (end > start) {
                    jsonStr = jsonStr.substring(start, end).trim();
                }
            } else if (jsonStr.contains("```")) {
                int start = jsonStr.indexOf("```") + 3;
                int end = jsonStr.indexOf("```", start);
                if (end > start) {
                    jsonStr = jsonStr.substring(start, end).trim();
                }
            }

            if (jsonStr.startsWith("[")) {
                JSONArray array = JSONArray.parseArray(jsonStr);
                for (int i = 0; i < array.size(); i++) {
                    mappings.add(array.getJSONObject(i));
                }
            }

        } catch (Exception e) {
            log.warn("解析映射JSON失败: {}", e.getMessage());
        }

        return mappings;
    }

    /**
     * 使用规则生成映射
     */
    private List<JSONObject> generateRuleBasedMappings(List<JSONObject> sourceFields, String standard, String domain) {
        return sourceFields.stream().map(field -> {
            String sourceName = field.getString("name");
            String sourceType = field.getString("type");
            String sourceComment = field.getString("comment");

            JSONObject mapping = new JSONObject();
            mapping.put("sourceField", sourceName);
            mapping.put("sourceType", sourceType);
            mapping.put("targetField", mapToStandard(sourceName, standard));
            mapping.put("transformType", getTransformType(sourceName));
            mapping.put("confidence", calculateConfidence(sourceName, sourceComment));
            mapping.put("comment", "自动映射");

            return mapping;
        }).toList();
    }

    /**
     * 补充映射信息
     */
    private void enrichMappings(List<JSONObject> mappings, List<JSONObject> sourceFields) {
        for (JSONObject mapping : mappings) {
            String sourceField = mapping.getString("sourceField");
            if (sourceField != null) {
                // 查找源字段信息
                for (JSONObject field : sourceFields) {
                    if (sourceField.equals(field.getString("name"))) {
                        if (!mapping.containsKey("sourceType")) {
                            mapping.put("sourceType", field.getString("type"));
                        }
                        break;
                    }
                }
            }

            // 设置默认值
            if (!mapping.containsKey("transformType")) {
                mapping.put("transformType", "DIRECT");
            }
            if (!mapping.containsKey("confidence")) {
                mapping.put("confidence", 0.8);
            }
        }
    }

    private String mapToStandard(String sourceField, String standard) {
        String lower = sourceField.toLowerCase();
        return switch (lower) {
            case "patient_id", "patientid", "patient_no", "患者id" -> "Patient.id";
            case "patient_name", "patientname", "name", "姓名" -> "Patient.name";
            case "id_card", "idcard", "id_no", "身份证号", "sfzh" -> "Patient.identifier[idCard]";
            case "phone", "mobile", "telephone", "手机号", "lxdh" -> "Patient.telecom[phone]";
            case "gender", "sex", "性别", "xb" -> "Patient.gender";
            case "birth_date", "birthday", "dob", "出生日期", "csrq" -> "Patient.birthDate";
            case "address", "地址", "dz" -> "Patient.address.text";
            default -> sourceField;
        };
    }

    private String getTransformType(String field) {
        String lower = field.toLowerCase();
        if (lower.contains("gender") || lower.contains("sex") || lower.contains("性别")) {
            return "MAP";
        } else if (lower.contains("phone") || lower.contains("mobile") || lower.contains("手机")) {
            return "CONVERT";
        } else if (lower.contains("id_card") || lower.contains("idcard") || lower.contains("身份证")) {
            return "MASK";
        }
        return "DIRECT";
    }

    private double calculateConfidence(String fieldName, String comment) {
        double confidence = 0.7;
        if (comment != null && !comment.isEmpty()) {
            confidence += 0.1;
        }
        return Math.min(confidence, 0.95);
    }

    private ToolResult buildResult(List<JSONObject> mappings, String standard, String domain,
                                    long startTime, boolean useLLM, String method) {
        JSONObject result = new JSONObject();
        result.put("targetStandard", standard);
        result.put("domain", domain);
        result.put("mappings", mappings);
        result.put("mappingCount", mappings.size());
        result.put("generationMethod", method);
        result.put("useLLM", useLLM);
        result.put("executionTime", System.currentTimeMillis() - startTime);

        // 计算平均置信度
        double avgConfidence = mappings.stream()
                .mapToDouble(m -> m.getDoubleValue("confidence", 0.8))
                .average()
                .orElse(0.8);
        result.put("averageConfidence", avgConfidence);

        log.info("标准映射生成成功 - 生成 {} 条映射，平均置信度 {:.2f}，耗时 {}ms",
                mappings.size(), avgConfidence, result.getLong("executionTime"));

        return ToolResult.success(result);
    }
}