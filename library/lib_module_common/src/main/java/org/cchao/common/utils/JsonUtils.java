package org.cchao.common.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
public class JsonUtils {

    private static ObjectMapper INSTANCE = new ObjectMapper();

    private JsonUtils() {
    }

    public static String toString(Object obj) {
        try {
            return INSTANCE.writeValueAsString(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return INSTANCE.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> toList(String json, Class<T> clazz) {
        try {
            JavaType javaType = getCollectionType(ArrayList.class, clazz);
            return (List<T>) INSTANCE.readValue(json, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return INSTANCE.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static Map<String, Object> toMap(String json) {
        try {
            return INSTANCE.readValue(json, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}
