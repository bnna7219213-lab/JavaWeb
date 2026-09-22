package com.example.esm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 原生 ESM 微前端进阶 - Spring Boot 启动类
 * 完整微前端架构：应用注册表 + 事件总线 + 生命周期管理 + 多业务子应用
 * 端口: 8094
 */
@SpringBootApplication
public class EsmMicroFrontendAdvancedApplication {
    public static void main(String[] args) {
        SpringApplication.run(EsmMicroFrontendAdvancedApplication.class, args);
    }
}
