package cn.healthcaredaas.datasphere.core.aop;

import cn.healthcaredaas.datasphere.core.annotation.Desensitize;
import cn.healthcaredaas.datasphere.core.common.RestResult;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 脱敏序列化器
 *
 * @author chenpan
 */
@Slf4j
public class DesensitizeSerializer extends StdSerializer<String> implements ContextualSerializer {

    private static final long serialVersionUID = 1L;

    private Desensitize.DesensitizeType type;
    private String pattern;
    private String replacement;

    public DesensitizeSerializer() {
        super(String.class);
    }

    public DesensitizeSerializer(Desensitize.DesensitizeType type, String pattern, String replacement) {
        super(String.class);
        this.type = type;
        this.pattern = pattern;
        this.replacement = replacement;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        String desensitizedValue = desensitize(value);
        gen.writeString(desensitizedValue);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        Desensitize annotation = property.getAnnotation(Desensitize.class);
        if (annotation != null) {
            return new DesensitizeSerializer(annotation.type(), annotation.pattern(), annotation.replacement());
        }
        return this;
    }

    /**
     * 执行脱敏
     */
    private String desensitize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        try {
            switch (type) {
                case PHONE:
                    return desensitizePhone(value);
                case EMAIL:
                    return desensitizeEmail(value);
                case ID_CARD:
                    return desensitizeIdCard(value);
                case BANK_CARD:
                    return desensitizeBankCard(value);
                case NAME:
                    return desensitizeName(value);
                case ADDRESS:
                    return desensitizeAddress(value);
                case CUSTOM:
                    return desensitizeCustom(value);
                default:
                    return value;
            }
        } catch (Exception e) {
            log.warn("Desensitize failed: {}", e.getMessage());
            return value;
        }
    }

    /**
     * 手机号脱敏
     */
    private String desensitizePhone(String phone) {
        if (phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 邮箱脱敏
     */
    private String desensitizeEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex < 0) {
            return email;
        }
        String prefix = email.substring(0, atIndex);
        String suffix = email.substring(atIndex);

        if (prefix.length() <= 2) {
            return "*" + suffix;
        }
        return prefix.substring(0, 2) + "***" + suffix;
    }

    /**
     * 身份证号脱敏
     */
    private String desensitizeIdCard(String idCard) {
        if (idCard.length() != 18 && idCard.length() != 15) {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(idCard.length() - 4);
    }

    /**
     * 银行卡号脱敏
     */
    private String desensitizeBankCard(String bankCard) {
        if (bankCard.length() < 8) {
            return bankCard;
        }
        return bankCard.substring(0, 4) + " **** **** " + bankCard.substring(bankCard.length() - 4);
    }

    /**
     * 姓名脱敏
     */
    private String desensitizeName(String name) {
        if (name.length() <= 1) {
            return "*";
        }
        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }
        return name.charAt(0) + "*" + name.substring(name.length() - 1);
    }

    /**
     * 地址脱敏
     */
    private String desensitizeAddress(String address) {
        if (address.length() <= 8) {
            return address.substring(0, 3) + "***";
        }
        return address.substring(0, 6) + "******" + address.substring(address.length() - 6);
    }

    /**
     * 自定义脱敏
     */
    private String desensitizeCustom(String value) {
        if (pattern == null || pattern.isEmpty()) {
            return value;
        }
        try {
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(value);
            return matcher.replaceAll(replacement);
        } catch (Exception e) {
            log.warn("Custom desensitize failed: {}", e.getMessage());
            return value;
        }
    }
}
