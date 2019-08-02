package org.cchao.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
public class GsonUtils {


    private static Gson gson = new Gson();

    private static GsonBuilder gb = new GsonBuilder();

    private GsonUtils() {
    }

    public static String toString(Object object) {
        gb.disableHtmlEscaping();
        return gb.create().toJson(object);
    }

    public static <T> T toObject(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> List<T> toList(String json, Class<T> classOfT) {
        return gson.fromJson(json, new ListOfJson<>(classOfT));
    }

    private static class ListOfJson<T> implements ParameterizedType {

        private Class<?> wrapped;

        ListOfJson(Class<T> wrapper) {
            this.wrapped = wrapper;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{wrapped};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
