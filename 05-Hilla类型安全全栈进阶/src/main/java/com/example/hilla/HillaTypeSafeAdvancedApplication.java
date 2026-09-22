package com.example.hilla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hilla 类型安全全栈 - 进阶版启动类
 *
 * 架构特点：
 * - 多个 Endpoint 类（UserEndpoint + OrderEndpoint）
 * - Service 层业务逻辑分离
 * - 全局异常处理（统一 JSON 错误响应）
 * - 完整 CRUD REST API
 * - 自动生成 TypeScript 客户端（含类型安全表单验证）
 */
@SpringBootApplication
public class HillaTypeSafeAdvancedApplication {

    public static void main(String[] args) {
        SpringApplication.run(HillaTypeSafeAdvancedApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  Hilla 类型安全全栈进阶版已启动");
        System.out.println("  访问: http://localhost:8086/");
        System.out.println("===========================================");
    }
}
