package cn.healthcaredaas.datasphere.core.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * 密码字段反序列化器
 * 直接返回原始值
 *
 * @author chenpan
 */
public class PasswordObjectDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return p.getValueAsString();
    }
}
