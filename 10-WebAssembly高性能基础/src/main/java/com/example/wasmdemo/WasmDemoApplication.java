package com.example.wasmdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * WebAssembly 高性能计算演示 - 基础版
 * Spring Boot 提供静态资源服务和计算任务 API
 */
@SpringBootApplication
public class WasmDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(WasmDemoApplication.class, args);
        System.out.println("=== WebAssembly Demo Basic started at http://localhost:8097 ===");
    }
}
