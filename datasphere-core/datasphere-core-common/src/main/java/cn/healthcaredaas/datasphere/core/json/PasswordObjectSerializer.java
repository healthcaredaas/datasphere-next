package cn.healthcaredaas.datasphere.core.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 密码字段序列化器
 * 将密码字段序列化为掩码形式
 *
 * @author chenpan
 */
public class PasswordObjectSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null || value.isEmpty()) {
            gen.writeString("");
            return;
        }
        // 密码脱敏显示
        gen.writeString("******");
    }
}
