package com.example.esm.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 微前端应用注册表接口
 * Host App 通过此接口获取当前可用的微前端列表
 */
@RestController
@RequestMapping("/api/apps")
@CrossOrigin(origins = "*")
public class AppRegistryController {

    private final List<Map<String, Object>> appRegistry = List.of(
        Map.of(
            "name", "user-module",
            "displayName", "用户管理",
            "entry", "/microapp/UserModule/main.js",
            "containerId", "user-module-container",
            "defaultRoute", "/users",
            "version", "1.0.0"
        ),
        Map.of(
            "name", "product-module",
            "displayName", "商品管理",
            "entry", "/microapp/ProductModule/main.js",
            "containerId", "product-module-container",
            "defaultRoute", "/products",
            "version", "1.0.0"
        )
    );

    @GetMapping
    public List<Map<String, Object>> getApps() {
        return appRegistry;
    }

    @GetMapping("/{name}")
    public Map<String, Object> getApp(@PathVariable String name) {
        return appRegistry.stream()
                .filter(app -> name.equals(app.get("name")))
                .findFirst()
                .orElse(Map.of("error", "App not found: " + name));
    }
}
