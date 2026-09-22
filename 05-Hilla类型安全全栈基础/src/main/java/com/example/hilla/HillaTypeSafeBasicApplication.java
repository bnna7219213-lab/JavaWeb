package com.example.hilla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hilla 类型安全全栈 - 基础版启动类
 *
 * 架构说明：
 * - 后端使用 Spring Boot 3，用 @RestController 模拟 Hilla 的 @Endpoint 行为
 * - 预生成的 TypeScript 客户端文件（frontend/ 目录）模拟 Hilla 自动生成效果
 * - 前端页面演示类型安全的 API 调用
 */
@SpringBootApplication
public class HillaTypeSafeBasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(HillaTypeSafeBasicApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  Hilla 类型安全全栈基础版已启动");
        System.out.println("  访问: http://localhost:8085/");
        System.out.println("===========================================");
    }
}
