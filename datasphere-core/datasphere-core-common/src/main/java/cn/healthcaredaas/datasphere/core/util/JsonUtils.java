package cn.healthcaredaas.datasphere.core.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

/**
 * JSON工具类
 *
 * @author chenpan
 */
public class JsonUtils {

    /**
     * 对象转JSON字符串
     */
    public static String toJsonString(Object obj) {
        if (obj == null) {
            return "{}";
        }
        return JSON.toJSONString(obj);
    }

    /**
     * 对象转JSON字符串（别名方法）
     */
    public static String toJson(Object obj) {
        return toJsonString(obj);
    }

    /**
     * JSON字符串转对象
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return JSON.parseObject(json, clazz);
    }

    /**
     * 创建JSONObject
     */
    public static JSONObject createObject() {
        return new JSONObject();
    }

    /**
     * 合并两个JSON对象
     */
    public static JSONObject merge(JSONObject source, JSONObject target) {
        JSONObject result = new JSONObject();
        if (source != null) {
            result.putAll(source);
        }
        if (target != null) {
            result.putAll(target);
        }
        return result;
    }
}
