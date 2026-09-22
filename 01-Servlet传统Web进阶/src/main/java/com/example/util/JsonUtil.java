package com.example.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * JSON工具类
 */
public class JsonUtil {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * 对象转JSON字符串
     */
    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    /**
     * JSON字符串转对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }
}
