package com.example.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一响应结果封装
 */
public class JsonResult {

    /**
     * 成功响应（无数据）
     */
    public static Map<String, Object> success() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "操作成功");
        return result;
    }

    /**
     * 成功响应（带数据）
     */
    public static Map<String, Object> success(String msg, Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", msg);
        result.put("data", data);
        return result;
    }

    /**
     * 失败响应
     */
    public static Map<String, Object> error(int code, String msg) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }
}
