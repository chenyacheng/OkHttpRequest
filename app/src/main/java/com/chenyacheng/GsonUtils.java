package com.chenyacheng;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Gson转换
 *
 * @author chenyacheng
 * @date 2019/12/16
 */
public class GsonUtils {

    private GsonUtils() {
    }

    /**
     * 对象实体json或分页的实体json，避免有空格或是空字符串时报错
     *
     * @param o    o
     * @param type new TypeToken<List<T>>() {}.getType()
     * @param <T>  泛型
     * @return 对象实体或分页的实体
     */
    public static <T> T removeSpaceFromJson(Object o, Class<T> type) {
        return fromJson(new Gson().toJson(o), type);
    }

    /**
     * 对象实体json或分页的实体json
     *
     * @param json json
     * @param type new TypeToken<List<T>>() {}.getType()
     * @param <T>  泛型
     * @return 对象实体或分页的实体
     */
    public static <T> T fromJson(String json, Class<T> type) {
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

    /**
     * 对象实体列表json
     *
     * @param json json
     * @param type new TypeToken<List<T>>() {}.getType()
     * @param <T>  泛型
     * @return 对象实体列表
     */
    public static <T> List<T> listFromJson(String json, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }
}
