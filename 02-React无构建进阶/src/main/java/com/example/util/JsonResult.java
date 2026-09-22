package com.example.util;

import java.util.HashMap;
import java.util.Map;

public class JsonResult {
    public static Map<String, Object> success(String msg, Object data) {
        Map<String, Object> r = new HashMap<>();
        r.put("code", 200); r.put("msg", msg); r.put("data", data);
        return r;
    }
    public static Map<String, Object> error(int code, String msg) {
        Map<String, Object> r = new HashMap<>();
        r.put("code", code); r.put("msg", msg);
        return r;
    }
}
