package org.cchao.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.Map;

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
public class FastJsonUtils {

    private FastJsonUtils() {
    }

    public static String toString(Object object) {
        return JSON.toJSONString(object);
    }

    public static <T> T toObject(String json, Class<T> classOfT) {
        return JSON.parseObject(json, classOfT);
    }

    public static Map<String, Object> toMap(String json) {
        return JSON.parseObject(json, new TypeReference<Map<String, Object>>() {
        });
    }
}
