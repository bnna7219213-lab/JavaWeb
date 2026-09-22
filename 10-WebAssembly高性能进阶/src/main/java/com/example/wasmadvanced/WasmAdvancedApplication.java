package com.example.wasmadvanced;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * WebAssembly 高性能计算演示 - 进阶版
 * 功能：多WASM模块管理、Web Worker集成、完整性能对比图表
 * Spring Boot 提供静态资源服务和高级计算任务 API
 */
@SpringBootApplication
public class WasmAdvancedApplication {
    public static void main(String[] args) {
        SpringApplication.run(WasmAdvancedApplication.class, args);
        System.out.println("=== WebAssembly Demo Advanced started at http://localhost:8098 ===");
    }
}
